package com.packagename.myapp;

public class DataServices {
    String service;
    String count;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCPU() {
        return CPU;
    }

    public void setCPU(String CPU) {
        this.CPU = CPU;
    }

    public String getRAM() {
        return RAM;
    }

    public void setRAM(String RAM) {
        this.RAM = RAM;
    }

    public String getDisk() {
        return Disk;
    }

    public void setDisk(String disk) {
        Disk = disk;
    }

    public String getPeakTime() {
        return peakTime;
    }

    public void setPeakTime(String peakTime) {
        this.peakTime = peakTime;
    }

    String CPU;
    String RAM;
    String Disk;
    String peakTime;

    public DataServices(String service, String count, String CPU, String RAM, String disk, String peakTime) {
        this.service = service;
        this.count = count;
        this.CPU = CPU;
        this.RAM = RAM;
        Disk = disk;
        this.peakTime = peakTime;
    }
}
