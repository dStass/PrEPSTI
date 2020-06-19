package PRSP.PrEPSTI.mdll;

import java.util.ArrayList;
import java.util.HashMap;

public class MDLL<T> {
    MDLLNode<T> head = null;
    MDLLNode<T> last = null;
    MDLLNode<T> curr = null;

    HashMap<String, MDLLNode<T>> mapping = null;
    ArrayList<MDLLForwardIterator<T>> currentIterators = null;
    

    public MDLL() {
        this.mapping = new HashMap<String, MDLLNode<T>>();
        this.currentIterators = new ArrayList<MDLLForwardIterator<T>>();
    }

    public void addNextNode(int nodeId, T object) {
        this.addNextNode(String.valueOf(nodeId), object);
    }

    public void addNextNode(String nodeId, T object) {
        MDLLNode<T> newNode = new MDLLNode<T>(nodeId, object);
        mapping.put(nodeId, newNode);

        if (head == null) {
            this.head = newNode;
            this.last = newNode;
        } else if (head.getNext() == null) {
            this.head.setNext(newNode);
            newNode.setPrev(this.head);
            this.last = newNode;
        } else {
            this.last.setNext(newNode);
            newNode.setPrev(this.last);
            this.last = newNode;
        }
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
        if (this.head.getId().equals(nodeId)) {
            this.head = this.head.getNext();
            this.head.setPrev(null);
        } else if (this.last.getId().equals(nodeId)) {
            this.last = this.last.getPrev();
            this.last.setNext(null);
        } else {
            MDLLNode<T> temp = toRemove.getPrev();
            toRemove.setPrev(toRemove.getNext());
            toRemove.getNext().setPrev(temp);
        }

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