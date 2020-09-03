package PRSP.PrEPSTI.mdll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * ADT containing a HashMap pointing to a Doubly Linked List of a given type
 * keys cannot be negative
 */
public class MDLL<T> implements Iterable<T> {

    // reserved ids
    // public static final String HEAD_ID = "_RESERVED_HEAD";
    // public static final String LAST_ID = "_RESERVED_LAST";

    public static final int HEAD_ID = -1;
    public static final int LAST_ID = -2;

    // number of reserved nodes = 2 (reserved head and last)
    public static final int NUM_RESERVED_NODES = 2;

    // pointers
    private MDLLNode<T> head = null;
    private MDLLNode<T> last = null;
    // private MDLLNode<T> curr = null;

    // mapping from nodeId (int/str) --> MDLLNode
    // private ConcurrentHashMap<String, MDLLNode<T>> mapping = null;
    private HashMap<Integer, MDLLNode<T>> mapping = null;

    /**
     * 
     * constructor for MDLL containing two dummy nodes (reserved head and reserved
     * last)
     */
    public MDLL() {
        this.mapping = new HashMap<Integer, MDLLNode<T>>();

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
        // this.curr = this.head;

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

    // public T get(int nodeId) {
    //     return this.get(String.valueOf(nodeId));
    // }

    public T get(int nodeId) {
        return this.mapping.get(nodeId).getObject();
    }

    /*
     * * * * * * * * * * * * ADD * * * * * * * * * * *
     */

    // /**
    //  * add a new node based on an Id
    //  * 
    //  * @param nodeId
    //  * @param object
    //  */
    // public void add(int nodeId, T object) {
    //     this.add(String.valueOf(nodeId), object);
    // }

    /**
     * add a new node based on an Id
     * 
     * @param nodeId
     * @param object
     */
    public void add(int nodeId, T object) {
        addBefore(nodeId, object, this.last.getId());
    }

    /**
     * 
     * @param newId
     * @param newObject
     * @param beforeId
     */
    private void addBefore(int nodeId, T object, int afterId) {
        MDLLNode<T> newNode = new MDLLNode<T>(nodeId, object);
        this.mapping.put(nodeId, newNode);

        MDLLNode<T> after = this.mapping.get(afterId);
        MDLLNode<T> before = after.getPrev();

        before.setNext(newNode);
        newNode.setPrev(before);
        newNode.setNext(after);
        after.setPrev(newNode);
    }

    /*
     * * * * * * * * * * * * CONTAINS * * * * * * * * * * *
     */

    // /**
    //  * boolean check on whether a particular nodeId exists
    //  * 
    //  * @param nodeId
    //  * @return boolean
    //  */
    // public boolean contains(int nodeId) {
    //     return this.contains(String.valueOf(nodeId));
    // }

    /**
     * boolean check on whether a particular nodeId exists
     * 
     * @param nodeId
     * @return boolean
     */
    public boolean contains(int nodeId) {
        if (this.mapping.containsKey(nodeId))
            return true;
        else
            return false;
    }

    /*
     * * * * * * * * * * * * REMOVE * * * * * * * * * * *
     */    

    // /**
    //  * removes a particular node based on given id assumes nodeId exists
    //  * 
    //  * @param nodeId
    //  * @return
    //  */
    // public boolean remove(int nodeId) {
    //     boolean removePossible = this.remove(String.valueOf(nodeId));
    //     // if (!removePossible) System.out.println("remove failed");
    //     return removePossible;
    // }

    /**
     * for a successful removal if nodeId exists, return true otherwise, return
     * false
     * 
     * @param nodeId
     * @return
     */
    public boolean remove(int nodeId) {

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

    // public int indexOf(int nodeId) {
    //     return indexOf(String.valueOf(nodeId));
    // }

    public int indexOf(int nodeId) {
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

    // /**
    //  * return a forward iterator from a particular nodeId assume valid nodeId
    //  * 
    //  * @param nodeId
    //  * @return
    //  */
    // public MDLLForwardIterator<T> getForwardIterator(int nodeId) {
    //     return this.getForwardIterator(String.valueOf(nodeId));
    // }

    public MDLLForwardIterator<T> getForwardIterator(int nodeId) {
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

    // public MDLLBackwardIterator<T> getBackwardIterator(int nodeId) {
    //     return this.getBackwardIterator(String.valueOf(nodeId));
    // }

    public MDLLBackwardIterator<T> getBackwardIterator(int nodeId) {
        if (!this.mapping.containsKey(nodeId))
            return null;
        else
            return new MDLLBackwardIterator<T>(this.head, this.mapping.get(nodeId));
    }

    /*
     * * * * * * * * * * * * CONVERSIONS * * * * * * * * * * *
     */    
    
    public ArrayList<T> toArrayList() {
        ArrayList<T> toReturn = new ArrayList<T>(size());
        for (T obj : this) toReturn.add(obj);
        return toReturn;
    }

    /**
     * @return returns a shuffled MDLL in one passing
     */
    public MDLL<T> returnShuffled() {
        MDLL<T> shuffled = new MDLL<T>();
        ArrayList<Integer> idList = new ArrayList<Integer>(size() + 1);
        idList.add(LAST_ID);
        MDLLNode<T> current = head.getNext();
        while (!current.getId().equals(LAST_ID)) {
            int currentId = current.getId();
            int randomIndex = (int) (Math.random() * idList.size());
            int randomId = idList.get(randomIndex);
            shuffled.addBefore(currentId, current.getObject(), randomId);
            idList.add(currentId);
            current = current.getNext();
        }

        return shuffled;
    }

    /**
     * Generate a stream of T objects and remove nulls
     * @return
     */
    public Stream<T> getStream() {
        return mapping.keySet().parallelStream().map(nodeId ->
        {
            return mapping.get(nodeId).getObject();
        })
        .filter(obj -> obj != null)
        ;
    }
    public HashMap<Integer, MDLLNode<T>> getInternalMap() {
        return mapping;
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