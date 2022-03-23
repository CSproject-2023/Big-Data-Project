package cse.bigdata.healthService;

import healthMessage.Message;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class HadoopSaver implements Runnable{

    private List<Message> messages;
    private String path;
    private String hadoop_path;

    private static String HADOOP_SERVER_PATH= "hdfs://localhost:9000/user/BigData/";

    public HadoopSaver(List<Message> messages){
        this.messages= messages;
        path= System.currentTimeMillis() % 1000+"_";
        LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
        path+="_"+currentDay;
        Month currentMonth = currentdate.getMonth();
        path+="_"+currentMonth;
        int currentYear = currentdate.getYear();
        path+= "_"+currentYear+".log";
        hadoop_path= HADOOP_SERVER_PATH+path;
    }

    @Override
    public void run() {
        Configuration conf = new Configuration();

        try(FileSystem fs = FileSystem.get(URI.create(hadoop_path), conf);
            FSDataOutputStream out = fs.create(new Path(hadoop_path))) {
            int counter= 1;
            while(!messages.isEmpty())
                writeMessage(messages.remove(0),counter++,out);
            System.out.println("Data successfully saved to Hadoop");
        }
        catch (Exception ignored){}
    }

    private void writeMessage(Message message, int counter,FSDataOutputStream out) throws IOException {
        out.writeBytes(getJsonStringFormat(message,counter) + "\n\n");
    }
    private String getJsonStringFormat(Message message , int counter){
        String json="__________________________________"+counter+"__________________________________\n\n";
        json += "{\n \"serviceName\": \""+message.getServiceName()+"\",\n \"Timestamp\": "+message.getTimestamp();
        json += ",\n \"RAM\": {\n\t\"Total\":"+message.getRAM().getTotal()+",\n\t\"Free\":"+message.getRAM().getFree()+"\n\t}";
        json += ",\n \"Disk\": {\n\t\"Total\":"+message.getDisk().getTotal()+",\n\t\"Free\":"+message.getDisk().getFree()+"\n\t}";
        json +="\n}";
        return json;
    }
}
