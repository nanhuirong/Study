package com.huirong.java.reflect;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by huirong on 17-3-5.
 */
public class MyReflect implements Serializable{
    private static final long serialVersionUID = -6197947287295938593L;
    public static void main(String[] args) {
        MyReflect reflect = new MyReflect();
        System.out.println(reflect.getClass().getName());
        MyReflect.function();
        MyReflect.function1();
        MyReflect.function2();
        MyReflect.function3();
        MyReflect.function4();
        MyReflect.function5();
        MyReflect.function6();
        MyReflect.function7();
        MyReflect.function8();
    }

    //实例化Class对象
    public static void function(){
        System.out.println("--------------------------------\n实例化class对象");
        Class<?> class1 = null;
        Class<?> class2 = null;
        Class<?> class3 = null;
        try {
            class1 = Class.forName("com.huirong.java.reflect.MyReflect");
            class2 = new MyReflect().getClass();
            class3 = MyReflect.class;
            System.out.println(class1.getName());
            System.out.println(class2.getName());
            System.out.println(class3.getName());
        }catch (ClassNotFoundException e){

        }

    }

    //获取一个对象的父类和实现的接口
    public static void function1(){
        System.out.println("--------------------------------\n获取一个对象的父类和实现的接口");
        try {
            Class<?> clazz = Class.forName("com.huirong.java.reflect.MyReflect");
            Class<?> parent = clazz.getSuperclass();
            System.out.println("super class :" + parent.getName());
            Class<?> intes[] = clazz.getInterfaces();
            for (Class<?> entry : intes){
                System.out.println(entry.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //获取全部的构造方法
    public static void function2(){
        System.out.println("--------------------------------\n获取全部的构造方法");
        Class<?> class1 = null;
        try {
            class1 = Class.forName("com.huirong.java.reflect.User");
            //调用默认构造方法
            User user = (User) class1.newInstance();
            user.setAge(10);
            user.setName("nanhuirong");
            System.out.println(user);
            //取得全部的构造方法
            Constructor<?> constructors[] = class1.getConstructors();
            for (Constructor<?> constructor : constructors){
                Class<?> clazzs[] = constructor.getParameterTypes();
                for (Class<?> clazz : clazzs){
                    System.out.print(clazz.getName());
                }
                System.out.println();

            }
            user = (User) constructors[1].newInstance(10, "wangjiangjuan");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取一个类的全部属性
    public static void function3(){
        System.out.println("--------------------------------\n获取一个类的全部属性");
        try {
            Class<?> clazz = Class.forName("com.huirong.java.reflect.User");
            Field[] fields = clazz.getDeclaredFields();
            //getFields() 获得实现的接口或父类的属性
            for (Field field : fields){
                //字段类型
                Class<?> type = field.getType();
                System.out.println(type.getName() + "\t" + field.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取一个类的全部方法
    public static void function4(){
        System.out.println("--------------------------------\n获取一个类的全部方法");
        try {
            Class<?> clazz = Class.forName("com.huirong.java.reflect.User");
            Method[] methods = clazz.getMethods();
            for (Method method : methods){
                System.out.print(method.getName() + "\t");
                Class<?> returnType = method.getReturnType();
                Class<?> paras[] = method.getParameterTypes();
                System.out.print(returnType.getName() + "\t");
                for (Class<?> para : paras){
                    System.out.print(para.getName() + "\t");
                }
                Class<?>[] execs = method.getExceptionTypes();
                for (Class<?> exec : execs){
                    System.out.print(exec.getName());
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //利用反射机制调用某个类的方法
    public static void function5(){
        System.out.println("--------------------------------\n利用反射机制调用某个类的方法");
        try {
            Class<?> clazz = Class.forName("com.huirong.java.reflect.User");
            Method method = clazz.getMethod("getName");
            method.invoke(clazz.newInstance());
            method = clazz.getMethod("setName", String.class);
            method.invoke(clazz.newInstance(), "huirong");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //通过过反射机制调用某个类的属性
    public static void function6(){
        System.out.println("--------------------------------\n利用反射机制调用某个类的属性");
        try {
            Class<?> clazz = Class.forName("com.huirong.java.reflect.User");
            Object obj = clazz.newInstance();
            Field field = clazz.getDeclaredField("name");
            System.out.println(field.getName());
            field.setAccessible(true);
            field.set(obj, "java");
            System.out.println(field.get(obj));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //反射机制的动态代理
    public static void function7(){
        System.out.println("--------------------------------\n反射机制的动态代理");
        User user = new User();
        System.out.println("类加载器" + user.getClass().getClassLoader().getClass().getName());
        MyInvocationHandler demo = new MyInvocationHandler();
        Subject sub = (Subject) demo.bind(new RealSubject());
        String info = sub.say("nan", 20);
        System.out.println(info);
    }

    //反射机制的应用
    public static void function8(){
        System.out.println("--------------------------------\n" +
                "在泛型为Integer的ArrayList中存放一个String类型的对象");
        ArrayList<Integer> list = new ArrayList<>();
        try {
            Method method = list.getClass().getMethod("add", Object.class);
            method.invoke(list, "java");
            System.out.println(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //通过反射取得并修改数组的信息
        System.out.println("通过反射取得并修改数组的信息");
        int[] temp = {1, 2, 3, 4, 6};
        Class<?> demo = temp.getClass().getComponentType();
        System.out.println("数组类型" + demo.getName());
    }
}
