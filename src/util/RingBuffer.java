package util;

import java.util.Iterator;

/**
 * A quick and dirty circular buffer implementation.
 */
public final strictfp class RingBuffer<T> implements Iterable<T> {

  private final int capacity;
  private int size;
  private Node<T> head;

  public RingBuffer(int capacity) {
    this.capacity = capacity;
    size = 0;
    head = null;
  }

  public RingBuffer(T... vals) {
    this.capacity = vals.length;
    for(T t : vals) {
      add(t);
    }
  }

  public T get() {
    if (head != null) {
      return head.val;
    } else {
      return null;
    }
  }

  public T getAndRotate() {
    if (head != null) {
      T val = head.val;
      head = head.next;
      return val;
    } else {
      return null;
    }
  }

  public void add(T val) {
    if (capacity == size) {
      throw new RuntimeException("Can't add to full RingBuffer " + this);
    }
    size++;
    if (head == null) {
      head = new Node<>(val);
      head.previous = head;
      head.next = head;
    } else {
      Node<T> node = new Node<>(val);
      node.previous = head.previous;
      node.next = head;
      head.previous.next = node;
      head.previous = node;
    }
  }

  public boolean remove() {
    if (head == null) return false;
    return remove(head);
  }

  private boolean remove(Node<T> node) {
    if (size == 1) {
      head = null;
    } else {
      node.next.previous = node.previous;
      node.previous.next = node.next;
      if (node == head) {
        head = node.next;
      }
    }
    size--;
    return true;
  }

  public int getCapacity() {
    return capacity;
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean isFull() {
    return capacity == size;
  }

  @Override
  public Iterator<T> iterator() {
    return new RingBufferIterator();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[");
    int i = 0;
    for (T t : this) {
      sb.append(t);
      i++;
      if (i < size) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  private static class Node<T> {
    private T val;
    private Node<T> previous;
    private Node<T> next;

    private Node(T val) {
      this.val = val;
    }
  }

  private class RingBufferIterator implements Iterator<T> {

    private int count;
    private int removedCount;
    private Node<T> next;
    private boolean removed;

    private RingBufferIterator() {
      next = RingBuffer.this.head;
      removed = true; // can't remove until next() called at least once.
    }

    @Override
    public boolean hasNext() {
      return count < size + removedCount;
    }

    @Override
    public T next() {
      count++;
      removed = false;
      T val = next.val;
      next = next.next;
      return val;
    }

    public void remove() {
      if (! removed) {
        RingBuffer.this.remove(next.previous);
        removedCount++;
      }
    }
  }
}
