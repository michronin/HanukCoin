package il.ac.tau.cs.hanukcoin;


import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static il.ac.tau.cs.hanukcoin.HanukCoinUtils.*;


public class HanukCoinUtilsTest extends TestCase {
    private static void walletError(int numCoins, ArrayList<Block> chain, int wallet1) {
        for (int i = 0; i < numCoins; i++) {
            Block newBlock = null;
            Block prevBlock = chain.get(i);
            while (newBlock == null) {
                newBlock = mineCoinAttemptWallerError(wallet1, prevBlock, 10000000);
            }

            chain.add(newBlock);

        }
    }
    private static Block mineCoinAttemptWallerError(int myWalletNum, Block prevBlock, int attemptsCount) {
        int newSerialNum = prevBlock.getSerialNumber() + 1;
        byte[] prevSig = new byte[8];
        System.arraycopy(prevBlock.getBytes(), 24, prevSig, 0, 8);
        Block newBlock = Block.createNoSig(newSerialNum, myWalletNum, prevSig);
        return mineCoinAttemptInternal(newBlock, attemptsCount);
    }

    private static void correctChain(int numCoins, ArrayList<Block> chain, int wallet1, int wallet2) {
        for (int i = 0; i < numCoins; i++) {
            Block newBlock = null;
            Block prevBlock = chain.get(i);
            while (newBlock == null) {
                newBlock = mineCoinAttempt(wallet1, prevBlock, 10000000);
            }
            int tmp = wallet1;
            wallet1 = wallet2;
            wallet2 = tmp;
            chain.add(newBlock);
        }
    }

    public void test_numBits() {
        assertEquals(0, HanukCoinUtils.numBits(0));
        assertEquals(6, HanukCoinUtils.numBits(32));
        assertEquals(5, HanukCoinUtils.numBits(31));
        assertEquals(3, HanukCoinUtils.numBits(5));
    }

    public void test_intFromIntoBytes() {
        byte[] data = new byte[9];
        int x = 0xDeadBeef;
        HanukCoinUtils.intIntoBytes(data, 4, x);
        assertEquals(x, HanukCoinUtils.intFromBytes(data, 4));
        x = -1411231107;
        HanukCoinUtils.intIntoBytes(data, 0, x);
        assertEquals(x, HanukCoinUtils.intFromBytes(data, 0));
        // This is the "Java way" to do intFromBytes() - I chose not to use it.
        // DataInputStream uses big endian. Checking this is indeed the same.
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        try {
            assertEquals(x, dis.readInt());
        } catch (IOException e) {
            assert (false);
        }
        // Checking timing - checking if indeed the choice to use the "less Java way" was correct
        long t1 = System.nanoTime();
        int i;
        final int passes = 100000;
        for (i = 0; i < passes; i++) {
            assertEquals(x, HanukCoinUtils.intFromBytes(data, 0));
        }
        long t2 = System.nanoTime();
        try {
            for (i = 0; i < passes; i++) {
                dis = new DataInputStream(new ByteArrayInputStream(data));
                assertEquals(x, dis.readInt());
            }
        } catch (IOException e) {
            assert (false);
        }
        long t3 = System.nanoTime();
        long delta1 = t2 - t1;
        long delta2 = t3 - t2;
        System.out.printf("time intFromIntoBytes=%d time DataInputStream=%d%n", delta1, delta2);
        assert (delta2 * 10 > 12 * delta1);  // check we are 20% better
    }

    public void test_mine() {
        Block genesis = HanukCoinUtils.createBlock0forTestStage();
        long t1 = System.nanoTime();
        Block newBlock = mineCoinAttempt(HanukCoinUtils.walletCode("TEST"), genesis, 10000000);
        long t2 = System.nanoTime();
        System.out.printf("mining took =%d milli%n", (int) ((t2 - t1) / 10000000));

        assert newBlock != null;
        System.out.println(newBlock.binDump());
    }

    public void test_numberOfZerosForPuzzle() {
        assertEquals(30, HanukCoinUtils.numberOfZerosForPuzzle(1000));
    }

    public void test_walletCode() {
        assertEquals(-1411231107, HanukCoinUtils.walletCode("Foo Bar,Bar Vaz"));
    }

    public void test_validate() {
        int numCoins = 5;
        ArrayList<Block> chain = new ArrayList<>();
        Block genesis = HanukCoinUtils.createBlock0forTestStage();
        chain.add(genesis);
        int wallet1 = HanukCoinUtils.walletCode("TEST1");
        int wallet2 = HanukCoinUtils.walletCode("TEST2");

        correctChain(numCoins, chain, wallet1, wallet2);
        assertTrue(HanukCoinUtils.validate(chain));

        // wallet error
        chain.clear();
        chain.add(genesis);

        walletError(numCoins, chain, wallet1);
        assertFalse(HanukCoinUtils.validate(chain));

        // first not genesis error
        Block err = chain.get(2);
        chain.clear();

        chain.add(err);
        assertFalse(HanukCoinUtils.validate(chain));

    }
}