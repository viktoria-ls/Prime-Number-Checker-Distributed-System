import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/* Adds prime numbers from range to primeList */
public class PrimeThread extends Thread {
    int startInteger;
    int endInteger;
    ArrayList<Integer> localPrimeList = new ArrayList<>();
    ArrayList<Integer> primeList = new ArrayList<Integer>();
    Semaphore primeListSem = null;

    public PrimeThread(int start, int end, ArrayList<Integer> primeList, Semaphore primeListSem) {
        this.startInteger = start;
        this.endInteger = end;
        this.primeList = primeList;
        this.primeListSem = primeListSem;
    }

    public void run() {
        // Checks if each integer in range is a prime
        for(int current_num = startInteger; current_num <= endInteger; current_num++) {
            if(check_prime(current_num)) {
                localPrimeList.add(current_num);
            }
        }
        
        // Adds primes from this range to slave's main range
        try {
            primeListSem.acquire();
            primeList.addAll(localPrimeList);
            primeListSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    // Checks if integer is a prime number
    public static boolean check_prime(int n) {
        for(int i = 2; i * i <= n; i++) {
            if(n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
