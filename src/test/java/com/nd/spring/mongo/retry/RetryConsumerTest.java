/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.spring.mongo.retry;

import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.nd.spring.mongo.retry.backoff.RetryExponentialBackOff;
import com.nd.spring.mongo.retry.message.RetryDBObject;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class RetryConsumerTest extends AppTest
{
    private static final Logger logger = LoggerFactory.getLogger(RetryConsumerTest.class);
    
    @Autowired
    private ConsumerService consumerService;

    @Test
    public void test() throws InterruptedException
    {
        consumerService.handler(Arrays.asList(new RetryDBObject(new BasicDBObject("test", true), new RetryExponentialBackOff(System.currentTimeMillis(), 10, 5000, 2.0))));
        
        Thread.sleep(60 * 1000);
        
        logger.debug("keep");
    }

}
