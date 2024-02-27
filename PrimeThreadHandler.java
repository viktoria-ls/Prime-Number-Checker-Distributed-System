import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/* Handles distributing ranges to threads and adding to prime list */
public class PrimeThreadHandler {
    public static void start(int start, int end, int numThreads, ArrayList<Integer> primes) {       // Is using a static method an thread safety issue?
        Semaphore primeListSem = new Semaphore(1);
        ArrayList<Thread> primeThreads = new ArrayList<Thread>();

        int size = end - start + 1;
        numThreads = Math.min(numThreads, size);

        int numPerThread = size / numThreads;
        int tempStart = start;
        int tempEnd = tempStart + numPerThread - 1;

        numPerThread = Math.max(1, numPerThread);

        for (int i = 0; i < numThreads; i++) {
            PrimeThread primeThread = new PrimeThread(tempStart, tempEnd, primes, primeListSem);
                
            // Adds to list of Threads and starts newly created one
            primeThreads.add(primeThread);
            primeThreads.get(primeThreads.size() - 1).start();

            tempStart = tempEnd + 1;

            // Any excess goes to the final thread
            if (i == numThreads) {
                tempEnd = end;
            }
            else
                tempEnd += numPerThread;
        }

        // Checks if all threads are done
        for (Thread thread: primeThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // TODO: Do sanity checking here
    }
}
