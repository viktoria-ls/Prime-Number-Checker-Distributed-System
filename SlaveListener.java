import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

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
            e.printStackTrace();
        }
        
        try {
            int add = input.readInt();
            Master.numPrimes += add;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
