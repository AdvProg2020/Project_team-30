package Model.Account;

import Model.Product.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class Customer extends Account {
    private static ArrayList<Customer> allCustomers = new ArrayList<>();
    private int balance;
    private HashMap<Product, Integer> cart;
    private ArrayList<BuyLog> buyLogs;
    private HashMap<Discount, Integer> usedDiscounts;

    public Customer(String username, String password, String name, String lastName, String email, String phoneNumber, int balance) {
        super(username, password, name, lastName, email, phoneNumber);
        this.balance = balance;
        allCustomers.add(this);
        cart = new HashMap<>();
        buyLogs = new ArrayList<>();
        usedDiscounts = new HashMap<>();
    }

    public Customer(){
        this("", "", "", "", "", "", 0);
    }

    public static ArrayList<Customer> getAllCustomers() {
        return allCustomers;
    }

    public static void setAllCustomers(ArrayList<Customer> allCustomers) {
        Customer.allCustomers = allCustomers;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int newBalance) {
        this.balance = newBalance;
    }

    public void addToCart(Product product, int number) {
        if (cart.containsKey(product)) {
            int newNumber = cart.get(product) + number;
            if (number <= 0) {
                cart.remove(product);
            }
            else {
                cart.put(product, newNumber);
            }
        }
        else if (number > 0) {
            cart.put(product, number);
        }
    }

    public double getTotalPrice(){
        double price = 0;
        for (Product product : cart.keySet()) {
            price += product.getPrice() * cart.get(product);
        }
        return price;
    }

    public void setUsedDiscounts(HashMap<Discount, Integer> usedDiscounts) {
        this.usedDiscounts = usedDiscounts;
    }

    public HashMap<Product, Integer> getCart() {
        return cart;
    }

    public void setCart(HashMap<Product, Integer> cart) {
        this.cart = cart;
    }

    public ArrayList<BuyLog> getBuyLogs() {
        return buyLogs;
    }

    public HashMap<Discount, Integer> getUsedDiscounts() {
        return usedDiscounts;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "balance=" + balance +
                ", cart=" + cart +
                ", buyLogs=" + buyLogs +
                ", usedDiscounts=" + usedDiscounts +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
