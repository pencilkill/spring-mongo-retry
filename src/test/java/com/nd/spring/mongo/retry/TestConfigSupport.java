/**
 * @copyright Copyright 1999-2016 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.spring.mongo.retry;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
@Configuration
@ComponentScan("com.nd.spring.mongo.retry")
public class TestConfigSupport
{
    public @Bean MongoTemplate mongoTemplate() throws UnknownHostException
    {
        return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(new MongoClientURI("mongodb://dev_mdb_im_friend:P9bPXBrfQuGN@172.24.133.23:34001,172.24.133.25:34001,172.24.133.24:34001/dev_mdb_im_friend?autoConnectRetry=true")), "dev_mdb_im_friend"));
    }
    
    
}
