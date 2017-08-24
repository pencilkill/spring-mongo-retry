/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.component.java.mongo.retry.message;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import com.nd.component.java.mongo.retry.backoff.RetryBackOff;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 * @param <T>
 */
public abstract class RetryMessage<T> implements Persistable<String>
{
    /**
     * serialVersionUID long
     */
    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
    
    private T payload;
    
    private RetryBackOff backOff;
    
    private int attempts;
    
    private long nextAttemptTime;
    
    private Date updateAt;
    
    public RetryMessage()
    {
        this(null, null);
    }
    
    /**
     * @param payload
     * @param method
     * @param maxAttempts
     * @param interval
     * @param multiplier
     */
    public RetryMessage(T payload, RetryBackOff backOff)
    {
        this.payload = payload;
        this.backOff = backOff;
    }

    /**
     * @return
     * @see org.springframework.data.domain.Persistable#isNew()
     */
    @Override
    public boolean isNew()
    {
        return id == null;
    }

    /**
     * @return the id
     */
    @Override
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the payload
     */
    public T getPayload()
    {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(T payload)
    {
        this.payload = payload;
    }

    /**
     * @return the attempts
     */
    public int getAttempts()
    {
        return attempts;
    }

    /**
     * @param attempts the attempts to set
     */
    public void setAttempts(int attempts)
    {
        this.attempts = attempts;
    }

    /**
     * @return the nextAttemptTime
     */
    public long getNextAttemptTime()
    {
        return nextAttemptTime;
    }

    /**
     * @param nextAttemptTime the nextAttemptTime to set
     */
    public void setNextAttemptTime(long nextAttemptTime)
    {
        this.nextAttemptTime = nextAttemptTime;
    }

    /**
     * @return the updateAt
     */
    public Date getUpdateAt()
    {
        return updateAt;
    }

    /**
     * @param updateAt the updateAt to set
     */
    public void setUpdateAt(Date updateAt)
    {
        this.updateAt = updateAt;
    }

    public void next()
    {
        RetryBackOff next = backOff.next();
        
        attempts = next.attempts();
        nextAttemptTime = next.attemptTime();
    }
}