package Application;

import java.io.Serializable;

public class Package implements Serializable {
    public byte system_state;
    public short battery_voltage;
    public short bus5v_voltage;
    public float baro_altitude;
    public byte pyro_states;
    public float pressure;
    public short temperature;
    public float acc_x;
    public float acc_y;
    public float acc_z;
    public float q_x;
    public float q_y;
    public float q_z;
    public float q_w;
    public float gps_n;
    public float gps_e;
    public float gps_alt;
    public short vdop;
    public short pdop;
    public byte gps_sats;
    public float est_x;
    public float est_y;
    public float est_z;
    public float v_x;
    public float v_y;
    public float v_z;
    public short rssi;
    public float snr;

    public Package(){

    }

    public void printState(){
        System.out.println(system_state);
    }
}