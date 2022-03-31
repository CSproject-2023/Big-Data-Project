package cse.bigdata.healthService;




import healthMessage.HealthMessage;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

import java.io.*;
import java.net.URI;
import java.util.Scanner;
//import org.a

public class HealthMonitorServer {

    public static MessageHandler handler;

    public static HealthMessage.Processor processor;

    public static String HADOOP_SERVER_PATH= "hdfs://h-primary:9000/user/BigData/";
    public static final Scanner sc= new Scanner(System.in);

    public static void main(String [] args) throws IOException {
//        System.out.print("Enter HDFS IP: ");
//        HADOOP_SERVER_PATH= sc.next();
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
            TServerTransport serverTransport = new TServerSocket(3500);
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
