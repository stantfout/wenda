package com.usth.wenda;

public class Test {

    public static void main(String[] args) {
        Teacher op = new Teacher();
        new Thread(op, "张老师").start();
        new Thread(op, "刘老师").start();
        new Thread(op, "王老师").start();
    }
}

class Teacher implements Runnable {
    int count = 80;

     public void run() {
        while (true) {
            if (count <= 0) {
                break;
            } else {
                Thread th = Thread.currentThread();
                String th_name = th.getName();
                System.out.println(th_name + "正在发第" + count-- + "本作业");
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

