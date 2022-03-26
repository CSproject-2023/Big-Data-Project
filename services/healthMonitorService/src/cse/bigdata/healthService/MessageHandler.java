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
        if (messages.size() % 100== 0)
            System.out.println(messages.size());
        if(messages.size() == 50){
            //save to Hadoop
            HadoopSaver saver= new HadoopSaver(messages);
            messages= new LinkedList<>();
            new Thread(saver).start();
        }
    }
}
