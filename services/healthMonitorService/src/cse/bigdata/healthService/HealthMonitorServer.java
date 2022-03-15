package cse.bigdata.healthService;




import healthMessage.HealthMessage;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

public class HealthMonitorServer {

    public static MessageHandler handler;

    public static HealthMessage.Processor processor;

    public static void main(String [] args) {
        try {
            handler = new MessageHandler();
            processor = new HealthMessage.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };
            new Thread(simple).start();
//            new Thread(secure).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(HealthMessage.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
//            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
             TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
