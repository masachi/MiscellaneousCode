import java.util.*;

public class ProblemSet {
    static boolean[] composite;
    static int[] primes;

    static void initPrimes() {
        int MAXN = 10000;
        int sqrtN = (int) Math.sqrt(MAXN);
        composite = new boolean[MAXN];
        composite[0] = composite[1] = true;
        for (int p = 2; p <= sqrtN; p++) {
            if (!composite[p]) {
                for (int x = 2 * p; x < MAXN; x += p) {
                    composite[x] = true;
                }
            }
        }
        int primeNo = 0;
        for (boolean b : composite) {
            if (!b) primeNo++;
        }
        primes = new int[primeNo];
        primeNo = 0;
        for (int p = 2; p < MAXN; p++) {
            if (!composite[p]) {
                primes[primeNo++] = p;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        initPrimes();
        Random random = new Random();
        StringBuffer input = new StringBuffer();
        int T = random.nextInt(9000);
        while (T <= 0) {
            T = random.nextInt(9000);
        }
        input.append(T + "\n");
        //StringBuilder output = new StringBuilder(10*T);
        StringBuffer output = new StringBuffer();
        for (int t = 0; t < T; t++) {
            int N = random.nextInt(6000);
            boolean mike;
            while (N <= 1) {
                N = random.nextInt(6000);
            }
            input.append(N + "\n");
            if (N == 2 || N == 17) {
                mike = true;
            } else if (N == 1 || N == 16 || N == 34 || N == 289) {
                mike = false;
            } else {
                mike = !isPrime(N);
            }
            output.append(mike ? "Mike\n" : "Tom\n");
        }
        System.out.println(input);
        System.out.print(output);
        int i1 = 8;
        DownloadModule.download_example(input.toString(), "C:\\Users\\sdlds\\Documents\\OnlineJudgeProblem\\Game of Divisors\\input" + i1 + ".in");
        DownloadModule.download_example(output.toString(), "C:\\Users\\sdlds\\Documents\\OnlineJudgeProblem\\Game of Divisors\\input" + i1 + ".out");
    }

    static boolean isPrime(int N) {
        for (int prime : primes) {
            if (N == prime) {
                return true;
            } else if (N%prime == 0) {
                return false;
            }
        }
        return true;
    }

}