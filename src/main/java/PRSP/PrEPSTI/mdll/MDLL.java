package PRSP.PrEPSTI.mdll;

import java.util.ArrayList;
import java.util.HashMap;

public class MDLL<T> {

    public static final String HEAD_ID = "_RESERVED_HEAD";
    public static final String LAST_ID = "_RESERVED_LAST";
    private MDLLNode<T> head = null;
    private MDLLNode<T> last = null;
    private MDLLNode<T> curr = null;

    private HashMap<String, MDLLNode<T>> mapping = null;
    private ArrayList<MDLLForwardIterator<T>> currentIterators = null;
    

    public MDLL() {
        this.mapping = new HashMap<String, MDLLNode<T>>();
        this.currentIterators = new ArrayList<MDLLForwardIterator<T>>();

        MDLLNode<T> headNode = new MDLLNode<T>(MDLL.HEAD_ID, null);
        mapping.put(MDLL.HEAD_ID, headNode);
        this.head = headNode;
        
        MDLLNode<T> lastNode = new MDLLNode<T>(MDLL.LAST_ID, null);
        mapping.put(MDLL.LAST_ID, lastNode);
        this.last = lastNode;

        this.head.setNext(this.last);
        this.last.setPrev(this.head);
        this.curr = this.head;

    }

    public void addNextNode(int nodeId, T object) {
        this.addNextNode(String.valueOf(nodeId), object);
    }

    public void addNextNode(String nodeId, T object) {
        MDLLNode<T> newNode = new MDLLNode<T>(nodeId, object);
        mapping.put(nodeId, newNode);

        this.curr.setNext(newNode);
        newNode.setPrev(this.curr);
        this.curr = this.curr.getNext();
        this.curr.setNext(this.last);
        this.last.setPrev(this.curr);
    }

    public boolean hasNode(int nodeId) {
        return this.hasNode(String.valueOf(nodeId));
    }

    public boolean hasNode(String nodeId) {
        if (this.mapping.containsKey(nodeId)) return true;
        else return false;
    }

    public boolean removeNode(int nodeId) {
        return this.removeNode(String.valueOf(nodeId));
    }

    /**
     * 
     * @param nodeId
     * @return
     */
    public boolean removeNode(String nodeId) {
        if (!this.mapping.containsKey(nodeId)) return false;
        MDLLNode<T> toRemove = this.mapping.get(nodeId);
        MDLLNode<T> nextNode = toRemove.getNext();
        this.mapping.remove(nodeId);
        // if (this.head.getId().equals(nodeId)) {
        //     this.head = this.head.getNext();
        //     if (this.head != null) {
        //         // System.out.println("hi");
        //         this.head.setPrev(null);
        //     }
        // } else if (this.last.getId().equals(nodeId)) {
        //     this.last = this.last.getPrev();
        //     this.last.setNext(null);
        // } else {
        MDLLNode<T> temp = toRemove.getPrev();
        toRemove.getPrev().setNext(toRemove.getNext());
        toRemove.getNext().setPrev(temp);

        // toRemove.setPrev(toRemove.getNext());
        // toRemove.getNext().setPrev(temp);
        // }

        // if (this.curr.getId().equals(toRemove.getId())) this.curr = nextNode;

        return true;
    }

    public MDLLForwardIterator<T> getForwardIterator() {
        return new MDLLForwardIterator<T>(this.head, this.last, this.mapping);
    }

    public MDLLForwardIterator<T> getForwardIterator(int nodeId) { 
        return this.getForwardIterator(String.valueOf(nodeId));
    }    

    public MDLLForwardIterator<T> getForwardIterator(String nodeId) {
        if (!this.mapping.containsKey(nodeId)) return null;
        else return new MDLLForwardIterator<T>(this.mapping.get(nodeId), this.last, this.mapping);
    }

    public MDLLBackwardIterator<T> getBackwardIterator() {
        return new MDLLBackwardIterator<T>(this.head, this.last, this.mapping);
    }

    public MDLLBackwardIterator<T> getBackwardIterator(int nodeId) { 
        return this.getBackwardIterator(String.valueOf(nodeId));
    }

    public MDLLBackwardIterator<T> getBackwardIterator(String nodeId) {
        if (!this.mapping.containsKey(nodeId)) return null;
        else return new MDLLBackwardIterator<T>(this.head, this.mapping.get(nodeId), this.mapping);
    }

}