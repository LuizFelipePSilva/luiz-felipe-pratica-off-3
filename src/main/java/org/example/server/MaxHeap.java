package org.example.server;

public class MaxHeap<T extends Comparable<T>> {
  private T[] heap;
  private int size;
  private static final int DEFAULT_CAPACITY = 10;

  public MaxHeap() {
    this.heap = (T[]) new Comparable[DEFAULT_CAPACITY];
    this.size = 0;
  }

  public int size() {
    return size;
  }

  public void add(T element) {
    if (size == heap.length)
      resize();
    heap[size] = element;
    siftUp(size);
    size++;
  }

  public T poll() {
    if (size == 0)
      return null;
    T result = heap[0];
    heap[0] = heap[size - 1];
    heap[size - 1] = null;
    size--;
    if (size > 0)
      siftDown(0);
    return result;
  }

  private void siftUp(int index) {
    int parent = (index - 1) / 2;
    while (index > 0 && heap[index].compareTo(heap[parent]) > 0) { // > 0 aqui
      swap(index, parent);
      index = parent;
      parent = (index - 1) / 2;
    }
  }

  private void siftDown(int index) {
    int largest = index;
    int left = 2 * index + 1;
    int right = 2 * index + 2;
    if (left < size && heap[left].compareTo(heap[largest]) > 0)
      largest = left; // > 0 aqui
    if (right < size && heap[right].compareTo(heap[largest]) > 0)
      largest = right; // > 0 aqui
    if (largest != index) {
      swap(index, largest);
      siftDown(largest);
    }
  }

  private void swap(int i, int j) {
    T temp = heap[i];
    heap[i] = heap[j];
    heap[j] = temp;
  }

  private void resize() {
    T[] newHeap = (T[]) new Comparable[heap.length * 2];
    for (int i = 0; i < heap.length; i++) {
      newHeap[i] = heap[i];
    }
    heap = newHeap;
  }
}
