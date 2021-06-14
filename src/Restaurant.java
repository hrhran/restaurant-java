import java.util.*;
import java.util.concurrent.*;

public class Restaurant {
    private static int customerCount;
    private static int waiterCount;
    private static int chefCount;

    static void getInputCount(){
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter the number of Customer:");
        customerCount = sc.nextInt();
        System.out.print("Enter the number of Waiters:");
        waiterCount = sc.nextInt();
        System.out.print("Enter the number of Chefs:");
        chefCount = sc.nextInt();
    }
    public static void main(String[] args){
        Object serveLock=new Object();
        Object prepLock=new Object();
        BlockingQueue<String> q = new LinkedBlockingQueue<>();
        BlockingQueue<String> food = new LinkedBlockingQueue<>();
        List<String> readyFood = new ArrayList<>();
        getInputCount();
        System.out.println("------Restaurant is open!------");
        List<Thread> threads = new ArrayList<>();
        for(int i=1;i<=customerCount;i++) {
            threads.add(new Customer(serveLock,q));
        }
        for(int i=1;i<=waiterCount;i++) {
            threads.add(new Waiter(serveLock,prepLock,q,readyFood,food));
        }
        for(int i=1;i<=chefCount;i++) {
            Thread chef=new Chef(prepLock,readyFood,food);
            chef.setDaemon(true);
            threads.add(chef);
        }
        for(Thread thread : threads){
            thread.start();
        }
        //System.out.println("END OF"+"MAIN");
    }
}



class Waiter extends Thread {
    final Object serveLock;
    final Object prepLock;
    private final String waiterName;
    private final BlockingQueue<String> q;
    private final BlockingQueue<String> food;
    private List<String> readyFood;
    String currentOrder;
    String currentCustomer;
    public Waiter(Object serveLock,Object prepLock,BlockingQueue<String> q,List<String> readyFood,BlockingQueue<String> food) {
        this.serveLock=serveLock;
        this.prepLock=prepLock;
        this.q = q;
        this.readyFood=readyFood;
        this.food=food;
        Random rand = new Random();
        int ran = rand.nextInt(200);
        waiterName=this.getClass().getSimpleName()+ran;
        this.setName(this.waiterName);
    }


    public void takeOrder(String name,String order) throws InterruptedException{
        sleep(1000);
        System.out.println(name+" has ordered "+order+" to "+waiterName);
        sleep(5000);
    }

    public void pickFood() throws InterruptedException{
        food.put(currentOrder);
        System.out.println(waiterName+" is waiting for "+currentOrder+" to be prepared");
        sleep(2500);
        synchronized (prepLock){
            while(!readyFood.contains(currentOrder))
                prepLock.wait();
        }
        System.out.println("Food Ready to Serve: "+readyFood);
        sleep(2500);
        readyFood.remove(currentOrder);
        synchronized (serveLock) {
            System.out.println(currentCustomer + " was served "+currentOrder+ " by " + waiterName);
            serveLock.notify();
        }
        //System.out.println("END OF PICK FOOD");

    }

    @Override
    public void run() {
        System.out.println(waiterName + " is available to take orders");
        try{
            sleep(1000);
        }catch (InterruptedException e){e.printStackTrace();}
        while (!q.isEmpty()){
            try {
                String customerNameAndOrder = q.take();
                String[] nameAndOrder = customerNameAndOrder.split("\\+");
                currentCustomer = nameAndOrder[0];
                currentOrder = nameAndOrder[1];
                takeOrder(currentCustomer,currentOrder);
                pickFood();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("END OF"+currentThread().getName());
    }
}



class Customer extends Thread{
    final Object serveLock;
    private final int foodOrder;
    private final String customerName;
    String foodChoice;
    private final BlockingQueue<String> q;
    public Customer (Object serveLock,BlockingQueue<String> q) {
        this.serveLock=serveLock;
        this.q = q;
        Random rand = new Random();
        foodOrder = rand.nextInt(4);
        int ran = rand.nextInt(200);
        customerName=this.getClass().getSimpleName()+ran;
        this.setName(this.customerName);
    }

    public void orderFood(int order) {
        switch (order) {
            case 0 -> foodChoice = "Biryani";
            case 1 -> foodChoice = "Tandoori";
            case 2 -> foodChoice = "Fried Rice";
            case 3 -> foodChoice = "Noodles";
        }
    }

    public void eating(){
        try{
            sleep(16000);
            System.out.println(customerName+" has completed eating his "+foodChoice);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println(customerName + " has entered the restaurant");
        try {
            orderFood(foodOrder);
            String customerNameAndOrder=customerName+"+"+foodChoice;
            q.put(customerNameAndOrder);
            synchronized (serveLock){
                serveLock.wait();
            }
            eating();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("END OF"+currentThread().getName());
    }
}


class Chef extends Thread{
    final Object prepLock;
    private final String chefName;
    private final BlockingQueue<String> food;
    private List<String> readyFood;
    String currentFood;
    public Chef (Object prepLock,List<String> readyFood,BlockingQueue<String> food) {
        this.prepLock=prepLock;
        this.readyFood=readyFood;
        this.food = food;
        Random rand = new Random();
        int ran = rand.nextInt(20);
        chefName=this.getClass().getSimpleName()+ran;
        this.setName(this.chefName);
    }
    void cookFood() throws InterruptedException{
        currentFood=food.take();
        switch (currentFood) {
            case "Biryani" -> {
                sleep(12000);
                readyFood.add("Biryani");
            }
            case "Tandoori" -> {
                sleep(9000);
                readyFood.add("Tandoori");
            }
            case "Fried Rice" -> {
                sleep(5000);
                readyFood.add("Fried Rice");
            }
            case "Noodles" -> {
                sleep(7000);
                readyFood.add("Noodles");
            }
        }
        System.out.println(chefName+" has prepared "+currentFood);
        synchronized (prepLock){
            prepLock.notify();
        }
    }
    @Override
    public void run() {
        System.out.println(chefName+" is available for cooking");
        try {
            while(true) cookFood();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("END OF"+currentThread().getName());
    }

}

