/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.component.java.mongo.retry.backoff;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public interface RetryBackOff
{
    public static final long STOP = 0;
    
    RetryBackOff next();
    
    int attempts();
    
    long attemptTime();
}