/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.spring.mongo.retry.backoff;

import org.springframework.util.Assert;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class RetryExponentialBackOff implements RetryBackOff
{
    private int attempts;
    
    private int maxAttempts;
    
    private long interval;
    
    private double multiplier;
    
    private long firstAttemptTime;
    
    public RetryExponentialBackOff()
    {
        //
    }

    /**
     * Exponential failover which includes the first execution
     *
     * @param maxAttempts
     * @param interval
     * @param multiplier
     * @param firstAttemptTime
     */
    public RetryExponentialBackOff(long firstAttemptTime, int maxAttempts, long interval, double multiplier)
    {
        this(firstAttemptTime, maxAttempts, maxAttempts, interval, multiplier);
    }
    
    /**
     * @param attempts
     * @param maxAttempts
     * @param interval
     * @param multiplier
     * @param firstAttemptTime
     */
    public RetryExponentialBackOff(long firstAttemptTime, int attempts, int maxAttempts, long interval, double multiplier)
    {
        Assert.isTrue(attempts >= 0, "Attempts must not be less than 0");
        Assert.isTrue(maxAttempts >= attempts, "Attempts must not be greater than maxAttempts");
        Assert.isTrue(multiplier > 1.0, "Multiplier must be greater than 1.0");
        
        this.firstAttemptTime = firstAttemptTime;
        this.attempts = attempts;
        this.maxAttempts = maxAttempts;
        this.interval = interval;
        this.multiplier = multiplier;
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
     * @return the interval
     */
    public long getInterval()
    {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(long interval)
    {
        this.interval = interval;
    }

    /**
     * @return the multiplier
     */
    public double getMultiplier()
    {
        return multiplier;
    }

    /**
     * @param multiplier the multiplier to set
     */
    public void setMultiplier(double multiplier)
    {
        this.multiplier = multiplier;
    }

    /**
     * @return the firstAttemptTime
     */
    public long getFirstAttemptTime()
    {
        return firstAttemptTime;
    }

    /**
     * @param firstAttemptTime the firstAttemptTime to set
     */
    public void setFirstAttemptTime(long firstAttemptTime)
    {
        this.firstAttemptTime = firstAttemptTime;
    }

    /**
     * @return
     * @see com.nd.spring.mongo.retry.backoff.RetryBackOff#next()
     */
    @Override
    public RetryBackOff next()
    {
        return new RetryExponentialBackOff(firstAttemptTime, Math.max(0, attempts - 1), maxAttempts, interval, multiplier);
    }

    /**
     * @return
     * @see com.nd.spring.mongo.retry.backoff.RetryBackOff#nextAttempts()
     */
    @Override
    public int attempts()
    {
        return attempts;
    }
    
    @Override
    public long attemptTime()
    {
        if(attempts > 0)
        {
            return firstAttemptTime + (long)(interval * Math.pow(Math.max(1.0, multiplier), Math.max(0, maxAttempts - attempts)));
        }
        
        return STOP;
    }
}
