package servercp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ServerCP {

    protected final static int PORT = 60000;
    public static ArrayList cSocketList = new ArrayList();
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    
    public static void main(String[] args) throws IOException{
        //initializing stream
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/game?zeroDateTimeBehavior=convertToNull", "root", "");
        }
        catch (Exception e) {System.out.println(e);}
        InetAddress serverIP = null;
        serverIP = InetAddress.getByName("localhost");
        ServerSocket sSocket = new ServerSocket(PORT, 0,serverIP);
        System.out.println("Server is starting...");
        messageThread messanger = new messageThread(sSocket,PORT);
        messanger.start();
        
        while (true){
            Socket cSocket = sSocket.accept();
            cSocketList.add(cSocket);
            System.out.println("To server connected " + cSocket.getInetAddress() + ":" + cSocket.getPort());
            new clientThread(messanger,cSocket,PORT).start();
        }
    }
    
}
