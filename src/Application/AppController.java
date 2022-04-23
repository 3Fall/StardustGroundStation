package Application;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;


import java.io.*;

import java.util.Base64;

import static Application.DistanceCalculator.calculateDistance;


public class AppController{

    @FXML Label portLabel;
    @FXML Label labelTelemetry;
    @FXML Label labelCompass;

    @FXML
    ImageView compass;

    @FXML Button buttonLaunch;
    @FXML Button buttonArmParachute;
    @FXML Button buttonEnableTVC;
    @FXML Button buttonSuspendMission;
    @FXML Button buttonChangePort;
    @FXML Button buttonSetHome;

    Package pack = new Package();

    ByteArrayOutputStream packet = new ByteArrayOutputStream();

    public float initial_pos_n = 0;
    public float initial_pos_e = 0;

    public int port;

    SerialPort serial = null;

    public void setPortLabel(int port) {

        //System.out.println(Mem.layoutString(Package.class));
        portLabel.setText("Port: " + SerialPort.getCommPorts()[port].toString());
        resetSerial(port);
        this.port = port;
    }

    public void resetSerial(int port) {
        serial = SerialPort.getCommPorts()[port];
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
                byte[] decoded = Base64.getDecoder().decode(packet.toByteArray());
                pack = new Package();
                try {
                    ObjectDeserializer.deserialize(decoded, pack);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> pack.resetLabels(labelTelemetry));
                compass.setRotate(Math.atan2(pack.gps_n-initial_pos_n, pack.gps_e-initial_pos_e) * 180 / Math.PI);
                Platform.runLater(() -> labelCompass.setText("The rocket is approximately\n" + Float.toString(calculateDistance(initial_pos_n,initial_pos_e,pack.gps_n,pack.gps_e)) + " meters away"));
            }
            packet = new ByteArrayOutputStream();
        }else{
            packet.write(item);
        }
    }

    public void launch(ActionEvent event){
        System.out.println("ready to send");
        byte[] testpacket = {1,2,3,4};
        sendData(testpacket,4);
    }

    public void armParachute(ActionEvent event){

    }

    public void enableTVC(ActionEvent event){

    }

    public void testTVC(ActionEvent event){

    }

    public void suspendMission(ActionEvent event){

    }

    public void changePort(ActionEvent event){
        //TODO program this shit
    }

    public void setHome(ActionEvent event){
        initial_pos_n = pack.gps_n;
        initial_pos_e = pack.gps_e;
    }

    private void sendData(byte[] arr, int datasize){
        System.out.println("sending data");
        serial.writeBytes(arr, datasize);
        byte[] endl = new byte[1];
        endl[0] = 10;
        serial.writeBytes(endl, 1);
        System.out.println("data sent");

    }
}
