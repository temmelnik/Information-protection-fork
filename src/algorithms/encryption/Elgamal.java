package algorithms.encryption;

import algorithms.cryptosys_public_key.DiffieHellman;
import algorithms.cryptosys_public_key.PowFast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Elgamal {
    private static long P; //public parameter P
    private static long G; //public parameter G

    private long C; //private number of subscriber B
    private long D; //public number of subscriber B
    private long R;

    public static void setInitialValues()
    {
        DiffieHellman Df = new DiffieHellman();
        P = Df.getP();
        G = Df.getG();
    }

    public Elgamal()
    {
        SecureRandom sr = new SecureRandom();
        sr.setSeed(System.currentTimeMillis());
        C = sr.nextInt((int)P - 2) + 1;
        D = PowFast.calculate(G, C, P);
    }

    public List<Long> calculateSendMassage(String path, long publicKeyD)
    {
        List<Long> E = new ArrayList<>();

        SecureRandom sr = new SecureRandom();
        sr.setSeed(System.currentTimeMillis());
        long K = sr.nextInt((int)P - 2 - 1 ) + 1;
        R = PowFast.calculate(G,K,P);

        long intermediateValue = PowFast.calculate(publicKeyD, K, P);


        byte[] byteArray = Shamir.getBytesFromFile(path);

        for (byte a : byteArray) {
            E.add(PowFast.calculate(a * intermediateValue, 1, P));
        }
        return E;
    }
    public void calculateReceiveMassage(long R, List<Long> E, String path)
    {
        byte[] buffer = new byte[E.size()];
        long intermediateValue = PowFast.calculate(R,P - 1 - C, P);

        for (int i = 0; i < E.size(); i++) {
            buffer[i] = (byte) PowFast.calculate(E.get(i) * intermediateValue, 1, getP());
        }
        Shamir.getFileFromBytes(path,buffer);
    }


    public long getP()
    {
        return P;
    }
    public long getD()
    {
        return D;
    }
    public long getR()
    {
        return R;
    }
}
