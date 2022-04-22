package Application;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.*;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Base64;


public class AppController{

    @FXML Label portLabel;
    @FXML ListView<String> console;
    @FXML LineChart<Integer, Integer> testChart;

    @FXML Label labelSystemState;

    Package p = new Package();

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

        //System.out.println(item);

        if((char)item == '\n'){
            if(packet.size() > 0) {
                //console.getItems().add(packet);
                byte[] decoded = Base64.getDecoder().decode(packet.toByteArray());
                p = new Package();
                ObjectDeserializer deserializer = new ObjectDeserializer();

                try {
                    deserializer.deserialize(decoded, p);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            packet = new ByteArrayOutputStream();
        }else{

            packet.write(item);
        }
    }
}
