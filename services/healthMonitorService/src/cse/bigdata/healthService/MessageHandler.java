package cse.bigdata.healthService;

import healthMessage.HealthMessage;
import healthMessage.Message;
import java.util.LinkedList;
import java.util.List;

public class MessageHandler implements HealthMessage.Iface {

    private static int MAX_COUNT= 1024;
    private MessageTimer messages;

    public  MessageHandler(){
        messages= new MessageTimer();
    }

    @Override
    public synchronized void sendHealthMessage(Message message){
        messages.add(message,System.currentTimeMillis());
        if (messages.messages.size() % 100== 0)
            System.out.println(messages.messages.size());
        if(messages.messages.size() == MAX_COUNT){
            //save to Hadoop
            HadoopSaver saver= new HadoopSaver(messages.messages);
            double average=0;
            for(long i : messages.timesAtReceive)
                average += (System.currentTimeMillis()-i);
            System.out.println("Average is "+(average/messages.messages.size()));
            average= average/messages.messages.size();
            double std= 0;
            for(long i : messages.timesAtReceive)
                std += Math.pow(average - (System.currentTimeMillis()-i) , 2);
            std /= (messages.messages.size()-1);
            System.out.println("STD is "+Math.sqrt(std));
            messages= new MessageTimer();
            new Thread(saver).start();
        }
    }


    class MessageTimer{
        List<Message> messages;
        List<Long> timesAtReceive;

        public MessageTimer(){
            messages= new LinkedList<>();
            timesAtReceive= new LinkedList<>();
        }

        public void add(Message message, long time){
            messages.add(message);
            timesAtReceive.add(time);
        }
    }
}
