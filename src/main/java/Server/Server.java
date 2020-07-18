package Server;

import Controller.ProductsManager;
import Model.Account.Account;
import Model.Account.Customer;
import Model.Product.Category;
import Model.Product.Comment;
import Model.Product.Product;
import View.Main;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
                if (message.startsWith("{\"token\":\"")) { //for requests with token
                    String token = getJasonObjectItem(message, "token");
                    Account account = Account.getAccountByToken(token);
                    if (account != null){
                        String content = getJasonObjectItem(message, "content");

                        if (content.startsWith("add to cart: ")){
                            Customer customer = (Customer) account;
                            Product product = Product.getProductByID(content.substring("add to cart: ".length()));
                            customer.addToCart(product, 1);
                        }
                        else if (content.startsWith("rate product: ")){
                            Customer customer = (Customer) account;
                            String[] split = content.split("\\s+");
                            Product product = Product.getProductByID(split[2]);
                            int rate = Integer.parseInt(split[3]);
                            product.addRate(customer, rate);
                        }
                        else if (content.startsWith("new comment: ")){
                            Comment comment = gson.fromJson(content.substring("new comment: ".length()), Comment.class);
                            Product.getProductByID(comment.getId()).addAComment(comment);
                        }
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

                }

                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getJasonObjectItem(String jasonObject, String item){
        int index1 = jasonObject.indexOf(item) + item.length() + 3;
        int index2 = jasonObject.indexOf("\"", index1);
        return jasonObject.substring(index1, index2);
    }
}
