package cse.bigdata.healthService;

import healthMessage.DiskData;
import healthMessage.Message;
import healthMessage.RamData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;

import static cse.bigdata.healthService.HealthMonitorServer.HADOOP_SERVER_PATH;

public class HadoopSaver implements Runnable{

    private List<Message> messages;
    private String path;
    private String hadoop_path;


    public static void main(String[] args) {
        LinkedList<Message> messages= new LinkedList<>();
        messages.add(new Message("Hello","World",2.5,new RamData(45,25),new DiskData(45,222)));
        HadoopSaver saver= new HadoopSaver(messages);
        saver.run();
    }

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
        try {
            FileWriter writer = new FileWriter(new File(path));
            int counter=0;
            for(Message i : messages){
                writer.write(getJsonStringFormat(i,counter++));
            }
            writer.close();
            System.out.println(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        long currentTime= System.currentTimeMillis();
        try(FileSystem fs = FileSystem.get(URI.create(hadoop_path), conf);
            FSDataOutputStream out = fs.create(new Path(hadoop_path))) {
            int counter= 1;
            while(!messages.isEmpty())
                writeMessage(messages.remove(0),counter++,out);
            System.out.println("Data successfully saved to Hadoop");
            System.out.println("Total time: "+(System.currentTimeMillis()-currentTime));
        }
        catch (Exception ignored){
            System.out.println("Error");
            ignored.printStackTrace();
        }
    }

    private void writeMessage(Message message, int counter,FSDataOutputStream out) throws IOException {
        out.writeBytes(getJsonStringFormat(message,counter) + "\n\n");
    }
    private String getJsonStringFormat(Message message , int counter){
        String json=counter+":";
        json += "{\"serviceName\": \""+message.getServiceName()+"\",\"Timestamp\": "+message.getTimestamp() + ",\"CPU\":"+ message.getCPU();
        json += ",\"RAM\": {\t\"Total\":"+message.getRAM().getTotal()+",\t\"Free\":"+message.getRAM().getFree()+"\t}";
        json += ", \"Disk\": {\t\"Total\":"+message.getDisk().getTotal()+",\t\"Free\":"+message.getDisk().getFree()+"\t}";
        json +="}\n";
        return json;
    }
}
