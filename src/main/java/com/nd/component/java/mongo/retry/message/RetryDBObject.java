/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.component.java.mongo.retry.message;

import com.mongodb.BasicDBObject;
import com.nd.component.java.mongo.retry.backoff.RetryBackOff;

/**
 * Simple RetryMessage with payload {@link BasicDBObject}
 *
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class RetryDBObject extends RetryMessage<BasicDBObject>
{

    /**
     * serialVersionUID long
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public RetryDBObject()
    {
        this(null, null);
    }

    /**
     * @param payload
     * @param backOff
     */
    public RetryDBObject(BasicDBObject payload, RetryBackOff backOff)
    {
        super(payload, backOff);
    }
}
