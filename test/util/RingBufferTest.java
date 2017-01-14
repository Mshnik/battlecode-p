package util;


import static org.junit.Assert.*;

import java.util.Iterator;
import org.junit.Test;

/**
 *
 */
public final class RingBufferTest {

  @Test
  public void testConstruction() {
    RingBuffer<Integer> r = new RingBuffer<>(10);
    assertEquals(10, r.getCapacity());
    assertEquals(0, r.size());
    assertEquals("[]", r.toString());
    assertEquals(null, r.get());
    assertEquals(null, r.getAndRotate());

    assertTrue(r.isEmpty());
    assertFalse(r.isFull());
  }

  @Test
  public void testAdd() {
    RingBuffer<Integer> r = new RingBuffer<>(3);
    assertEquals(3, r.getCapacity());
    assertEquals(0, r.size());
    assertEquals("[]", r.toString());

    r.add(0);
    assertEquals(3, r.getCapacity());
    assertEquals(1, r.size());
    assertEquals("[0]", r.toString());
    assertFalse(r.isEmpty());
    assertFalse(r.isFull());

    r.add(1);
    assertEquals(3, r.getCapacity());
    assertEquals(2, r.size());
    assertEquals("[0, 1]", r.toString());
    assertFalse(r.isEmpty());
    assertFalse(r.isFull());

    r.add(2);
    assertEquals(3, r.getCapacity());
    assertEquals(3, r.size());
    assertEquals("[0, 1, 2]", r.toString());
    assertFalse(r.isEmpty());
    assertTrue(r.isFull());

    try {
      r.add(3);
      fail("Added to a full RingBuffer");
    } catch (RuntimeException e) {}
  }

  @Test
  public void testGetAndRotate() {
    RingBuffer<Integer> r = new RingBuffer<>(4);
    r.add(0);
    r.add(1);
    r.add(2);

    assertEquals(Integer.valueOf(0), r.get());
    assertEquals(Integer.valueOf(0), r.get());

    assertEquals(Integer.valueOf(0), r.getAndRotate());
    assertEquals(Integer.valueOf(1), r.getAndRotate());
    assertEquals(Integer.valueOf(2), r.getAndRotate());
    assertEquals(Integer.valueOf(0), r.get());

    r.add(3);
    assertEquals(Integer.valueOf(0), r.getAndRotate());
    assertEquals(Integer.valueOf(1), r.getAndRotate());
    assertEquals(Integer.valueOf(2), r.getAndRotate());
    assertEquals(Integer.valueOf(3), r.getAndRotate());
  }

  @Test
  public void testRemove() {
    RingBuffer<Integer> r = new RingBuffer<>(1,2,3,4);
    int size = 4;
    int val = 1;
    Iterator<Integer> iter = r.iterator();
    while(iter.hasNext()) {
      assertEquals(Integer.valueOf(val), iter.next());
      val++;

      iter.remove();
      size--;
      assertEquals(size, r.size());
    }

    assertEquals(0, r.size());
  }

}
