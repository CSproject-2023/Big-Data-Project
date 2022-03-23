package cse.bigdata.healthService;

import healthMessage.HealthMessage;
import healthMessage.Message;
import org.apache.thrift.TException;

import java.util.LinkedList;
import java.util.List;

public class MessageHandler implements HealthMessage.Iface {

    private static int MAX_COUNT= 1024;
    private List<Message> messages;

    public  MessageHandler(){
        messages= new LinkedList<>();
    }

    @Override
    public synchronized void sendHealthMessage(Message message){
        messages.add(message);
        if(messages.size() == MAX_COUNT){
            //save to Hadoop
        }
    }
}
