
package com.isaccanedo.examples.guice.binding;

import com.isaccanedo.examples.guice.aop.MessageLogger;
import com.isaccanedo.examples.guice.aop.MessageSentLoggable;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 *
 * @author isaccanedo
 */
public class AOPModule extends AbstractModule {

    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(),
                Matchers.annotatedWith(MessageSentLoggable.class),
                new MessageLogger()
        );
    }

}
