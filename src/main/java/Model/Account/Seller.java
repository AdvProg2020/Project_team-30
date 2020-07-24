package Model.Account;

import Model.Product.Product;
import Server.BankConnection;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Seller extends Account {
    private static ArrayList<Seller> allSellers = new ArrayList<>();
    private String nameOfCompany;
    private double balance;
    private ArrayList<SellLog> sellLogs;
    private ArrayList<Product> products;
    private ArrayList<Off> offs;
    private String phoneNumberOfCompany;
    private String CompanyAddress;
    private String CompanyOpenYear;
    private String bankAccountID;
    private String bankAccountToken;

    public Seller(String username, String password, String name, String lastName, String email, String phoneNumber, String nameOfCompany,
                  int balance) {
        super(username, password, name, lastName, email, phoneNumber);
        allSellers.add(this);
        this.nameOfCompany = nameOfCompany;
        this.balance = balance;
        sellLogs = new ArrayList<>();
        products = new ArrayList<>();
        offs = new ArrayList<>();
        createBankAccount();
    }

    public Seller() {
        allSellers.add(this);
    }

    private void createBankAccount() {
        bankAccountID = BankConnection.createAccount(name, lastName, username, password);
    }

    public String getBankAccountID() {
        return bankAccountID;
    }

    public static ArrayList<Seller> getAllSellers() {
        return allSellers;
    }

    public static void setAllSellers(ArrayList<Seller> allSellers) {
        Seller.allSellers = allSellers;
    }

    public String getNameOfCompany() {
        return nameOfCompany;
    }

    public ArrayList<SellLog> getSellLogs() {
        return sellLogs;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public ArrayList<Off> getOffs() {
        return offs;
    }

    public double getBalance() {
        return balance;
    }

    public void setNameOfCompany(String nameOfCompany) {
        this.nameOfCompany = nameOfCompany;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setSellLogs(ArrayList<SellLog> sellLogs) {
        this.sellLogs = sellLogs;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void setOffs(ArrayList<Off> offs) {
        this.offs = offs;
    }

    public String getPhoneNumberOfCompany() {
        return phoneNumberOfCompany;
    }

    public void setPhoneNumberOfCompany(String phoneNumberOfCompany) {
        this.phoneNumberOfCompany = phoneNumberOfCompany;
    }

    public String getCompanyAddress() {
        return CompanyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        CompanyAddress = companyAddress;
    }

    public String getCompanyOpenYear() {
        return CompanyOpenYear;
    }

    public void setCompanyOpenYear(String companyOpenYear) {
        CompanyOpenYear = companyOpenYear;
    }

    public static Seller getSellerByUserName(String username){
        for (Seller seller : allSellers) {
            if (seller.getUsername().equalsIgnoreCase(username)){
                return seller;
            }
        }
        return null;
    }

    @Override
    public void removeUser() {
        allSellers.remove(this);
        for (Off off : this.getOffs()) {
            Off.removeOff(off);
        }
        for (Product product : this.getProducts()) {
            Product.removeProduct(product);
        }
    }

    @Override
    public String toString() {
        return "Seller{" +
                "nameOfCompany='" + nameOfCompany + '\'' +
                ", balance=" + balance +
                ", sellLogs=" + sellLogs +
                ", products=" + products +
                ", offs=" + offs +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
