package cse.bigdata.service1;


import healthMessage.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.sql.Timestamp;
import java.util.Random;


public class Main {

    public static final String SERVICE_NAME= "Server 1";
    public static final float RAM_MAX_SIZE= 8.6f;
    public static final float DISK_MAX_SIZE= 1024f;

    public static void main(String[] args) throws Exception {
        TTransport transport;
        while (true){
            try {
                transport = new TSocket("192.168.1.118", 9090);
                transport.open();
            }catch (Exception e){
                System.out.println("Connection error");
                Thread.sleep(3000);
                continue;
            }
            TProtocol protocol = new  TBinaryProtocol(transport);
            HealthMessage.Client client = new HealthMessage.Client(protocol);
            handleSending(client);
            System.out.println("Sent!");
            Thread.sleep(3000);
        }
    }

    private static void handleSending(HealthMessage.Client client) throws TException {
        String timestamp= System.currentTimeMillis()+"";
        Random rand= new Random();
        float cpu= rand.nextFloat();
        float usedRam= rand.nextFloat() * RAM_MAX_SIZE;
        RamData RAM= new RamData(usedRam, RAM_MAX_SIZE- usedRam);
        float usedDisk= rand.nextFloat() * DISK_MAX_SIZE;
        DiskData disk= new DiskData(usedDisk, DISK_MAX_SIZE- usedDisk);
        Message message= new Message(SERVICE_NAME,timestamp,cpu,RAM,disk);
        client.sendHealthMessage(message);
    }


}
