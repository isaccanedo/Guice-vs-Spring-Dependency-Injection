
package com.isaccanedo.examples.guice;

import com.isaccanedo.examples.guice.aop.MessageSentLoggable;
import com.isaccanedo.examples.guice.constant.CommunicationModel;
import com.google.inject.Inject;
import java.util.logging.Logger;

/**
 *
 * @author isaccanedo
 */
public class SMSCommunicationMode implements CommunicationMode {
    
    @Inject
    private Logger logger;

    @Override
    public CommunicationModel getMode() {
        return CommunicationModel.SMS;
    }

    @Override
    @MessageSentLoggable
    public boolean sendMessage(String message) {
        logger.info("SMS message sent");
        return true;
    }

}
