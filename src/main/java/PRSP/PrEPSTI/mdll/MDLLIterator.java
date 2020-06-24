package PRSP.PrEPSTI.mdll;

public interface MDLLIterator<T> {
    public boolean hasNext();
    public T getNextAndIterate();
    public void iterateBack();
}