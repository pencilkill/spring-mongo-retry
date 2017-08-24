/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.component.java.mongo.retry.task;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.nd.component.java.mongo.retry.RetryConsumer;
import com.nd.component.java.mongo.retry.message.RetryMessage;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class RetryTaskService<T extends RetryMessage<?>>
{
    private static final String DEFAULT_COLLECTION = "retry_task";

    private static final Sort QUERY_SORT = new Sort(Direction.ASC, "update_at");
    
    private static final DBObject QUERY_INDEX = new BasicDBObject("nextAttemptTime", 1);
    
    private static final String QUERY_EXPRESSION = "{'nextAttemptTime':{'$gt':0,'$lte':T(java.lang.System).currentTimeMillis()}}";
    
    private static final ConcurrentMap<Integer, ScheduledTask> task = new ConcurrentHashMap<Integer, ScheduledTask>();

    private String collection = DEFAULT_COLLECTION;

    private long fixedRate = 15000;

    private long initialDelay = 60000;

    private int queryLimit = 100;

    private MongoTemplate mongoTemplate;

    private Class<T> document;
    
    private RetryConsumer<T> consumer;
    
    private ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
    
    public RetryTaskService(MongoTemplate mongoTemplate, Class<T> document)
    {
        this(mongoTemplate, document, DEFAULT_COLLECTION);
    }
    
    /**
     * @param mongoTemplate
     * @param document
     * @param collection
     */
    public RetryTaskService(MongoTemplate mongoTemplate, Class<T> document, String collection)
    {
        this.mongoTemplate = mongoTemplate;
        this.document = document;
        this.collection = collection;
    }

    /**
     * @param message
     */
    private void addCollectionTask(T message)
    {
        addCollectionTask(message.getAttempts());
    }
    
    /**
     * @param index
     */
    public void addCollectionTask(int index)
    {
        if(task.containsKey(index) == false)
        {
            String collection = collectionName(index);
            
            addCollection(collection);
            
            task.put(index, registrar.scheduleFixedRateTask(new IntervalTask(createCollectionTask(collection), fixedRate, initialDelay)));
        }
    }
    
    /**
     * @param index
     * @return
     */
    public ScheduledTask getCollectionTask(int index)
    {
        return task.get(index);
    }
    
    /**
     * @param collection
     * @return
     */
    private Runnable createCollectionTask(String collection)
    {
        Pageable pageable = new PageRequest(0, queryLimit, QUERY_SORT);
        
        return new RetryTask<T>(mongoTemplate, document, collection, consumer, QUERY_EXPRESSION, pageable);
    }

    /**
     * @param index
     * @return
     */
    private String collectionName(int index)
    {
        return String.format("%s_%s", collection, index);
    }

    /**
     * @param collection
     */
    private void addCollection(String collection)
    {
        if(mongoTemplate.collectionExists(collection))
        {
            return;
        }
        
        mongoTemplate.createCollection(collection);
        
        addCollectionIndex(collection);
    }

    /**
     * @param collection
     */
    private void addCollectionIndex(String collection)
    {
        IndexOperations indexOps = mongoTemplate.indexOps(collection);
        
        indexOps.ensureIndex(new CompoundIndexDefinition(QUERY_INDEX).background());
    }

    /**
     * @param message
     */
    public void save(T message)
    {
        mongoTemplate.save(message, collectionName(message.getAttempts()));
        
        addCollectionTask(message);
    }
    
    /**
     * @param message
     */
    public void remove(T message)
    {
        mongoTemplate.remove(message, collectionName(message.getAttempts()));
    }
}
