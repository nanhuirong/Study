package com.huirong.java.concurrent.cas;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicReference;
import static com.huirong.java.concurrent.cas.ConcurrentLinkedQueue.*;

/**
 * Created by huirong on 17-3-12.
 * 非阻塞链表的插入算法
 */
@ThreadSafe
public class ConcurrentLinkedQueue <E>{
    private static class Node<E>{
        final E item;
        final AtomicReference<Node<E>> next;

        public Node(E item, Node next) {
            this.item = item;
            this.next = new AtomicReference<Node<E>>(next);
        }
    }

    //哑元
    private final Node dummy = new Node<E>(null, null);

    private final AtomicReference<Node<E>> head = new AtomicReference<>(dummy);
    private final AtomicReference<Node<E>> tail = new AtomicReference<>(dummy);

    public boolean put(E item){
        Node<E> newNode = new Node<E>(item, null);
        while (true){
            Node<E> curTail = tail.get();
            Node<E> tailNext = curTail.next.get();
            if (curTail == tail.get()){
                if (tailNext != null){
                    //队列处于中间状态, 推进尾节点
                    tail.compareAndSet(curTail, tailNext);
                }else {
                    //处于稳定状态, 尝试插入新节点
                    if (curTail.next.compareAndSet(null, newNode));
                        //插入成功, 尝试推进尾节点
                        tail.compareAndSet(curTail, newNode);
                        return true;
                }
            }
        }
    }
}
