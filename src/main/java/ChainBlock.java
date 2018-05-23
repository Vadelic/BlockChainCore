public interface ChainBlock {

    boolean isValidBlock();
    boolean isValidPreviousBlock(ChainBlock previousBlock);
}
