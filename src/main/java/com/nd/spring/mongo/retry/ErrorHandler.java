/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.spring.mongo.retry;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public interface ErrorHandler<T>
{
    /**
     * Handle the given error, possibly rethrowing it as a fatal exception.
     */
    void handleError(T message, Throwable e);
}
