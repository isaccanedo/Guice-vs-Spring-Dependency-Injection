
package com.isaccanedo.examples.guice;

import com.isaccanedo.examples.guice.constant.CommunicationModel;

public interface CommunicationMode {

    public CommunicationModel getMode();
    
    public boolean sendMessage(String message);

}
