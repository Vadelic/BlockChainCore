public interface ValidateBlock {

    boolean isValidBlock();
    boolean isValidPreviousBlock(ValidateBlock previousBlock);
}
