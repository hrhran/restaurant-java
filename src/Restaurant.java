import java.util.*;
import java.util.concurrent.*;

public class Restaurant {
    public static void main(String[] args){
        BlockingQueue<String> q = new LinkedBlockingQueue<>();
        BlockingQueue<Integer> food = new LinkedBlockingQueue<>(3);
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter the number of Customer:");
        int customerCount = sc.nextInt();
        System.out.print("Enter the number of Waiters:");
        int waiterCount = sc.nextInt();
        System.out.println("------Restaurant is open!------");
        List<Thread> threads = new ArrayList<>();
        for(int i=1;i<=customerCount;i++) {
            threads.add(new Customer(q));
        }
        for(int i=1;i<=waiterCount;i++) {
            threads.add(new Waiter(q,food));
        }
        for(Thread thread : threads){
            thread.start();
        }
        Thread chef = new Chef(food);
        chef.setDaemon(true);
        chef.start();
    }
}



class Waiter extends Thread {
    private final String waiterName;
    private final BlockingQueue<String> q;
    private final BlockingQueue<Integer> food;
    public Waiter(BlockingQueue<String> q,BlockingQueue<Integer> food) {
        this.q = q;
        this.food=food;
        Random rand = new Random();
        int ran = rand.nextInt(200);
        waiterName=this.getClass().getSimpleName()+ran;
        this.setName(this.waiterName);
    }

    public void takeOrder(String name) throws InterruptedException{
        System.out.println(name+"'s order has been taken by "+waiterName);
        sleep(7000);
    }

    public void pickFood() throws InterruptedException{
        if(food.isEmpty())
            System.out.println(waiterName+" is waiting for food to be prepared");
        food.take();
        sleep(3000);
    }

    @Override
    public void run() {
        System.out.println(waiterName+" is available to take orders");
        while (!q.isEmpty())
            try {
                String name = q.take();
                takeOrder(name);
                pickFood();
                System.out.println(name + " was served by " + waiterName);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}



class Customer extends Thread{
    private final String customerName;
    private final BlockingQueue<String> q;
    public Customer (BlockingQueue<String> q) {
        this.q = q;
        Random rand = new Random();
        int ran = rand.nextInt(200);
        customerName=this.getClass().getSimpleName()+ran;
        this.setName(this.customerName);
    }

    public void eating(){
        try{
            sleep(3000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println(customerName + " has entered the restaurant");
        try {
            q.put(this.customerName);
            eating();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


class Chef extends Thread{
    private final String chefName;
    private final BlockingQueue<Integer> food;
    public Chef (BlockingQueue<Integer> food) {
        this.food = food;
        Random rand = new Random();
        int ran = rand.nextInt(10);
        chefName=this.getClass().getSimpleName()+ran;
        this.setName(this.chefName);
    }
    void cookBatch() throws InterruptedException{
        sleep(6000);
        food.put(1);
        System.out.println(chefName+" has prepared food to serve");
    }
    @Override
    public void run() {
        System.out.println(chefName+" is available for cooking");
        try {
            while(food.size()<=3)
                cookBatch();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

