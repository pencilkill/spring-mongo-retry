/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.spring.mongo.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.nd.spring.mongo.retry.message.RetryDBObject;
import com.nd.spring.mongo.retry.task.RetryTaskService;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
@Service
public class ConsumerService extends AbstractRetryConsumer<RetryDBObject>
{
    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param message
     * @see com.nd.spring.mongo.retry.RetryConsumer#handler(com.nd.spring.mongo.retry.message.RetryMessage)
     */
    @Override
    public void handler(RetryDBObject message)
    {
        logger.debug(message.getPayload().toString());
    }

    /**
     * @return
     * @see com.nd.spring.mongo.retry.AbstractRetryConsumer#createRetryService()
     */
    @Override
    protected RetryService<RetryDBObject> createRetryService()
    {
        RetryTaskService<RetryDBObject> retryTaskService = new RetryTaskService<RetryDBObject>(mongoTemplate, RetryDBObject.class);
        
        retryTaskService.setConsumer(this);
        
        return new RetryService<RetryDBObject>(retryTaskService);
    }

}
