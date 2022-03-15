package cse.bigdata.healthService;

import healthMessage.HealthMessage;
import healthMessage.Message;
import org.apache.thrift.TException;

public class MessageHandler implements HealthMessage.Iface {

    private int counter= 0;

    @Override
    public void sendHealthMessage(Message message) throws TException {
        counter++;
        System.out.println(counter);
    }
}
