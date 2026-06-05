package org.example.huffman;

public class MinHeap<T extends Comparable<T>> {

  private T[] heap;
  private int size;
  private static final int DEFAULT_CAPACITY = 10;

  public MinHeap() {

    this.heap = (T[]) new Comparable[DEFAULT_CAPACITY];
    this.size = 0;
  }

  public int size() {
    return size;
  }

  public void add(T element) {
    if (size == heap.length) {
      resize();
    }
    heap[size] = element;
    siftUp(size);
    size++;
  }

  public T poll() {
    if (size == 0)
      return null;

    T result = heap[0];

    T lastElement = heap[size - 1];
    heap[0] = lastElement;
    heap[size - 1] = null;
    size--;

    if (size > 0) {
      siftDown(0);
    }

    return result;
  }

  private void siftUp(int index) {
    int parentIndex = (index - 1) / 2;

    while (index > 0 && heap[index].compareTo(heap[parentIndex]) < 0) {
      swap(index, parentIndex);
      index = parentIndex;
      parentIndex = (index - 1) / 2;
    }
  }

  private void siftDown(int index) {
    int smallest = index;
    int leftChild = 2 * index + 1;
    int rightChild = 2 * index + 2;

    if (leftChild < size && heap[leftChild].compareTo(heap[smallest]) < 0) {
      smallest = leftChild;
    }

    if (rightChild < size && heap[rightChild].compareTo(heap[smallest]) < 0) {
      smallest = rightChild;
    }

    if (smallest != index) {
      swap(index, smallest);
      siftDown(smallest);
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
