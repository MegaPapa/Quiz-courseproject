package servercp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class messageThread extends Thread{
    
    private static DataInputStream fromClients;
    private static DataOutputStream toClients;
    private static int PORT;
    private static final int RECEIVE_COMMAND = 2;
    private static ServerSocket sSocket;
    private static ArrayList cSocketList = new ArrayList();
    
    
    messageThread(ServerSocket sSocket,int PORT){
        this.sSocket = sSocket;
        this.PORT = PORT;
    }
    
    protected void addClientSocket(Socket cSocket){
        cSocketList.add(cSocket);
        System.out.println("MESSAGE_THREAD : To messanger added " + cSocket.getInetAddress() + ":" + cSocket.getPort());
    }
    
    protected void sendMessage(String message) throws IOException{
        for (int i = 0; i < (cSocketList.size()); i++){
            System.out.println("raz otoslali");
            Socket tmpSocket = (Socket)cSocketList.get(i);
            toClients = new DataOutputStream(tmpSocket.getOutputStream());
            toClients.writeInt(RECEIVE_COMMAND);
            toClients.writeUTF(message);
        }
    }
    
    public void run(){
        String message;
        //fromClients = new DataInputStream(sSocket.);
        while (true){
            
        }
    }
}
