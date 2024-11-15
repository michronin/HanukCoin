package il.ac.tau.cs.hanukcoin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static il.ac.tau.cs.hanukcoin.HanukCoinUtils.mineCoinAtteempt;

public class Main {
    public static void main(String[] args) {
        int numCoins = Integer.parseInt(args[0]);
        System.out.println(String.format("Mining %d coins...", numCoins));
        ArrayList<Block> chain = new ArrayList<>();
        Block genesis = HanukCoinUtils.createBlock0forTestStage();
        chain.add(genesis);
        int wallet1 = HanukCoinUtils.walletCode("TEST1");
        int wallet2 = HanukCoinUtils.walletCode("TEST2");

        for (int i = 0; i < numCoins; i++) {
            long t1 = System.nanoTime();
            Block newBlock = null;
            Block prevBlock = chain.get(i);
            while (newBlock == null) {
                newBlock = mineCoinAtteempt(wallet1, prevBlock, 10000000);
            }
            int tmp = wallet1;
            wallet1 = wallet2;
            wallet2 = tmp;
            if (newBlock.checkValidNext(prevBlock) != Block.BlockError.OK) {
                throw new RuntimeException("BAD BLOCK");
            }
            chain.add(newBlock);
            long t2 = System.nanoTime();
            System.out.println(String.format("mining took =%d milli", (int) ((t2 - t1) / 10000000)));
            System.out.println(newBlock.binDump());

        }

        System.out.println(validate(chain));
    }

    private static boolean validate(List<Block> chain) {
        for (int i = 0; i < chain.size(); i++) {
            Block current = chain.get(i);

            // checking if previous block signature is matching the signature that the current block holds.
            if (i != 0) {
                if (current.checkValidNext(chain.get(i - 1)) != Block.BlockError.OK) {
                    return false;
                }
            }
            // checking if first block is genesis block
            else {
                if (!(current.equals(HanukCoinUtils.createBlock0forTestStage()))) {
                    return false;
                }
            }

            // calculating the signature
            byte[] actualCurrentSignature = current.calcSignature();
            System.out.println("hello");
            System.out.println(Arrays.toString(actualCurrentSignature));



        }

        return true;
    }
}
