package PRSP.PrEPSTI.mdll;

public class MDLLNode<T> {
    private Integer id = null;
    private T object = null;
    private MDLLNode<T> prev = null;
    private MDLLNode<T> next = null;

    public MDLLNode(int id, T object) {
        this.id = id;
        this.object = object;
    }

    public void setPrev(MDLLNode<T> prevNode) {
        this.prev = prevNode;
    }

    public void setNext(MDLLNode<T> nextNode) {
        this.next = nextNode;
    }

    public Integer getId() {
        return this.id;
    }

    public MDLLNode<T> getNext() {
        return this.next;
    }

    public MDLLNode<T> getPrev() {
        return this.prev;
    }

    public T getObject() {
        return this.object;
    }

    /**
     * set every field to null
     */
    public void destroy() {
        this.id = null;
        this.object = null;
        this.prev = null;
        this.next = null;
    }
}