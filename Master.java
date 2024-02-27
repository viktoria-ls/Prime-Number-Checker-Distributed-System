import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.io.*;

public class Master {
    static ServerSocket serverSocket = null;
    static int PORT = 1337;

    static int start = 2;
    static int end = 100;
    static int NUM_THREADS = 1;

    static int numPrimes = 0;

    static ArrayList<Socket> slaves = new ArrayList<>();
    static Semaphore readyToProcessSem = new Semaphore(0);          // Used to make sure there is at least one slave connected
    static Semaphore slaveListSem = new Semaphore(1);               // Used to ensure no interference on slaves list
    static Semaphore primeCountSem = new Semaphore(1);

  public static void main(String[] args) {
    MasterThread master = new MasterThread();
    master.start();

    Scanner sc = new Scanner(System.in);

    while (true) {
        // Gets user input
        System.out.print("Enter start: ");
        Master.start = sc.nextInt();
        System.out.print("Enter end: ");
        Master.end = sc.nextInt();
        
        // Divides range and gives to slave servers
        // TODO: Give tasks to master too
        try {
            readyToProcessSem.acquire();

            slaveListSem.acquire();
            int numSlaves = slaves.size() + 1;      // +1 for the master server
            int size = end - start + 1;
            numSlaves = Math.min(numSlaves, size);

            int numPerSlave = size / numSlaves;
            int tempStart = start;
            int tempEnd = tempStart + numPerSlave - 1;

            if(numPerSlave < 1) {
                numPerSlave = 1;
            }
    
            for (int i = 0; i < numSlaves - 1; i++) {
                // Sends start and end to slave
                Socket currSocket = slaves.get(i);
                DataOutputStream out = new DataOutputStream(currSocket.getOutputStream());
                out.writeInt(tempStart);
                out.writeInt(tempEnd);

                tempStart = tempEnd + 1;

                //Any excess goes to the final slave
                if (i == numSlaves - 2) {
                    tempEnd = end;
                }
                else
                    tempEnd += numPerSlave;
            }

            ArrayList<Integer> primes = new ArrayList<>();

            for(int i = tempStart; i <= tempEnd; i++) {
                if(check_prime(i))
                    primes.add(i);
            }

            primeCountSem.acquire();
            numPrimes += primes.size();
            primeCountSem.release();

            slaveListSem.release();
            readyToProcessSem.release();        // Might be another way to not use semaphores bc this is only needed for the first slave
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            sc.close();
        }
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
}

// Keeps accepting connections from slaves
class MasterThread extends Thread {
    public void run() {
        try {
            Master.serverSocket = new ServerSocket(Master.PORT);
            System.out.println("Server started");
    
            System.out.println("Waiting for slave server/s to connect...");
            while(true) {
                Socket newSlave = Master.serverSocket.accept();
                Master.slaveListSem.acquire();
                Master.slaves.add(newSlave);
                Master.slaveListSem.release();

                System.out.println("New slave! Slave count: " + Master.slaves.size());

                if(Master.slaves.size() == 1)
                    Master.readyToProcessSem.release();
            }
        }
        catch(UnknownHostException h) {
            System.out.println(h);
            return;
        }
        catch(IOException i) {
            System.out.println(i);
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// TODO: Maybe make a thread for each connected slave and wait for its returned prime count there
// class SlaveListenerThread extends Thread {
//     // waits for returned prime count from slaves
//     public void run() {
//         while (true) {
//             int receivedPrimeCount = MainMaster.in
//         }
//     }
// }