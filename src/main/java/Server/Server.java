package Server;

import Controller.ProductsManager;
import Model.Account.*;
import Model.Product.Category;
import Model.Product.Comment;
import Model.Product.Product;
import View.Main;
import com.google.gson.Gson;
import graphics.products.PurchaseMenuController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class Server extends Application {
    private static final int serverPort = 8080;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static ProductsManager productsManager = new ProductsManager();
    private static final Gson gson = new Gson();

    @Override
    public void start(Stage stage) {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        Button button = new Button("Shut down server");
        VBox vBox = new VBox(button);
        vBox.setSpacing(500);
        vBox.setPadding(new Insets(100, 100, 100, 100));
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Server");
        stage.show();
        stage.setOnCloseRequest(e -> {
            stage.close();
            Main.serializeXML();
            System.exit(0);
        });
        button.setOnAction(e -> {
            stage.close();
            Main.serializeXML();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        Main.deserializeXML();
        Thread thread = new Thread(Server::run);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        launch();
    }

    private static void run(){
        System.out.println("Server is running...\n");
        try {
            serverSocket = new ServerSocket(serverPort);
            while (true){
                clientSocket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));

                String message = dataInputStream.readUTF();
                System.out.println(message);
                System.out.println();
                String[] splitMessage = message.split("\\s+");
                if (message.startsWith("{\"token\":\"")) { //for requests with token
                    String token = getJasonObjectItem(message, "token");
                    Account account = Account.getAccountByToken(token);
                    String content = getJasonObjectItem(message, "content");
                    String[] splitContent = content.split("\\s+");

                    if (account != null){
                        if (content.startsWith("add to cart: ")){
                            Customer customer = (Customer) account;
                            Product product = Product.getProductByID(splitContent[3]);
                            customer.addToCart(product, Integer.parseInt(splitContent[4]));
                        }
                        else if (content.startsWith("rate product: ")){
                            Customer customer = (Customer) account;
                            Product product = Product.getProductByID(splitContent[2]);
                            int rate = Integer.parseInt(splitContent[3]);
                            product.addRate(customer, rate);
                        }
                        else if (content.startsWith("new comment: ")){
                            Comment comment = gson.fromJson(content.substring("new comment: ".length()), Comment.class);
                            Product.getProductByID(comment.getId()).addAComment(comment);
                        }
                        else if (content.startsWith("use discount: ")){
                            Discount discount = Discount.getDiscountByDiscountCode(content.substring("use discount: ".length()));
                            Customer customer = (Customer) account;
                            HashMap<Discount, Integer> usedDiscounts = customer.getUsedDiscounts();
                            usedDiscounts.put(discount, usedDiscounts.getOrDefault(discount, 0) + 1);
                        }
                        else if (content.startsWith("finish buying: ")){
                            Customer customer = (Customer) account;
                            double finalAmount = Double.parseDouble(splitContent[2]);
                            double totalAmount = Double.parseDouble(splitContent[3]);
                            PurchaseMenuController.finishBuying(finalAmount, customer.getCart(), customer, totalAmount);
                        }
                        else if (content.equals("logout")){
                            account.setToken(null);
                        }
                    }
                    if (content.equals("get logged in account")){
                        sendAccountInfo(dataOutputStream, account);
                    }
                }
                else if (message.equals("getProducts")){
                    dataOutputStream.writeUTF(gson.toJson(Product.getAllProducts()));
                    dataOutputStream.flush();
                }
                else if (message.equals("getCategories")){
                    dataOutputStream.writeUTF(gson.toJson(Category.getAllCategories()));
                    dataOutputStream.flush();
                }
                else if (message.startsWith("delete product: ")){
                    Product product = Product.getProductByID(message.substring("delete product: ".length()));
                    productsManager.deleteAProduct(product);
                }
                else if (message.startsWith("visit product: ")){
                    Product product = Product.getProductByID(message.substring("visit product: ".length()));
                    product.setVisitNumber(product.getVisitNumber() + 1);
                }
                else if (message.startsWith("get product: ")){
                    Product product = Product.getProductByID(splitMessage[2]);
                    dataOutputStream.writeUTF(gson.toJson(product));
                    dataOutputStream.flush();
                }
                else if (message.startsWith("get discount: ")){
                    Discount discount = Discount.getDiscountByDiscountCode(message.substring("get discount: ".length()));
                    dataOutputStream.writeUTF(gson.toJson(discount));
                    dataOutputStream.flush();
                }
                else if (message.startsWith("login: ")){
                    Account account = Account.getAccountByUsername(message.substring("login: ".length()));
                    String token = generateToken();
                    account.setToken(token);
                    dataOutputStream.writeUTF(token);
                    dataOutputStream.flush();
                }
                else if (message.startsWith("get account: ")){
                    Account account = Account.getAccountByUsername(message.substring("get account: ".length()));
                    sendAccountInfo(dataOutputStream, account);
                }

                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getJasonObjectItem(String jasonObject, String item){
        if (item.equals("token")){
            return jasonObject.substring(10, jasonObject.indexOf(',') - 1);
        }
        else {
            return jasonObject.substring(jasonObject.indexOf(',') + 12, jasonObject.length() - 2);
        }
    }

    private static String generateToken(){
        char[] code = new char[25];
        Random random = new Random();
        for (int i = 0; i < 25; ++i) {
            int a = random.nextInt(62);

            if (a < 10)
                code[i] = (char) (a + 48);

            else if (a < 36)
                code[i] = (char) (a + 55);
            else
                code[i] = (char) (a + 61);
        }
        return String.valueOf(code);
    }

    private static void sendAccountInfo(DataOutputStream dataOutputStream, Account account){
        try {
            if (account instanceof Admin) {
                dataOutputStream.writeUTF("Admin: " + gson.toJson((Admin) account));
            }
            else if (account instanceof Customer) {
                dataOutputStream.writeUTF("Customer: " + gson.toJson((Customer) account));
            }
            else {
                dataOutputStream.writeUTF("Seller: " + gson.toJson((Seller) account));
            }
        dataOutputStream.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}