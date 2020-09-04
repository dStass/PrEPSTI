package PRSP.PrEPSTI.mdll;
public class MDLLBackwardIterator<T> implements MDLLIterator<T>{
    private MDLLNode<T> head;
    private MDLLNode<T> last;
    private MDLLNode<T> curr;

    public MDLLBackwardIterator(MDLLNode<T> head, MDLLNode<T> last) {
        this.head = head;
        this.last = last;
        this.curr = this.last;
    }

    public boolean hasNext() {
        if (this.curr.getId().equals(this.head.getId())
        || this.curr.getPrev().getId().equals(this.head.getId()))
            return false;
        return true;
    }

    public T next() {
        this.curr = this.curr.getPrev();
        if (this.curr.getId().equals(MDLL.HEAD_ID)) return null;
        return this.curr.getObject();
    }

    public void iterateBack() {
        this.curr = this.curr.getNext();
    }
}