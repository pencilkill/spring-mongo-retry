/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.spring.mongo.retry.task;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.mongodb.BasicDBObject;
import com.nd.spring.mongo.retry.RetryConsumer;
import com.nd.spring.mongo.retry.message.RetryMessage;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 * @param <T>
 */
public class RetryTask<T extends RetryMessage<?>> implements Runnable
{
    private static final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true,true));
    
    private MongoTemplate mongoTemplate;

    private Class<T> document;
    
    private String collection;

    private int attempt;

    private RetryConsumer<T> consumer;
    
    private String criteria;
    
    private Pageable pageable;

    /**
     * @param mongoTemplate
     * @param document
     * @param collection
     * @param consumer
     * @param criteria SPEL expression BasicDBObject string value
     * @param pageable
     */
    public RetryTask(MongoTemplate mongoTemplate, Class<T> document, String collection, int attempt, RetryConsumer<T> consumer, String criteria, Pageable pageable)
    {
        this.mongoTemplate = mongoTemplate;
        this.document = document;
        this.collection = collection;
        this.attempt = attempt;
        this.consumer = consumer;
        this.criteria = criteria;
        this.pageable = pageable;
    }

    /**
     * 单次查询完成任务后，间隔30秒查询一次重试时间大于0并且小于当前时间的任务.
     */
    @Override
    public void run()
    {
        consumer.handler(mongoTemplate.find(new BasicQuery(parser.parseExpression(criteria).getValue(this, BasicDBObject.class)).with(pageable), document, collection));
    }

    /**
     * @return the mongoTemplate
     */
    public MongoTemplate getMongoTemplate()
    {
        return mongoTemplate;
    }

    /**
     * @param mongoTemplate the mongoTemplate to set
     */
    public void setMongoTemplate(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * @return the document
     */
    public Class<T> getDocument()
    {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(Class<T> document)
    {
        this.document = document;
    }

    /**
     * @return the collection
     */
    public String getCollection()
    {
        return collection;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(String collection)
    {
        this.collection = collection;
    }

    /**
     * @return the attempt
     */
    public int getAttempt()
    {
        return attempt;
    }

    /**
     * @param attempt the attempt to set
     */
    public void setAttempt(int attempt)
    {
        this.attempt = attempt;
    }

    /**
     * @return the consumer
     */
    public RetryConsumer<T> getConsumer()
    {
        return consumer;
    }

    /**
     * @param consumer the consumer to set
     */
    public void setConsumer(RetryConsumer<T> consumer)
    {
        this.consumer = consumer;
    }

    /**
     * @return the criteria
     */
    public String getCriteria()
    {
        return criteria;
    }

    /**
     * @param criteria the criteria to set
     */
    public void setCriteria(String criteria)
    {
        this.criteria = criteria;
    }

    /**
     * @return the pageable
     */
    public Pageable getPageable()
    {
        return pageable;
    }

    /**
     * @param pageable the pageable to set
     */
    public void setPageable(Pageable pageable)
    {
        this.pageable = pageable;
    }
}
