package PRSP.PrEPSTI.mdll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * ADT containing a HashMap pointing to a Doubly Linked List of a given type
 */
public class MDLL<T> implements Iterable<T> {

    // reserved ids
    public static final String HEAD_ID = "_RESERVED_HEAD";
    public static final String LAST_ID = "_RESERVED_LAST";

    // number of reserved nodes = 2 (reserved head and last)
    public static final int NUM_RESERVED_NODES = 2;

    // pointers
    private MDLLNode<T> head = null;
    private MDLLNode<T> last = null;
    private MDLLNode<T> curr = null;

    // mapping from nodeId (int/str) --> MDLLNode
    private HashMap<String, MDLLNode<T>> mapping = null;

    /**
     * 
     * constructor for MDLL containing two dummy nodes (reserved head and reserved
     * last)
     */
    public MDLL() {
        this.mapping = new HashMap<String, MDLLNode<T>>();

        // create dummy head
        MDLLNode<T> headNode = new MDLLNode<T>(MDLL.HEAD_ID, null);
        mapping.put(MDLL.HEAD_ID, headNode);
        this.head = headNode;

        // create dummy last
        MDLLNode<T> lastNode = new MDLLNode<T>(MDLL.LAST_ID, null);
        mapping.put(MDLL.LAST_ID, lastNode);
        this.last = lastNode;

        // linking step
        this.head.setNext(this.last);
        this.last.setPrev(this.head);
        this.curr = this.head;

    }

    /**
     * Returns the amount of nodes This is the size of mapping hashmap subtracted by
     * 2 (reserved head and reserved last nodes)
     */
    public int size() {
        return this.mapping.size() - MDLL.NUM_RESERVED_NODES;
    }

    /*
     * * * * * * * * * * * * GET * * * * * * * * * * *
     */    

    public T get(int nodeId) {
        return this.get(String.valueOf(nodeId));
    }

    public T get(String nodeId) {
        return this.mapping.get(nodeId).getObject();
    }

    /*
     * * * * * * * * * * * * ADD * * * * * * * * * * *
     */

    /**
     * add a new node based on an Id
     * 
     * @param nodeId
     * @param object
     */
    public void add(int nodeId, T object) {
        this.add(String.valueOf(nodeId), object);
    }

    /**
     * add a new node based on an Id
     * 
     * @param nodeId
     * @param object
     */
    public void add(String nodeId, T object) {
        MDLLNode<T> newNode = new MDLLNode<T>(nodeId, object);
        mapping.put(nodeId, newNode);

        this.curr.setNext(newNode);
        newNode.setPrev(this.curr);
        this.curr = this.curr.getNext();
        this.curr.setNext(this.last);
        this.last.setPrev(this.curr);
    }

    /*
     * * * * * * * * * * * * CONTAINS * * * * * * * * * * *
     */

    /**
     * boolean check on whether a particular nodeId exists
     * 
     * @param nodeId
     * @return boolean
     */
    public boolean contains(int nodeId) {
        return this.contains(String.valueOf(nodeId));
    }

    /**
     * boolean check on whether a particular nodeId exists
     * 
     * @param nodeId
     * @return boolean
     */
    public boolean contains(String nodeId) {
        if (this.mapping.containsKey(nodeId))
            return true;
        else
            return false;
    }

    /*
     * * * * * * * * * * * * REMOVE * * * * * * * * * * *
     */    

    /**
     * removes a particular node based on given id assumes nodeId exists
     * 
     * @param nodeId
     * @return
     */
    public boolean remove(int nodeId) {
        boolean removePossible = this.remove(String.valueOf(nodeId));
        // if (!removePossible) System.out.println("remove failed");
        return removePossible;
    }

    /**
     * for a successful removal if nodeId exists, return true otherwise, return
     * false
     * 
     * @param nodeId
     * @return
     */
    public boolean remove(String nodeId) {

        // if node doesn't exist, return false
        if (!this.mapping.containsKey(nodeId))
            return false;

        // identify node to be removed and remove it from the mapping
        MDLLNode<T> toRemove = this.mapping.get(nodeId);
        this.mapping.remove(nodeId);

        // point nodes on either side of nodeId to each other
        MDLLNode<T> temp = toRemove.getPrev();
        toRemove.getPrev().setNext(toRemove.getNext());
        toRemove.getNext().setPrev(temp);

        // destroy temp node
        toRemove.destroy();

        // successful removal
        return true;
    }

    /*
     * * * * * * * * * * * * INDEX * * * * * * * * * * *
     */


    public int indexOf(T object) {
        MDLLForwardIterator<T> forwardIterator = this.getForwardIterator();
        int currentPosition = -1;
        while (forwardIterator.hasNext()) {
            T nextObject = forwardIterator.next();
            currentPosition += 1;
            if (object.equals(nextObject))
                return currentPosition;
        }

        // returns -1 if object does not exist
        return -1;
    }

    public int indexOf(int nodeId) {
        return indexOf(String.valueOf(nodeId));
    }

    public int indexOf(String nodeId) {
        T object = get(nodeId);
        return indexOf(object);
    }

    /*
     * * * * * * * * * * * * * * ITERATORS * * * * * * * * * * * *
     */

    @Override
    public Iterator<T> iterator() {
        return new MDLLForwardIterator<T>(this.head, this.last);
    }

    /*
     * * * * * * * * * * * * FORWARD ITERATOR * * * * * * * * * * *
     */

    /**
     * return an iterator moving forward from the start of MDLL
     * 
     * @return MDLLForward
     */
    public MDLLForwardIterator<T> getForwardIterator() {
        return new MDLLForwardIterator<T>(this.head, this.last);
    }

    /**
     * return a forward iterator from a particular nodeId assume valid nodeId
     * 
     * @param nodeId
     * @return
     */
    public MDLLForwardIterator<T> getForwardIterator(int nodeId) {
        return this.getForwardIterator(String.valueOf(nodeId));
    }

    public MDLLForwardIterator<T> getForwardIterator(String nodeId) {
        if (!this.mapping.containsKey(nodeId))
            return null;
        else
            return new MDLLForwardIterator<T>(this.mapping.get(nodeId), this.last);
    }

    /*
     * * * * * * * * * * * * BACKWARD ITERATOR * * * * * * * * * * *
     */

    public MDLLBackwardIterator<T> getBackwardIterator() {
        return new MDLLBackwardIterator<T>(this.head, this.last);
    }

    public MDLLBackwardIterator<T> getBackwardIterator(int nodeId) {
        return this.getBackwardIterator(String.valueOf(nodeId));
    }

    public MDLLBackwardIterator<T> getBackwardIterator(String nodeId) {
        if (!this.mapping.containsKey(nodeId))
            return null;
        else
            return new MDLLBackwardIterator<T>(this.head, this.mapping.get(nodeId));
    }

    /*
     * * * * * * * * * * * * CONVERSIONS * * * * * * * * * * *
     */    

    public ArrayList<T> toArrayList() {
        ArrayList<T> toReturn = new ArrayList<T>();
        for (T obj : this) {
            toReturn.add(obj);
        }
        return toReturn;
    }

    /*
     * * * * * * * * * * * * CLONE * * * * * * * * * * *
     */    

    @Override
    public MDLL<T> clone() {
        MDLL<T> toReturn = new MDLL<T>();
        MDLLNode<T> currentNode = head;
        while (currentNode != null) {
            toReturn.add(currentNode.getId(), currentNode.getObject());
            currentNode = currentNode.getNext();
        }
        return toReturn;
    }
}