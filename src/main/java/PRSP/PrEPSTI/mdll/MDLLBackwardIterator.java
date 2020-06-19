package PRSP.PrEPSTI.mdll;

import java.util.HashMap;

import PRSP.PrEPSTI.mdll.MDLLNode;

public class MDLLBackwardIterator<T> implements MDLLIterator<T>{
        
    private HashMap<String, MDLLNode<T>> mapping;
    private MDLLNode<T> head;
    private MDLLNode<T> last;
    private MDLLNode<T> curr;

    public MDLLBackwardIterator(MDLLNode<T> head, MDLLNode<T> last, HashMap<String, MDLLNode<T>> mapping) {
        this.head = head;
        this.last = last;
        this.mapping = mapping;

        this.curr = this.last;
    }

    /**
     * 
     * @return
     */
    public boolean hasNext() {
        if (this.curr.getId().equals(this.head.getId())
        || this.curr.getPrev().getId().equals(this.head.getId()))
            return false;
        return true;
    }


    public T getNextAndIterate() {
        this.curr = this.curr.getPrev();
        if (this.curr.getId().equals(MDLL.HEAD_ID)) return null;
        return (T) this.curr.getObject();
    }

    public void iterateBack() {
        this.curr = this.curr.getNext();
    }
    

}