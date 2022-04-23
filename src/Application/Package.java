package Application;

import javafx.scene.control.Label;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Locale;

public class Package implements Serializable {
    public byte system_state;
    public short battery_voltage;
    public short bus5v_voltage;
    public float baro_altitude;
    public byte pyro_states;
    public float pressure;
    public short temperature;
    public float acceleration_x;
    public float acceleration_y;
    public float acceleration_z;
    public float gyroscope_x;
    public float gyroscope_y;
    public float gyroscope_z;
    public float gyroscope_w;
    public float gps_n = 0.0f;
    public float gps_e = 0.0f;
    public float gps_alt;
    public short vdop;
    public short pdop;
    public byte gps_satellites;
    public float estimate_x;
    public float estimate_y;
    public float estimate_z;
    public float velocity_x;
    public float velocity_y;
    public float velocity_z;
    public short RRSI;
    public float SNR;

    public Package(){}

    private String getSystemState() {
        return "idk";
    }

    public void resetLabels(Label label){
        String res = "";
        for(Field field : this.getClass().getFields()){
            switch(field.getName().replace('_', ' ')){
                case "system state":
                    res += field.getName().replace('_', ' ') + ": " + this.getSystemState() + "\n";
                default:
                    try {
                        res += field.getName().replace('_', ' ') + ": " + field.get(this) + "\n";
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
            }

        }
        label.setText(res);
    }
}