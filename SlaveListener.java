import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/* Adds prime numbers from range to primeList */
public class SlaveListener extends Thread {
    Socket slaveSocket;
    Integer primes;
    DataInputStream input;

    public SlaveListener(Socket s, Integer p) {
        this.slaveSocket = s;
        this.primes = p;
    }

    public void run() {
        try {
            input = new DataInputStream(slaveSocket.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Checks if each integer in range is a prime
        
        try {
            int add = input.readInt();
            System.out.println(add);
            Master.numPrimes += add;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
