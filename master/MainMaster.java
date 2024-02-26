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
    static Semaphore readyToProcessSem = new Semaphore(0);          // Used to make sure there is at least one slave connected
    static Semaphore slaveListSem = new Semaphore(1);               // Used to ensure no interference on slaves list

  public static void main(String[] args) {
    MasterThread master = new MasterThread();
    master.start();

    Scanner sc = new Scanner(System.in);

    while (true) {
        // Gets user input
        System.out.print("Enter start: ");
        MainMaster.start = sc.nextInt();
        System.out.print("Enter end: ");
        MainMaster.end = sc.nextInt();
        
        // Divides range and gives to slave servers
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
    
            for (int i = 0; i < numSlaves; i++) {
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

// Keeps accepting connections from slaves
class MasterThread extends Thread {
    public void run() {
        try {
            MainMaster.serverSocket = new ServerSocket(MainMaster.PORT);
            System.out.println("Server started");
    
            System.out.println("Waiting for slave server/s to connect...");
            while(true) {
                Socket newSlave = MainMaster.serverSocket.accept();
                MainMaster.slaveListSem.acquire();
                MainMaster.slaves.add(newSlave);
                MainMaster.slaveListSem.release();

                System.out.println("New slave! Slave count: " + MainMaster.slaves.size());

                if(MainMaster.slaves.size() == 1)
                    MainMaster.readyToProcessSem.release();
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