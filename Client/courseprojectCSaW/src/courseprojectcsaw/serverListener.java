package courseprojectcsaw;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import sun.security.util.ManifestDigester;


public class serverListener extends Thread{
    
    
    
    private static DataInputStream fromServer;
    private static DataOutputStream toServer;
    private static Socket socket;
    private static TextArea mf;
    
    
    
    public serverListener(Socket socket,TextArea mf) {
        this.socket = socket;
        this.mf = mf;
        
    }
    
    
    
    private static void receiveMessageFromServer() throws IOException{
       
        String message = fromServer.readUTF();
        mf.appendText(message+"\n");
    }
    
    private static void selectCommand(int command) throws IOException{
        switch (command){
            case 2:
                receiveMessageFromServer();
                break;
        }
    }
    
    @Override
    public void run(){
        int command = -1;
        try{
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
            
            while (true){
                command = fromServer.readInt();
                selectCommand(command);
            }
        }
        catch (IOException e) {System.out.println(e);}
    }
    
}
