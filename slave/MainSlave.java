import java.io.*;
import java.net.*;

public class MainSlave {
    private Socket socketSlave = null;
    private DataInputStream input = null;
    private DataInputStream output = null;

    public MainSlave(String address, int port) {
        try {
            socketSlave = new Socket(address, port);
            input = new DataInputStream(new BufferedInputStream(socketSlave.getInputStream()));

            String temp = "";
            while(true) {
                try {
                    temp = input.readUTF();
                    System.out.println(temp);
                }
                catch(IOException i) {
                    System.out.println(i);
                }
            }
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        MainSlave slave = new MainSlave("127.0.0.1", 1337);
    }
}
