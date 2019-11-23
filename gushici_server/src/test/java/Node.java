import com.alibaba.fastjson.JSONObject;
import com.gushici.controller.user.UserController;
import org.apache.ibatis.annotations.Param;
import sun.awt.geom.AreaOp;

import javax.sound.midi.Soundbank;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntUnaryOperator;

public class Node {

    private final int value;

    private Node node;

    public Node(int value) {
        this.value = value;
        this.node = null;
    }

    public int getValue() {
        return value;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }


}

class reverNode{


    public Node reverserLinkedList(Node node){
        if (node.getNode() == null || node == null){
            return node;
        }
        Node newdata = reverserLinkedList(node.getNode());
        node.getNode().setNode(node);
        node.setNode(null);
        return newdata;
    }
}

class newNode{



    public Node createLinkedList(List<Integer> list){
        if (list.isEmpty()){
            return null;
        }
        Node headNode = new Node(list.get(0));
        Node tempNode = createLinkedList(list.subList(1, list.size()));
        headNode.setNode(tempNode);
        return headNode;
    }

    //测试方便的打印函数
    public void printList(Node node){
        while (node != null){
            System.out.print(node.getValue());
            System.out.print(" ");
            node = node.getNode();
        }
        System.out.println();
    }


    public static void main(String[] args) {
       /* newNode newNode = new newNode();
        Node node = newNode.createLinkedList(Arrays.asList(1, 2, 3, 4, 5));

        reverNode reverNode = new reverNode();
        Node res = reverNode.reverserLinkedList(node);
        newNode.printList(res);*/


        AtomicInteger atomicInteger = new AtomicInteger(3);
        System.out.println(atomicInteger.getAndSet(5));
        //atomicInteger.getAndIncrement();
        //atomicInteger.getAndSet(1);
        //System.out.println(atomicInteger.getAndIncrement() + "     " + atomicInteger.get());
        //System.out.println(atomicInteger.incrementAndGet() + "     " + atomicInteger.get());
        //System.out.println(atomicInteger.addAndGet(4) + "     " + atomicInteger.get());
        /*System.out.println(atomicInteger.intValue());

        AtomicReference<Object> objectAtomicReference = new AtomicReference<>();
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
//        System.out.println(atomicBoolean.get());
//        atomicBoolean.set(true);
        System.out.println(atomicBoolean.getAndSet(false));

        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
        new AtomicLong();

        new ReentrantLock();*/

    }

}


class SpinLock {
    private AtomicReference<Thread> cas = new AtomicReference<>();
    public void lock() {
        Thread current = Thread.currentThread();
        // 利用CAS
        while (!cas.compareAndSet(null, current)) {
            // DO nothing
        }
    }
    public void unlock() {
        Thread current = Thread.currentThread();
        cas.compareAndSet(current, null);
    }
}

