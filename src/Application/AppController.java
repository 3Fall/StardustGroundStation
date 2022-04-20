package Application;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import theleo.jstruct.Struct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.Base64;


public class AppController{

    @FXML Label portLabel;
    @FXML ListView<String> console;
    @FXML LineChart<Integer, Integer> testChart;


    @Struct
    class Package{
        int x;
        int y;
        int z;
    }

    ByteArrayOutputStream packet = new ByteArrayOutputStream();

    public void setPortLabel(int port) {
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
                System.out.print(pac);
                System.out.print(" ");
                System.out.println(Base64.getDecoder().decode(pac));
            }

            packet = new ByteArrayOutputStream();
        }else{
            packet.write(item);
        }
    }
}
