package implementations;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import utilities.BSTreeADT;
import utilities.Iterator;

public class BSTree<E extends Comparable<? super E>> implements BSTreeADT<E> {
    private BSTreeNode<E> root;
    private int size;

    public BSTree() {
        root = null;
        size = 0;
    }

    public BSTree(E element) {
        if (element == null) throw new NullPointerException();
        root = new BSTreeNode<>(element);
        size = 1;
    }

    @Override
    public boolean add(E element) {
        if (element == null) throw new NullPointerException();
        if (root == null) {
            root = new BSTreeNode<>(element);
            size++;
            return true;
        }
        return addRecursive(root, element);
    }

    private boolean addRecursive(BSTreeNode<E> node, E element) {
        int cmp = element.compareTo(node.getElement());
        if (cmp < 0) {
            if (node.getLeft() == null) {
                node.setLeft(new BSTreeNode<>(element));
                size++;
                return true;
            }
            return addRecursive(node.getLeft(), element);
        } else if (cmp > 0) {
            if (node.getRight() == null) {
                node.setRight(new BSTreeNode<>(element));
                size++;
                return true;
            }
            return addRecursive(node.getRight(), element);
        }
        return false;
    }

    @Override
    public BSTreeNode<E> search(E element) {
        if (element == null) throw new NullPointerException();
        return searchRecursive(root, element);
    }

    private BSTreeNode<E> searchRecursive(BSTreeNode<E> node, E element) {
        if (node == null) return null;
        int cmp = element.compareTo(node.getElement());
        if (cmp == 0) return node;
        else if (cmp < 0) return searchRecursive(node.getLeft(), element);
        else return searchRecursive(node.getRight(), element);
    }

    @Override
    public boolean contains(E element) {
        if (element == null) throw new NullPointerException();
        return search(element) != null;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int getHeight() {
        return heightRecursive(root);
    }

    private int heightRecursive(BSTreeNode<E> node) {
        if (node == null) return 0;
        return 1 + Math.max(heightRecursive(node.getLeft()), heightRecursive(node.getRight()));
    }

    @Override
    public BSTreeNode<E> getRoot() {
        if (root == null) throw new NullPointerException();
        return root;
    }

    @Override
    public BSTreeNode<E> removeMin() {
        if (root == null) return null;
        BSTreeNode<E> minNode = getMin(root);
        root = removeMinRecursive(root);
        size--;
        return minNode;
    }

    private BSTreeNode<E> getMin(BSTreeNode<E> node) {
        while (node.getLeft() != null) node = node.getLeft();
        return node;
    }

    private BSTreeNode<E> removeMinRecursive(BSTreeNode<E> node) {
        if (node.getLeft() == null) return node.getRight();
        node.setLeft(removeMinRecursive(node.getLeft()));
        return node;
    }

    @Override
    public BSTreeNode<E> removeMax() {
        if (root == null) return null;
        BSTreeNode<E> maxNode = getMax(root);
        root = removeMaxRecursive(root);
        size--;
        return maxNode;
    }

    private BSTreeNode<E> getMax(BSTreeNode<E> node) {
        while (node.getRight() != null) node = node.getRight();
        return node;
    }

    private BSTreeNode<E> removeMaxRecursive(BSTreeNode<E> node) {
        if (node.getRight() == null) return node.getLeft();
        node.setRight(removeMaxRecursive(node.getRight()));
        return node;
    }

    @Override
    public Iterator<E> inorderIterator() {
        return new TreeIterator<>(root, "inorder");
    }

    @Override
    public Iterator<E> preorderIterator() {
        return new TreeIterator<>(root, "preorder");
    }

    @Override
    public Iterator<E> postorderIterator() {
        return new TreeIterator<>(root, "postorder");
    }

    private static class TreeIterator<E extends Comparable<? super E>> implements Iterator<E> {
        private ArrayList<E> elements;
        private int index;

        public TreeIterator(BSTreeNode<E> root, String order) {
            elements = new ArrayList<>();
            if (order.equals("inorder")) inorder(root);
            else if (order.equals("preorder")) preorder(root);
            else if (order.equals("postorder")) postorder(root);
            index = 0;
        }

        private void inorder(BSTreeNode<E> node) {
            if (node != null) {
                inorder(node.getLeft());
                elements.add(node.getElement());
                inorder(node.getRight());
            }
        }

        private void preorder(BSTreeNode<E> node) {
            if (node != null) {
                elements.add(node.getElement());
                preorder(node.getLeft());
                preorder(node.getRight());
            }
        }

        private void postorder(BSTreeNode<E> node) {
            if (node != null) {
                postorder(node.getLeft());
                postorder(node.getRight());
                elements.add(node.getElement());
            }
        }

        @Override
        public boolean hasNext() {
            return index < elements.size();
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            return elements.get(index++);
        }
    }
}
