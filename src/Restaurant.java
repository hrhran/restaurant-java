import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Restaurant {
    int customers;
    public static void main(String[] args){
        BlockingQueue<String> q = new LinkedBlockingQueue<>();
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter the number of Customer:");
        int customerCount = sc.nextInt();
        System.out.print("Enter the number of Waiters:");
        int waiterCount = sc.nextInt();
        System.out.println("Restaurant is open!");
        List<Thread> threads = new ArrayList<>();
        for(int i=1;i<=customerCount;i++) {
            threads.add(new Customer(q));
        }
        for(int i=1;i<=waiterCount;i++) {
            threads.add(new Waiter(q));
        }
        for(Thread thread : threads){
            thread.start();
        }
    }
}



class Waiter extends Thread {
    private String waiterName;
    private final BlockingQueue<String> q;

    public Waiter(BlockingQueue<String> q) {
        this.q = q;
        Random rand = new Random();
        int ran = rand.nextInt(200);
        waiterName=this.getClass().getSimpleName()+ran;
        this.setName(this.waiterName);
    }

    public void takeOrder(String name) throws InterruptedException{
        System.out.println(name+"'s order has been taken by "+waiterName);
        sleep(5000);
    }

    @Override
    public void run() {
        System.out.println(waiterName+" is available to take orders");
        while(true)
            try {
                String name = q.take();
                takeOrder(name);
                sleep(3000);
                System.out.println(name+" was served by "+waiterName);
            } catch (Exception err) {
                err.printStackTrace();
            }
    }
}



class Customer extends Thread{
    private String customerName;
    private final BlockingQueue<String> q;
    public Customer (BlockingQueue<String> q) {
        this.q = q;
        Random rand = new Random();
        int ran = rand.nextInt(200);
        customerName=this.getClass().getSimpleName()+ran;
        this.setName(this.customerName);
    }

    @Override
    public void run() {
        System.out.println(customerName+" has entered the restaurant");
            try {
                q.put(this.customerName);
                sleep(1500);
            }catch(Exception e) {
                e.printStackTrace();
            }
    }
}