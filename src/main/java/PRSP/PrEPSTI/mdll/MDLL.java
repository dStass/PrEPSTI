package PRSP.PrEPSTI.mdll;
import java.util.HashMap;
/**
 * ADT containing a HashMap pointing to a Doubly Linked List of
 * a given type
 */
public class MDLL<T> {

    // reserved ids 
    public static final String HEAD_ID = "_RESERVED_HEAD";
    public static final String LAST_ID = "_RESERVED_LAST";

    // pointers
    private MDLLNode<T> head = null;
    private MDLLNode<T> last = null;
    private MDLLNode<T> curr = null;

    // mapping from nodeId (int/str) --> MDLLNode
    private HashMap<String, MDLLNode<T>> mapping = null;

    /**
     * 
     * constructor for MDLL containing two dummy nodes
     * (reserved head and reserved last)
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
     * Returns the amount of nodes 
     * This is the size of mapping hashmap subtracted by 2
     * (reserved head and reserved last nodes)
     */
    public int size() {
        return this.mapping.size() - 2;
    }

    /**
     * add a new node based on an Id
     * @param nodeId
     * @param object
     */
    public void addNextNode(int nodeId, T object) {
        this.addNextNode(String.valueOf(nodeId), object);
    }

    /**
     * add a new node based on an Id
     * @param nodeId
     * @param object
     */
    public void addNextNode(String nodeId, T object) {
        MDLLNode<T> newNode = new MDLLNode<T>(nodeId, object);
        mapping.put(nodeId, newNode);

        this.curr.setNext(newNode);
        newNode.setPrev(this.curr);
        this.curr = this.curr.getNext();
        this.curr.setNext(this.last);
        this.last.setPrev(this.curr);
    }

    /**
     * boolean check on whether a particular nodeId exists
     * @param nodeId
     * @return boolean
     */
    public boolean hasNode(int nodeId) {
        return this.hasNode(String.valueOf(nodeId));
    }

    /**
     * boolean check on whether a particular nodeId exists
     * @param nodeId
     * @return boolean
     */
    public boolean hasNode(String nodeId) {
        if (this.mapping.containsKey(nodeId)) return true;
        else return false;
    }

    /**
     * removes a particular node based on given id
     * assumes nodeId exists
     * @param nodeId
     * @return
     */
    public boolean removeNode(int nodeId) {
        return this.removeNode(String.valueOf(nodeId));
    }

    /**
     * for a successful removal if nodeId exists, return true
     * otherwise, return false
     * @param nodeId
     * @return
     */
    public boolean removeNode(String nodeId) {
        
        // if node doesn't exist, return false
        if (!this.mapping.containsKey(nodeId)) return false;

        // identify node to be removed and remove it from the mapping
        MDLLNode<T> toRemove = this.mapping.get(nodeId);
        this.mapping.remove(nodeId);

        // point nodes on either side of nodeId to each other
        MDLLNode<T> temp = toRemove.getPrev();
        toRemove.getPrev().setNext(toRemove.getNext());
        toRemove.getNext().setPrev(temp);

        // successful removal
        return true;
    }

    /* * * * * * * * * * * *
     *  FORWARD ITERATOR   *
     * * * * * * * * * * * */

    /**
     * return an iterator moving forward from the start of MDLL
     * @return MDLLForward
     */
    public MDLLForwardIterator<T> getForwardIterator() {
        return new MDLLForwardIterator<T>(this.head, this.last);
    }

    /**
     * return a forward iterator from a particular nodeId
     * assume valid nodeId
     * @param nodeId
     * @return
     */
    public MDLLForwardIterator<T> getForwardIterator(int nodeId) { 
        return this.getForwardIterator(String.valueOf(nodeId));
    }    

    public MDLLForwardIterator<T> getForwardIterator(String nodeId) {
        if (!this.mapping.containsKey(nodeId)) return null;
        else return new MDLLForwardIterator<T>(this.mapping.get(nodeId), this.last);
    }

    /* * * * * * * * * * * *
     *  BACKWARD ITERATOR  *
     * * * * * * * * * * * */

    public MDLLBackwardIterator<T> getBackwardIterator() {
        return new MDLLBackwardIterator<T>(this.head, this.last);
    }

    public MDLLBackwardIterator<T> getBackwardIterator(int nodeId) { 
        return this.getBackwardIterator(String.valueOf(nodeId));
    }

    public MDLLBackwardIterator<T> getBackwardIterator(String nodeId) {
        if (!this.mapping.containsKey(nodeId)) return null;
        else return new MDLLBackwardIterator<T>(this.head, this.mapping.get(nodeId));
    }

}