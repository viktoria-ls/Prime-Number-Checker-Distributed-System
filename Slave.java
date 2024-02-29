import java.io.*;
import java.net.*;
import java.util.ArrayList;

/* Gets a range from Master for prime checking */
public class Slave {
    private Socket socketSlave = null;
    private static int PORT = 1337;
    private static String SERVER_ADDRESS = "10.50.190.251";
    private DataInputStream in = null;
    private DataOutputStream out = null;

    private static int start;
    private static int end;
    private static int NUM_THREADS = 8;
    private static ArrayList<Integer> primes = new ArrayList<>();

    public Slave(String address, int port) {
        try {
            socketSlave = new Socket(address, port);
            out = new DataOutputStream(socketSlave.getOutputStream());
            in = new DataInputStream(socketSlave.getInputStream());

            while (true) {
                NUM_THREADS = in.readInt();
                start = in.readInt();
                end = in.readInt();
                System.out.println("Threads used: " + NUM_THREADS + "Range: " + start + ", " + end);

                PrimeThreadHandler.start(start, end, NUM_THREADS, primes);      // Do sanity checking in this method

                out.writeInt(primes.size());

                System.out.println("Prime count (slave): " + primes.size());

                primes.clear();
            }
        }
        catch(IOException i) {
            i.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Slave slave = new Slave(SERVER_ADDRESS, PORT);
    }
}
