package il.ac.tau.cs.hanukcoin;

public class Main {
    public static void main(String[] args) {
        Block genesis = HanukCoinUtils.createBlock0forTestStage();
        Block second = Block.createNoSig(2, 2, genesis.getBytes());
        System.out.println(second);
    }
}
