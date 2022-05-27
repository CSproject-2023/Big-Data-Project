package com.packagename.myapp;

import java.net.*;

public class GetAnalysis {

    String servingLayerData;
    String SpeedLayerData;

    int startDay;
    int endDay;
    int startHr;
    int endHr;
    int startMin;
    int endMin;
    public String getData(int start , int end, int startHr,int startMin, int endHr,int endMin){
        this.startDay=start;
        this.endDay= end;
        this.startHr=startHr;
        this.endHr=endHr;
        this.startMin=startMin;
        this.endMin= endMin;

        Thread serving= new Thread(new Runnable() {
            @Override
            public void run() {
                handleServingLayer();
            }
        });

        Thread speed= new Thread(new Runnable() {
            @Override
            public void run() {
                handleSpeedLayer();
            }
        });

        try {
            servingLayerData= "";
            SpeedLayerData="";

            DatagramSocket ds = new DatagramSocket();
            int fromMin = (startMin + startHr * 60) % (24 * 60);
            int toMin = (endHr * 60 + endMin) % (24 * 60);
            String str = startDay + " " + endDay + " " + fromMin + " " + toMin;
            System.out.println(str);
            InetAddress ipServing = InetAddress.getByName("h-primary");
            InetAddress ipSpeed = InetAddress.getByName("192.168.1.2");
            DatagramPacket dServing = new DatagramPacket(str.getBytes(), str.length(), ipServing, 3001);
            DatagramPacket dSpeed = new DatagramPacket(str.getBytes(), str.length(), ipSpeed, 3001);
            ds.send(dServing);
            ds.send(dSpeed);
            ds.close();


            serving.start();
            speed.start();
            serving.join();
            speed.join();
            return servingLayerData+SpeedLayerData;
        }catch(Exception e){
            e.printStackTrace();
            return "Error!";
        }
    }


    private void handleSpeedLayer(){
        DatagramSocket recSocket = null;
        try {
            long timeNow = System.currentTimeMillis();
            recSocket = new DatagramSocket(4225);
            recSocket.setSoTimeout(10000); //10 seconds is enough
            byte[] buf = new byte[1024];
            DatagramPacket recPacket = new DatagramPacket(buf, 1024);
            recSocket.receive(recPacket);
            SpeedLayerData = new String(recPacket.getData(), 0, recPacket.getLength());
            recSocket.close();
            System.out.println("Time taken in Speed layer= " + (System.currentTimeMillis() - timeNow));
        }catch (Exception e){
//            e.printStackTrace();
            System.out.println("Error in Speed Layer");
            recSocket.close();
        }

    }
    private void handleServingLayer(){
        DatagramSocket recSocket = null;
        try {
            long timeNow = System.currentTimeMillis();
            recSocket = new DatagramSocket(3000);
            recSocket.setSoTimeout(10000); //10 seconds is enough
            byte[] buf = new byte[1024];
            DatagramPacket recPacket = new DatagramPacket(buf, 1024);
            recSocket.receive(recPacket);
            servingLayerData = new String(recPacket.getData(), 0, recPacket.getLength());
            recSocket.close();
            System.out.println("Time taken in serving layer= " + (System.currentTimeMillis() - timeNow));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in serving layer");
            recSocket.close();
        }
    }
}
