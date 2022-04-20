package Application;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import theleo.jstruct.Mem;
import theleo.jstruct.Struct;

import java.io.ByteArrayOutputStream;

import java.util.Base64;


public class AppController{

    @FXML Label portLabel;
    @FXML ListView<String> console;
    @FXML LineChart<Integer, Integer> testChart;

    @FXML Label labelSystemState;


    @Struct
    class Package{
        byte system_state;
        short battery_voltage;
        short bus5v_voltage;
        float baro_altitude;
        byte pyro_states;
        float pressure;
        short temperature;
        float acc_x, acc_y, acc_z;
        float q_w, q_x, q_y, q_z;
        float gps_n, gps_e;
        float gps_alt;
        short vdop, pdop;
        byte gps_sats;
        float est_x, est_y, est_z;
        float v_x, v_y, v_z;
        short rssi;
        float snr;
    }

    ByteArrayOutputStream packet = new ByteArrayOutputStream();

    public void setPortLabel(int port) {
        //System.out.println(Mem.layoutString(Package.class));
        portLabel.setText("Port: " + SerialPort.getCommPorts()[port].toString());
        SerialPort serial = SerialPort.getCommPorts()[port];
        serial.openPort();
        serial.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                int size = event.getSerialPort().bytesAvailable();
                byte[] buffer = new byte[size];
                event.getSerialPort().readBytes(buffer, size);
                for(byte b : buffer){
                    addToConsole(b);
                }

            }
        });
    }

    public void addToConsole(byte item){

        if((char)item == '\n'){
            if(packet.size() > 0) {
                //console.getItems().add(packet);
                byte[] pac = packet.toByteArray();
                for(byte b : pac){
                    System.out.print((char)b);
                }
                System.out.print(" ");

                byte[] decoded = Base64.getDecoder().decode(pac);
                for(byte b : decoded){
                    System.out.print((char)b);
                }
                System.out.println();
            }

            packet = new ByteArrayOutputStream();
        }else{

            packet.write(item);
        }
    }
}
