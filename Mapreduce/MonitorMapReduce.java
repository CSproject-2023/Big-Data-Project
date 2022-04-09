import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.Provider.Service;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

// import ServiceData;

public class MonitorMapReduce {

  private static final String INPUT_PATH= "/user/BigData/";
  private static final String OUTPUT_PATH= "/output_big_data2";
  private FileSystem hdfs;
  private Configuration conf;
  private Job job;

  private HashMap<String, ServiceData> batchData= new HashMap<>();



  /*Mapper Key: Timestamp:ServerName */
  /*Combiner Key:TimeStamp , value: Service */

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, ServiceData>{
      
     
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      String data= value.toString();
      if(data.length() == 0)
        return;
      ServiceData x= new ServiceData(data);                      
      context.write(x.serviceName ,x);      
    }
  }

  public static class ServiceReducer extends Reducer <Text,ServiceData,Text,ServiceData> {

    private ServiceData overAll= new ServiceData();
    @Override
    protected void reduce(Text key, Iterable<ServiceData> values,Context context) throws IOException, InterruptedException {

      for (ServiceData i : values){
        overAll.add(i);
      }
      overAll.serviceName= key;
      overAll.CPU_util.set(overAll.CPU_util.get() / overAll.count.get());
      overAll.ram_util.set(overAll.ram_util.get() / overAll.count.get());
      overAll.disk_util.set(overAll.disk_util.get() / overAll.count.get());
      overAll.timestamp.set(overAll.timestamp.get() / overAll.count.get());
      context.write(key, overAll);
    }
  }
 

  public static class ServicePartitioner extends
   Partitioner < Text, ServiceData >
   {
      @Override
      public int getPartition(Text key, ServiceData value, int numReduceTasks){
        int x=((int)value.timestamp.get() -1) ;
        if(x < 0)
          return 0;
        return x % numReduceTasks;
      }
   }


  public MonitorMapReduce(){
    System.out.println("Configuring....");
    conf= new Configuration();
    try{
    hdfs = FileSystem.get(conf);
    }catch(Exception e){
      throw new RuntimeException();
    }
  }

  private void deleteFileIfItExists(){
    try{

      if (hdfs.exists(new Path(OUTPUT_PATH))) {
        hdfs.delete(new Path(OUTPUT_PATH),true);
      }
    }
    catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException("Failed to delete Output!");
    }
  }

  public String start(int from, int to) throws Exception{
    String data= "";
    try {
      
      for(int i= from ; i<to ; i++)
        read_hdfs(i);


      for(ServiceData service: batchData.values()){
        data+=service.serviceName.toString()+": ";
        service.CPU_util.set(service.CPU_util.get() / service.count.get());
        data+= "Count="+service.count.get()+" ";
        data+="CPU="+service.CPU_util.get()+" ";
        service.ram_util.set(service.ram_util.get() / service.count.get());
        data+="RAM="+service.ram_util.get()+" ";
        service.disk_util.set(service.disk_util.get() / service.count.get());
        data+="Disk="+service.disk_util.get()+" ";
        // service.timestamp.set(service.timestamp.get() / service.count.get());
        data+="PeakTime="+((float)service.timestamp.get()/service.count.get())+"\n";
      }
      System.out.println(data);
      return data;
      }catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }

  private String generateNum(int num){
    String x= num+"";
    while (x.length() != 5)
      x= "0"+x;
    return x;
  }
  private String read_hdfs(int num)throws Exception{
    Path inFile = new Path(OUTPUT_PATH+"/part-r-"+generateNum(num));

    FSDataInputStream in = hdfs.open(inFile);
    // System.out.println(in);
    try{
      
      if(in.available() > 0){
        // Text serviceName= new Text();
        // ServiceData service= new ServiceData();
        // serviceName.readFields(in);
        // service.readFields(in);
        // System.out.println(service.toString())
        List<ServiceData> services= ServiceData.getServices(in);
        for(ServiceData service:services)
          addToMap(service.serviceName, service);
      }
      in.close();
      return "";
    }catch(Exception e){
      in.close();
      return "";
    }
  }

  private void addToMap(Text x, ServiceData data) {
    // System.out.println(x.toString());
    ServiceData currentData= batchData.get(x.toString());
    if(currentData == null){
      System.out.println(x.toString()+" Not Found!");
      batchData.put(x.toString(), data);
      // data.count.set(1);
      return;
    }
    // System.out.println("Found!");
    currentData.add(data);
    // System.out.println(data.timestamp.get());
    currentData.count.set(data.count.get()-1 + currentData.count.get());
    // System.out.println(currentData.timestamp.get());
  }

  private void doMapReduce() {
    deleteFileIfItExists();
    try{
      job= Job.getInstance(conf, "monitor");
      job.setJarByClass(MonitorMapReduce.class);
      job.setMapperClass(TokenizerMapper.class);
      job.setMapOutputKeyClass(Text.class);
      job.setMapOutputValueClass(ServiceData.class);
      
      job.setPartitionerClass(ServicePartitioner.class);
      job.setReducerClass(ServiceReducer.class);
      job.setNumReduceTasks(365); //CPU,RAM,DISK,Count, Peak.
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(ServiceData.class);
      FileInputFormat.addInputPath(job, new Path(INPUT_PATH));
      FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));
      job.waitForCompletion(true);
    }catch(Exception e){
      throw new RuntimeException("Failed to initialize Job!");
    }

  }

  private void handleBatches(MonitorMapReduce monitor){
      byte[] buf= new byte[1024];
      while(true){
        try{
          DatagramSocket ds = new DatagramSocket(3000);  
          DatagramPacket dp = new DatagramPacket(buf, 1024);  
          System.out.println("Waiting to connect...");
          ds.receive(dp);  
          String str = new String(dp.getData(), 0, dp.getLength());  
          System.out.println(str);
          String[] receivedNums= str.split(" ");  
          String data=monitor.start(Integer.parseInt(receivedNums[0]),Integer.parseInt(receivedNums[1]) ); /* To be configured */
          // String data=monitor.start(0,364 ); /* To be configured */

          // System.out.println(data);
          DatagramPacket packet= new DatagramPacket(data.getBytes(),data.length(),dp.getAddress(),3001);
          ds.send(packet);
          ds.close();
        }catch(Exception e){}
      }

  }

  public static void main(String[] args) throws Exception {

    // String x= "11:{\"serviceName\": \"service-2","Timestamp": 1675538628,"CPU":0.052407549846939405,"RAM": {	"Total":319.40702085845203,	"Free":0.0	}, "Disk": {	"Total":377.907239064582,	"Free":0.0	}}"
    // while (true){
      long time= System.currentTimeMillis();
      MonitorMapReduce monitor= new MonitorMapReduce();
      // monitor.doMapReduce();
      System.out.println("Time is "+(System.currentTimeMillis() - time) + " ms");
      monitor.handleBatches(monitor);
     
    // }
    // ds.close();  

  }




    
}

