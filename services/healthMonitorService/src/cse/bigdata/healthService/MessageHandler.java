package cse.bigdata.healthService;

import healthMessage.HealthMessage;
import healthMessage.Message;
import org.apache.thrift.TException;

public class MessageHandler implements HealthMessage.Iface {

    @Override
    public void sendHealthMessage(Message message) throws TException {
        System.out.println("Message Received!");
        System.out.println(message);
    }
}
