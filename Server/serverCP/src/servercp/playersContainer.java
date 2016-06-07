package servercp;

/**
 *
 * @author PC
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class playersContainer {
    public int playersCount;
    private ArrayList playerList = new ArrayList();
    private ArrayList streamList = new ArrayList();
    
    
    
    protected void add(Socket cSocket) throws IOException{
        //String playerInfo = ip + ":" + port;
        playersCount++;
        playerList.add(cSocket);
        streamList.add(new BufferedInputStream(cSocket.getInputStream()));
        //System.out.println(playerList.get(0));
    }
    
    protected Socket getSocket(int number){
        return (Socket)playerList.get(number);
    }
    
    protected BufferedInputStream getStream(int number){
        return (BufferedInputStream)streamList.get(number);
    } 
}
