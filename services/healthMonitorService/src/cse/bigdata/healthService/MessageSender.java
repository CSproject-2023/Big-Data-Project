package cse.bigdata.healthService;


import healthMessage.HealthMessage;
import healthMessage.Message;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

public class MessageSender {
    public static void sendToSpeedLayer(Message message) {
        try {
            TTransport transport;
            transport = new TSocket("192.168.43.184", 8650);
            transport.open();

            TProtocol protocol = new  TBinaryProtocol(transport);
            HealthMessage.Client client = new HealthMessage.Client(protocol);
            perform(client, message);
            transport.close();
        } catch (TException x) {
//            x.printStackTrace();
            System.out.println("Message Not sent to speed layer!");
        }
    }

    private static void perform(HealthMessage.Client client, Message message) throws TException {
        client.sendHealthMessage(message);
    }

}
