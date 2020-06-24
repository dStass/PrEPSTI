package PRSP.PrEPSTI.mdll;

public class MDLLNode<T> {
    private String id = null;
    private T object = null;
    private MDLLNode<T> prev = null;
    private MDLLNode<T> next = null;

    public MDLLNode(String id, T object) {
        this.id = id;
        this.object = object;
    }

    public void setPrev(MDLLNode<T> prevNode) {
        this.prev = prevNode;
    }

    public void setNext(MDLLNode<T> nextNode) {
        this.next = nextNode;
    }

    public String getId() {
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
}