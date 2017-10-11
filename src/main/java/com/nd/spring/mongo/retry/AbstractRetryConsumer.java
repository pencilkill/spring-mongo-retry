/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.spring.mongo.retry;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nd.spring.mongo.retry.backoff.RetryExponentialBackOff;
import com.nd.spring.mongo.retry.message.RetryMessage;

/**
 * Exponential mongodb retry task, for example:
 *
 * <pre>
public class SomeThingRetry extends AbstractRetryConsumer{@literal <}RetryDBObject{@literal >}
{
    public void send(BasicDBObject payload)
    {
        handler(Arrays.asList(new RetryDBObject(payload, new RetryExponentialBackOff(System.currentTimeMillis(), 10, 15000, 2.0))));
    }
    
    {@literal @}Override
    public void handler(RetryDBObject message)
    {
        // Do something ...
    }
    
    {@literal @}Override
    protected RetryService{@literal <}RetryDBObject{@literal >} createRetryService()
    {
        return new RetryService{@literal <}RetryDBObject{@literal >}(mongoTemplate, RetryDBObject.class, "retry_payload");
    }
}
 * </pre>
 *
 * @see {@link RetryService}, {@link RetryMessage}, {@link RetryExponentialBackOff}
 *
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 * @param <T>
 */
public abstract class AbstractRetryConsumer<T extends RetryMessage<?>> implements RetryConsumer<T>, InitializingBean
{
    private RetryService<T> retry;

    private ExecutorService executor;
    
    private ErrorHandler<T> errorHander;
    
    public AbstractRetryConsumer()
    {
        this(null);
    }
    
    public AbstractRetryConsumer(RetryService<T> retryService)
    {
        this(retryService, null);
    }
    
    public AbstractRetryConsumer(RetryService<T> retryService, ErrorHandler<T> errorHander)
    {
        this(retryService, errorHander, Executors.newFixedThreadPool(10, new ThreadFactoryBuilder().setDaemon(true).build()));
    }
    
    public AbstractRetryConsumer(RetryService<T> retryService, ErrorHandler<T> errorHander, ExecutorService executorService)
    {
        Assert.notNull(executorService);
        
        this.retry = retryService;
        this.executor = executorService;
    }

    /**
     * @return the retry
     */
    public RetryService<T> getRetry()
    {
        return retry;
    }

    /**
     * @param retry the retry to set
     */
    public void setRetry(RetryService<T> retry)
    {
        this.retry = retry;
    }

    /**
     * @return the executor
     */
    public ExecutorService getExecutor()
    {
        return executor;
    }

    /**
     * @param executor the executor to set
     */
    public void setExecutor(ExecutorService executor)
    {
        this.executor = executor;
    }

    /**
     * @return the errorHander
     */
    public ErrorHandler<T> getErrorHander()
    {
        return errorHander;
    }

    /**
     * @param errorHander the errorHander to set
     */
    public void setErrorHander(ErrorHandler<T> errorHander)
    {
        this.errorHander = errorHander;
    }

    @Override
    public void handler(Collection<T> messages)
    {
        if(CollectionUtils.isEmpty(messages))
        {
            return;
        }
        
        for (final T message : messages)
        {
            if(message.getNextAttemptTime() > System.currentTimeMillis())
            {
                if(retry != null)
                {
                    retry.save(message);
                }
                
                continue;
            }
            
            executor.execute(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        handler(message);
                        
                        retry.remove(message);
                    }
                    catch (Exception e)
                    {
                        if(errorHander != null)
                        {
                            errorHander.handleError(message, e);
                        }
                        
                        message.next();
                        
                        if(retry != null)
                        {
                            retry.save(message);
                        }
                    }
                }
            });
        }
    }
    
    protected abstract RetryService<T> createRetryService();

    /**
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        retry = createRetryService();
        
        if(retry != null)
        {
            retry.afterPropertiesSet();
        }
    }
}
