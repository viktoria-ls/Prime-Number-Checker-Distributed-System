import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MainSlave {
    private Socket socketSlave = null;
    private static int PORT = 1337;
    private static String SERVER_ADDRESS = "127.0.0.1";
    private DataInputStream in = null;
    private DataOutputStream out = null;

    private static int start;
    private static int end;
    private static ArrayList<Integer> primes = new ArrayList<>();

    public MainSlave(String address, int port) {

        try {
            socketSlave = new Socket(address, port);
            out = new DataOutputStream(socketSlave.getOutputStream());
            in = new DataInputStream(socketSlave.getInputStream());

            while (true) {
                start = in.readInt();
                end = in.readInt();
                System.out.println(start + ", " + end);

                for(int i = start; i <= end; i++) {
                    if(check_prime(i))
                        primes.add(i);
                }

                // TODO: Do sanity checking here
                out.writeInt(primes.size());

                primes.clear();
            }
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    public static boolean check_prime(int n) {
        for(int i = 2; i * i <= n; i++) {
            if(n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String args[]) {
        MainSlave slave = new MainSlave(SERVER_ADDRESS, PORT);
    }
}
