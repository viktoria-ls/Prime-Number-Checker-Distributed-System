// import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.junit.jupiter.api.Test;
import java.net.*;
import java.io.*;

public class MainMaster {
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    

  public MainMaster(int port) {
    try
    {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started");

        System.out.println("Waiting for a client ...");

        //Accept multiple sockets learn how
        socket = serverSocket.accept();
        System.out.println("Client accepted");

        // takes input from the client socket
        in = new DataInputStream(
            new BufferedInputStream(socket.getInputStream()));

        String line = "";

        // reads message from client until "Over" is sentz
        while (!line.equals("Over"))
        {
            try
            {
                line = in.readUTF();
                System.out.println(line);

            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }
        System.out.println("Closing connection");

        // close connection
        socket.close();
        in.close();
    }
    catch(IOException i)
    {
        System.out.println(i);
    }
  }
  
  public static void main(String[] args) {
    System.out.println("Hello world!");
  }

  // @Test
  // void addition() {
  //     assertEquals(2, 1 + 1);
  // }
}