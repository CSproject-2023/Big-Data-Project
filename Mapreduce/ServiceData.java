// package mapreduce;
  /**
   * Sample analytics we can collect are, but not limited to:
    The mean CPU utilization for each service
    The mean Disk utilization for each service
    The mean RAM utilization for each service
    The peak time of utilization for each resource for each service
    The count of health messages received for each service
   */
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class ServiceData implements Writable {

    Text serviceName= new Text();
    LongWritable timestamp= new LongWritable();
    DoubleWritable CPU_util= new DoubleWritable();
    DoubleWritable disk_util= new DoubleWritable();
    DoubleWritable ram_util= new DoubleWritable();
    IntWritable count= new IntWritable(0);
    private static Calendar cal= Calendar.getInstance();

    // public static void main(String[] args) {
    //     String x = "0:{\"serviceName\": \"Server2\",\"Timestamp\": 1648718654.6556547,\"CPU\":0.9110879710762173,\"RAM\": {	\"Total\":0.4820151159393198,	\"Free\":5.51798488406068	}, \"Disk\": {	\"Total\":382.5469861504271,	\"Free\":417.4530138495729	}}\"";
    //     new ServiceData(x);

    // }
    public ServiceData(){}
    public ServiceData(String data){
      String ss= data.split(":")[2];
    //   System.out.println(ss.split(",")[0]);
      String ser= ss.split(",")[0];
      serviceName.set(ser.split("\"")[1]);
      String[] partitions= data.split(",");
      try{
        set_timestamp(partitions[1]);
        set_CPU(partitions[2]);
        set_disk(partitions[5]);
        set_RAM(partitions[3]);
      }catch(Exception e){}
    }
    private void set_CPU(String data){
        char i= 0;
        while( data.charAt(i) != ':')
            i++;
        i++;
        String cpu= data.substring(i);
        CPU_util.set(Double.parseDouble(cpu));
        // System.out.println(CPU_util);
    }

    private void set_disk(String data){
        String[] parts= data.split(":");
        disk_util.set(Double.parseDouble(parts[parts.length-1]));
        // System.out.println(disk_util);
    }

    private void set_RAM(String data){
        String[] parts= data.split(":");
        ram_util.set(Double.parseDouble(parts[parts.length-1]));
        // System.out.println(ram_util);
    }

    private void set_timestamp(String data){
        String[] parts= data.split(":");
        long x=(long)Double.parseDouble(parts[parts.length-1]);
        cal.setTimeInMillis(x);
        x= cal.get(Calendar.DAY_OF_YEAR);
        x= x<0 ? x+365 : x;
        timestamp.set(x);
    }
    @Override
    public void write(DataOutput out) throws IOException {
        try{
            serviceName.write(out);
            count.write(out);
            timestamp.write(out);
            CPU_util.write(out);
            ram_util.write(out);
            disk_util.write(out);
        }catch(Exception e){
            /* Do Nothing */
        }
    
    }
    @Override
    public void readFields(DataInput in) throws IOException {
        try{
            serviceName.readFields(in);
            count.readFields(in);
            timestamp.readFields(in);
            CPU_util.readFields(in);
            ram_util.readFields(in);
            disk_util.readFields(in);  
        }catch(Exception e){
            /* Do Nothing */
        }
    }

    public void add(ServiceData x){
        this.CPU_util.set(x.CPU_util.get() + CPU_util.get());
        this.disk_util.set(x.disk_util.get() + disk_util.get());
        this.ram_util.set(x.ram_util.get() + ram_util.get());
        System.out.println("OLD TIMESTAMP: "+this.timestamp.get());
        this.timestamp.set(x.timestamp.get() + timestamp.get());
        System.out.println(this.timestamp.get());
        this.count.set(count.get() + 1);
    }
    @Override
    public String toString() {
        return "[Count="+this.count.get()+ " CPU_util=" + CPU_util.get() +  ", disk_util=" + disk_util.get() + ", ram_util="
                + ram_util.get() + ", serviceName=" + serviceName.toString() + ", timestamp=" + timestamp.get() + "]";
    }
    public static List<ServiceData> getServices(FSDataInputStream in) throws Exception {
       String data= new String(in.readAllBytes());
    //    System.out.println(data);
       List<ServiceData> services= new LinkedList<>();
       String[] records= data.split("\n");
       for(String st:records)
            if(st.trim().length() > 0){
                // System.out.println(st);
                ServiceData serv= new ServiceData();
                String[] dataSplits= st.split("=");
                // System.out.println(dataSplits[1]);
                serv.count.set(Integer.parseInt(dataSplits[1].split(" ")[0]));
                // System.out.println(serv.count.get());
                serv.CPU_util.set(Double.parseDouble(dataSplits[2].split(",")[0]));
                // System.out.println(serv.CPU_util.get());

                serv.disk_util.set(Double.parseDouble(dataSplits[3].split(",")[0]));
                // System.out.println(serv.disk_util.get());

                serv.ram_util.set(Double.parseDouble(dataSplits[4].split(",")[0]));
                // System.out.println(serv.ram_util.get());

                serv.timestamp.set(Long.parseLong(dataSplits[6].split("]")[0]));
                System.out.println(serv.timestamp.get());

                serv.serviceName.set(dataSplits[5].split(",")[0]);
                // System.out.println(serv.serviceName.toString());
                services.add(serv);

            }
        return services;

    }

    
  }