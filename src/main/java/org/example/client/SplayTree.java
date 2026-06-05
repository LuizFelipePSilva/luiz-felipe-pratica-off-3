package org.example.client;

public class SplayTree {

  private SplayNode root;

  private void rotateRight(SplayNode v) {
    SplayNode q = v.left;
    v.left = q.right;
    if (q.right != null)
      q.right.parent = v;
    q.parent = v.parent;
    if (v.parent == null)
      root = q;
    else if (v == v.parent.left)
      v.parent.left = q;
    else
      v.parent.right = q;
    q.right = v;
    v.parent = q;
  }

  private void rotateLeft(SplayNode v) {
    SplayNode q = v.right;
    v.right = q.left;
    if (q.left != null)
      q.left.parent = v;
    q.parent = v.parent;
    if (v.parent == null)
      root = q;
    else if (v == v.parent.left)
      v.parent.left = q;
    else
      v.parent.right = q;
    q.left = v;
    v.parent = q;
  }

  private void splay(SplayNode q) {
    while (q.parent != null) {
      SplayNode v = q.parent;
      SplayNode z = v.parent;

      if (z == null) {
        if (q == v.left)
          rotateRight(v);
        else
          rotateLeft(v);

      } else if (q == v.left && v == z.left) {
        rotateRight(z);
        rotateRight(v);

      } else if (q == v.right && v == z.right) {
        rotateLeft(z);
        rotateLeft(v);

      } else if (q == v.right && v == z.left) {
        rotateLeft(v);
        rotateRight(z);

      } else {
        rotateRight(v);
        rotateLeft(z);
      }
    }
  }

  public void insert(int key, String value) {
    SplayNode q = new SplayNode(key, value);
    SplayNode parent = null;
    SplayNode current = root;

    while (current != null) {
      parent = current;
      if (key < current.key)
        current = current.left;
      else if (key > current.key)
        current = current.right;
      else {
        current.value = value;
        splay(current);
        return;
      }
    }

    q.parent = parent;
    if (parent == null)
      root = q;
    else if (key < parent.key)
      parent.left = q;
    else
      parent.right = q;

    splay(q);
  }

  public String search(int key) {
    SplayNode q = root;
    SplayNode last = null;

    while (q != null) {
      last = q;
      if (key == q.key) {
        splay(q);
        return q.value;
      } else if (key < q.key)
        q = q.left;
      else
        q = q.right;
    }

    if (last != null)
      splay(last);
    return null;
  }

  public void remove(int key) {
    SplayNode q = root;
    while (q != null) {
      if (key == q.key)
        break;
      else if (key < q.key)
        q = q.left;
      else
        q = q.right;
    }
    if (q == null)
      return;

    SplayNode parentOfQ = q.parent;
    splay(q);

    if (q.left == null) {
      root = q.right;
      if (root != null)
        root.parent = null;
    } else if (q.right == null) {
      root = q.left;
      if (root != null)
        root.parent = null;
    } else {
      SplayNode maxLeft = q.left;
      while (maxLeft.right != null)
        maxLeft = maxLeft.right;

      q.left.parent = null;
      root = q.left;
      splay(maxLeft);

      maxLeft.right = q.right;
      q.right.parent = maxLeft;
    }

    if (parentOfQ != null && parentOfQ != q)
      splay(parentOfQ);
  }

  public SplayNode getRoot() {
    return root;
  }

  public SplayNode[] getTopN(int n) {
    SplayNode[] result = new SplayNode[n];
    int count = 0;
    if (root == null)
      return result;
    SplayNode[] queue = new SplayNode[1024];
    int head = 0, tail = 0;
    queue[tail++] = root;
    while (head != tail && count < n) {
      SplayNode node = queue[head++];
      result[count++] = node;
      if (node.left != null)
        queue[tail++] = node.left;
      if (node.right != null)
        queue[tail++] = node.right;
    }
    return result;
  }
}
