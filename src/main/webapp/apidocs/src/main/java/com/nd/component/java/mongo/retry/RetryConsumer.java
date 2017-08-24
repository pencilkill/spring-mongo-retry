/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.component.java.mongo.retry;

import java.util.Collection;

import com.nd.component.java.mongo.retry.message.RetryMessage;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 * @param <T>
 */
public interface RetryConsumer<T extends RetryMessage<?>>
{
    /**
     * @param message
     */
    void handler(T message);
    
    /**
     * @param messages
     */
    void handler(Collection<T> messages);
}