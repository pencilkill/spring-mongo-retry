/**
 * @copyright Copyright 1999-2016 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.spring.mongo.retry;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
@ContextConfiguration(classes = {TestConfigSupport.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Ignore
public abstract class AppTest
{
    static
    {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.DEBUG);
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{MM-dd HH:mm:ss:SSS} %-1p %c{36}(%L) - %m%n")));
    }

    @Before
    public void setUp() throws Exception
    {
        new TestContextManager(getClass()).prepareTestInstance(this);
    }
}
