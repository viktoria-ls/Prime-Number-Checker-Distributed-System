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
        in = new DataInputStream(System.in);
        out = new DataOutputStream(socket.getOutputStream());
    }
    catch(UnknownHostException h) {
        System.out.println(h);
        return;
    }
    catch(IOException i) {
        System.out.println(i);
        return;
    }

    String temp = "";

    while(true) {
        try {
            temp = in.readLine();
            out.writeUTF(temp);
        }
        catch(IOException i) {
            System.out.print(i);
        }
    }
  }
  
  public static void main(String[] args) {
    MainMaster server = new MainMaster(1337);
  }

  // @Test
  // void addition() {
  //     assertEquals(2, 1 + 1);
  // }
}