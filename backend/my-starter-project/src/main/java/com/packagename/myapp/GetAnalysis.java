package com.packagename.myapp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetAnalysis {
    public String getData(int start , int end){
        try {
            long timeNow= System.currentTimeMillis();
            DatagramSocket ds = new DatagramSocket();
            DatagramSocket recSocket = new DatagramSocket(3001);
            String str = start+" "+end;
            InetAddress ip = InetAddress.getByName("h-primary");
            DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, 3000);
            ds.send(dp);
            ds.close();

            byte[] buf = new byte[1024];
            DatagramPacket recPacket = new DatagramPacket(buf, 1024);
            recSocket.receive(recPacket);

            str = new String(recPacket.getData(), 0, recPacket.getLength());
            System.out.println(str);
            recSocket.close();
            System.out.println("Time taken= "+(System.currentTimeMillis()- timeNow));
            return str;
        }catch(Exception e){
            e.printStackTrace();
            return "Error!";
        }
    }
}
