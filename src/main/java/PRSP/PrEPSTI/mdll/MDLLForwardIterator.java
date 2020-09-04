package PRSP.PrEPSTI.mdll;
public class MDLLForwardIterator<T> implements MDLLIterator<T>{
    private MDLLNode<T> head;
    private MDLLNode<T> last;
    private MDLLNode<T> curr;

    public MDLLForwardIterator(MDLLNode<T> head, MDLLNode<T> last) {
        this.head = head;
        this.last = last;    
        this.curr = this.head;
    }

    public boolean hasNext() {
        if (this.curr.getId().equals(this.last.getId())
        || this.curr.getNext().getId().equals(this.last.getId()))
            return false;
        return true;
    }

    public T next() {
        this.curr = this.curr.getNext();
        if (this.curr.getId().equals(MDLL.LAST_ID)) return null;
        return this.curr.getObject();
    }

    public void iterateBack() {
        this.curr = this.curr.getPrev();
    }
}