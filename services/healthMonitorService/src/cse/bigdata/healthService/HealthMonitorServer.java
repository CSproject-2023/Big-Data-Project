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
//import org.a

public class HealthMonitorServer {

    public static MessageHandler handler;

    public static HealthMessage.Processor processor;

    public static void main(String [] args) throws IOException {
//        try {
//            handler = new MessageHandler();
//            processor = new HealthMessage.Processor(handler);
//
//            Runnable simple = new Runnable() {
//                public void run() {
//                    simple(processor);
//                }
//            };
//            new Thread(simple).start();
////            new Thread(secure).start();
//        } catch (Exception x) {
//            x.printStackTrace();
//        }



    }

    public void readFromHDFS() throws IOException {
        String uri = "hdfs://localhost:9000/user/input/xxx.txt";

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), conf);
        FSDataInputStream in = null ;

        try
        {
            in = fs.open(new Path(uri));
            IOUtils.copyBytes(in, System.out, 4096, false);
        }
        finally
        {
            IOUtils.closeStream(in);
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
