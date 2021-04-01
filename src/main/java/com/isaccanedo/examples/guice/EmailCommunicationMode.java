
package com.isaccanedo.examples.guice;

import com.isaccanedo.examples.guice.aop.MessageSentLoggable;
import com.isaccanedo.examples.guice.constant.CommunicationModel;

/**
 *
 * @author isaccanedo
 */
public class EmailCommunicationMode implements CommunicationMode {

    @Override
    public CommunicationModel getMode() {
        return CommunicationModel.EMAIL;
    }

    @Override
    @MessageSentLoggable
    public boolean sendMessage(String Message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
