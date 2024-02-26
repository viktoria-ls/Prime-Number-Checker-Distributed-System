import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.io.*;

public class MainMaster {
    static ServerSocket serverSocket = null;
    static int PORT = 1337;

    static int start = 2;
    static int end = 100;
    static int NUM_THREADS = 1;

    static ArrayList<Socket> slaves = new ArrayList<>();
    static Semaphore readyToProcessSem = new Semaphore(-1);
    static Semaphore slaveListSem = new Semaphore(1);

  public static void main(String[] args) {
    ClientThread client = new ClientThread();       // Gets user input
    client.start();
    MasterThread master = new MasterThread();       // Lets slaves connect
    master.start();

    // Make master not distribute tasks until there is at least one slave and user input is okay
    while (true) {
        try {
            readyToProcessSem.acquire();

            slaveListSem.acquire();
            int numSlaves = slaves.size();
            int size = end - start + 1;
            numSlaves = Math.min(numSlaves, size);

            int numPerSlave = size / numSlaves;
            int tempStart = start;
            int tempEnd = tempStart + numPerSlave - 1;

            if(numPerSlave < 1) {
                numPerSlave = 1;
            }
    
            // TODO: figure out how to divide range
            for (int i = 0; i < numSlaves; i++) {
                tempStart = tempEnd + 1;

                //Any excess goes to the final thread
                if (i == numSlaves - 2) {
                    tempEnd = end;
                }
                else
                    tempEnd += numPerSlave;

                // slaves.get(i)
            }

            slaveListSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
  }
}

// Client thread gets user input
class ClientThread extends Thread {
    

    public void run() {
        while (true) {
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter start: ");
            MainMaster.start = sc.nextInt();
            System.out.print("Enter end: ");
            MainMaster.end = sc.nextInt();
            System.out.print("Enter number of threads: ");
            MainMaster.NUM_THREADS = sc.nextInt();

            sc.close();

            // Releases 1/2 of needed permits for processing to start
            MainMaster.readyToProcessSem.release();

            try {
                // waits until request is fully processed
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Keeps accepting connections from slaves
class MasterThread extends Thread {
    public void run() {
        try {
            MainMaster.serverSocket = new ServerSocket(MainMaster.PORT);
            System.out.println("Server started");

            // Waits for at least one slave before releasing 1/2 needed permits for semaphore
            System.out.println("Waiting for slave server/s to connect...");

            MainMaster.slaveListSem.acquire();
            MainMaster.slaves.add(MainMaster.serverSocket.accept());
            MainMaster.slaveListSem.release();

            MainMaster.readyToProcessSem.release();
            System.out.println("New slave! Slave count: " + MainMaster.slaves.size());
    
            while(true) {
                MainMaster.slaveListSem.acquire();
                MainMaster.slaves.add(MainMaster.serverSocket.accept());
                MainMaster.slaveListSem.release();

                System.out.println("New slave! Slave count: " + MainMaster.slaves.size());
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}