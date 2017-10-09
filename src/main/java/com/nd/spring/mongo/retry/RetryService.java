/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.spring.mongo.retry;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.nd.spring.mongo.retry.message.RetryMessage;
import com.nd.spring.mongo.retry.task.RetryTaskService;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 * @param <T>
 */
public class RetryService<T extends RetryMessage<?>> implements InitializingBean
{
    private static final int DEFAULT_MAX_ATTEMPTS = 10;
    
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    
    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
    
    private RetryTaskService<T> taskService;
    
    /**
     * @param mongoTemplate
     * @param document
     */
    public RetryService()
    {
        this(null);
    }
    
    /**
     * @param mongoTemplate
     * @param document
     * @param collection
     */
    public RetryService(MongoTemplate mongoTemplate, Class<T> document, String collection)
    {
        this(new RetryTaskService<T>(mongoTemplate, document, collection));
    }
    
    /**
     * @param mongoTemplate
     * @param document
     * @param collectionName
     */
    public RetryService(RetryTaskService<T> taskService)
    {
        this.taskService = taskService;
    }

    /**
     * @return the maxAttempts
     */
    public int getMaxAttempts()
    {
        return maxAttempts;
    }

    /**
     * @param maxAttempts the maxAttempts to set
     */
    public void setMaxAttempts(int maxAttempts)
    {
        this.maxAttempts = maxAttempts;
    }

    /**
     * @return the taskService
     */
    public RetryTaskService<T> getTaskService()
    {
        return taskService;
    }

    /**
     * @param taskService the taskService to set
     */
    public void setTaskService(RetryTaskService<T> taskService)
    {
        this.taskService = taskService;
    }

    /**
     * @param message
     */
    public void remove(T message)
    {
        taskService.remove(message);
    }
    
    /**
     * @param message
     */
    public void save(T message)
    {
        taskService.save(message);
    }
    
    /**
     * Resume persist task
     */
    private void initialize()
    {
        if(initialized.compareAndSet(false, true))
        {
            initializeTasks();
        }
    }

    /**
     * Resume persist task
     */
    private void initializeTasks()
    {
        for(int i = 0; i <= maxAttempts; i++)
        {
            taskService.addCollectionTask(i);
        }
    }

    /**
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        initialize();
    }
}
