package com.huirong.until;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * Created by huirong on 17-3-24.
 */
public class Stack {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    private void ensureCapacity(){
        if (elements.length == size){
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }

    public void push(Object obj){
        ensureCapacity();
        elements[size++] = obj;
    }

    public Object pop(){
        if (size == 0){
            throw new EmptyStackException();
        }
//        return elements[--size];会引起内存泄漏
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }

    public static void main(String[] args)throws Exception {
        Stack stack = new Stack();
        for (int i = 0; i < 50000000; i++){
            stack.push(i);
            if (i % 1000 == 0){
                stack.pop();
                Thread.sleep(3);
            }
        }
    }
}
