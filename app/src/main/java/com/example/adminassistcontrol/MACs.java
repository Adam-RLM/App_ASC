package com.example.adminassistcontrol;

public class MACs {
    private String MAC;
    private String SSID;
    private String ID;

    public MACs(){

    }
    public MACs(String MAC, String SSID) {
        this.MAC = MAC;
        this.SSID = SSID;
        this.ID = ID;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
