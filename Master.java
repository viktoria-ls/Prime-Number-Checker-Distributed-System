import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.io.*;

/* Handles user input and distributing ranges to connected slaves and self */
public class Master {
    static ServerSocket serverSocket = null;
    static int PORT = 1337;

    static int start = 2;
    static int end = 100;
    static int NUM_THREADS = 8;

    static Integer numPrimes = 0;

    static ArrayList<Socket> slaves = new ArrayList<>();
    static ArrayList<SlaveListener> slaveListeners = new ArrayList<>();
    static Semaphore readyToProcessSem = new Semaphore(0);          // Used to make sure there is at least one slave connected
    static Semaphore slaveListSem = new Semaphore(1);               // Used to ensure no interference on slaves list
    static Semaphore primeCountSem = new Semaphore(1);

  public static void main(String[] args) {
    SlaveAcceptThread accepter = new SlaveAcceptThread();
    accepter.start();

    Scanner sc = new Scanner(System.in);

    while (true) {
        // Gets user input
        System.out.print("Enter start: ");
        Master.start = sc.nextInt();
        System.out.print("Enter end: ");
        Master.end = sc.nextInt();
        
        // Divides range and gives to slave servers (and self)
        try {
            numPrimes = 0;
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

            
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < numSlaves - 1; i++) {         
                // Sends start and end to slave
                Socket currSocket = slaves.get(i);
                SlaveListener currListener = new SlaveListener(currSocket, numPrimes);
                slaveListeners.add(currListener);
                slaveListeners.get(slaveListeners.size() - 1).start();
                DataOutputStream out = new DataOutputStream(currSocket.getOutputStream());
                out.writeInt(tempStart);
                out.writeInt(tempEnd);

                tempStart = tempEnd + 1;

                //Any excess goes to the final slave (master server)
                if (i == numSlaves - 2) {
                    tempEnd = end;
                }
                else
                    tempEnd += numPerSlave;
            }

            ArrayList<Integer> primes = new ArrayList<>();
            PrimeThreadHandler.start(tempStart, tempEnd, NUM_THREADS, primes);
            numPrimes += primes.size();

            System.out.println(tempStart + ", " + tempEnd);
            System.out.println("Prime count (master): "  + primes.size());

            for(SlaveListener s : slaveListeners) {
                s.join();
            }


            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Total primes: " + (numPrimes));
            System.out.println("Total time (ms): " + (totalTime));

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
}

/* Keeps accepting connections from slaves */
class SlaveAcceptThread extends Thread {
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
            // h.printStackTrace();
            return;
        }
        catch(IOException i) {
            // i.printStackTrace();
            return;
        } catch (InterruptedException e) {
            // e.printStackTrace();
            return;
        }
    }
}