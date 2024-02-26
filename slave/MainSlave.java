import java.io.*;
import java.net.*;

public class MainSlave {
    private Socket socketSlave = null;
    private static int PORT = 1337;
    private static String SERVER_ADDRESS = "127.0.0.1";
    private DataInputStream in = null;
    private DataInputStream out = null;

    public MainSlave(String address, int port) {

        try {
            socketSlave = new Socket(address, port);
            in = new DataInputStream(socketSlave.getInputStream());

            while (true) {
                System.out.println(in.readInt() + ", " + in.readInt());
            }
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        MainSlave slave = new MainSlave(SERVER_ADDRESS, PORT);
    }
}
