package PRSP.PrEPSTI.mdll;

import java.util.HashMap;

import PRSP.PrEPSTI.mdll.MDLLNode;

public class MDLLForwardIterator<T> implements MDLLIterator<T>{
        
    private HashMap<String, MDLLNode<T>> mapping;
    private MDLLNode<T> head;
    private MDLLNode<T> last;
    private MDLLNode<T> dummyHead;
    private MDLLNode<T> curr;

    public MDLLForwardIterator(MDLLNode<T> head, MDLLNode<T> last, HashMap<String, MDLLNode<T>> mapping) {
        this.head = head;
        this.last = last;
        this.dummyHead = new MDLLNode<T>("_RESERVED", null);
        this.dummyHead.setNext(this.head);
        this.curr = this.dummyHead;
        this.mapping = mapping;
    }

    /**
     * 
     * @return
     */
    public boolean hasNext() {
        if (this.curr == null || this.curr.getNext() == null) return false;
        return true;
    }


    public T getNextAndIterate() {
        this.curr = this.curr.getNext();
        return (T) this.curr.getObject();
    }

    public void iterateBack() {
        this.curr = this.curr.getPrev();
    }
    

}