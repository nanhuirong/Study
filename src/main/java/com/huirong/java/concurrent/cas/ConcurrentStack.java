package com.huirong.java.concurrent.cas;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by huirong on 17-3-12.
 * 非阻塞式栈
 */
@ThreadSafe
public class ConcurrentStack<E> {
    AtomicReference<Node<E>> top = new AtomicReference<>();
    public void push(E item){
        Node<E> newTop = new Node<E>(item);
        Node<E> oldTop;
        do {
            oldTop = top.get();
            newTop.next = oldTop;
        }while (!top.compareAndSet(oldTop, newTop));
    }

    public E pop(){
        Node<E> oldTop;
        Node<E> newTop;
        do {
            oldTop = top.get();
            if (oldTop == null){
                return null;
            }
            newTop = oldTop.next;
        }while (!top.compareAndSet(oldTop, newTop));
        return oldTop.item;
    }

    private static class Node <E>{
        public final E item;
        public Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }
}
