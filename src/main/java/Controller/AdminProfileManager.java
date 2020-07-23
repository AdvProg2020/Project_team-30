package Controller;

import Client.Connection;
import Model.Account.*;
import Model.Request.*;
import Model.Product.Category;
import Model.Product.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.*;

public class AdminProfileManager extends ProfileManager {

    public AdminProfileManager(Admin admin) {
        super(admin);
    }

    public static boolean isThereAdmin() {
        Connection.sendToServer("isThereAdmin");
        String response = Connection.receiveFromServer();
        if (response.equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Account> getAllUsers() {
        return Account.getAllAccounts();
    }

    public TableView getAllUsersTable() {
        TableView allUsers = new TableView();

        TableColumn<String, Account> column1 = new TableColumn<>("Username");
        column1.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<String, Account> column2 = new TableColumn<>("First Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<String, Account> column3 = new TableColumn<>("Last Name");
        column3.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<String, Account> column4 = new TableColumn<>("Email");
        column4.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<String, Account> column5 = new TableColumn<>("Phone Number");
        column5.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        allUsers.getColumns().addAll(column1, column2, column3, column4, column5);

        Connection.sendToServer("getCustomers");
        ArrayList<Customer> allCustomers = new Gson().fromJson(Connection.receiveFromServer(), new TypeToken<ArrayList<Customer>>(){}.getType());
        Connection.sendToServer("getSellers");
        ArrayList<Seller> allSellers = new Gson().fromJson(Connection.receiveFromServer(), new TypeToken<ArrayList<Seller>>(){}.getType());
        Connection.sendToServer("getAdmins");
        ArrayList<Admin> allAdmins = new Gson().fromJson(Connection.receiveFromServer(), new TypeToken<ArrayList<Admin>>(){}.getType());

        ArrayList<Account> allAccounts = new ArrayList<>();
        for (Admin admin : allAdmins) {
            allAccounts.add(admin);
        }
        for (Seller seller : allSellers) {
            allAccounts.add(seller);
        }
        for (Customer customer : allCustomers) {
            allAccounts.add(customer);
        }
        for (Account account : allAccounts) {
            if (account.equals(Account.getLoggedInAccount())) {
                continue;
            }
            allUsers.getItems().add(account);
        }
        allUsers.setPlaceholder(new Label("No Data to display"));
        return allUsers;
    }

    public TableView getAllCustomersTable(TableView allCustomersTable) {
        TableColumn<String, Customer> column1 = new TableColumn<>("Username");
        column1.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<String, Customer> column2 = new TableColumn<>("First Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<String, Customer> column3 = new TableColumn<>("Last Name");
        column3.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<String, Customer> column4 = new TableColumn<>("Email");
        column4.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<String, Customer> column5 = new TableColumn<>("Phone Number");
        column5.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        allCustomersTable.getColumns().addAll(column1, column2, column3, column4, column5);

        Connection.sendToServer("getCustomers");
        ArrayList<Customer> allCustomers = new Gson().fromJson(Connection.receiveFromServer(), new TypeToken<ArrayList<Customer>>(){}.getType());
        for (Customer customer : allCustomers) {
            allCustomersTable.getItems().add(customer);
        }
        allCustomersTable.setPlaceholder(new Label("No Data to display"));
        return allCustomersTable;
    }

    public void deleteUser(String username) throws NullPointerException, IllegalArgumentException {
        if (username.equals("")) {
            throw new IllegalArgumentException("You must enter username.");
        } else {
            Account account = Account.getAccountByUsername(username);
            if (account == null) {
                throw new NullPointerException("There is no account with this username.");
            }
            Connection.sendToServer("delete user: " + username);
        }
    }

    public static String generateRandomDiscountCode() {
        char[] code = new char[8];
        Random random = new Random();
        for (int i = 0; i < 8; ++i) {
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

    public void registerAdmin(String username, String password, String name, String lastName, String email, String phoneNumber) {
        String message = "register admin: ," + username + "," + password + "," + name + "," + lastName + "," + email + "," + phoneNumber + ", ";
        Connection.sendToServer(message);
    }

    //todo: checking this
    public void removeProduct(String productId) throws NullPointerException {
        Product product = Product.getProductByID(productId);
        if (product == null) {
            throw new NullPointerException();
        } else {
            Connection.sendToServer("remove product: " + productId);
        }
    }

    public void createDiscountCode(String discountCode, Date startTime, Date endTime, String discountPercent,
                                   String maxPossibleDiscount, String discountPerCustomer,
                                   ArrayList<String> includingCustomers) throws Exception {

        if (checkDiscountCodeValidity(discountCode) && checkDiscountPercentValidity(discountPercent) &&
                checkMaxPossibleDiscountValidity(maxPossibleDiscount) &&
                checkDiscountPerCustomerValidity(discountPerCustomer) && checkCustomersValidity(includingCustomers)) {

            Discount discount = new Discount(discountCode, startTime, endTime, Double.parseDouble(discountPercent),
                    Double.parseDouble(maxPossibleDiscount), Integer.parseInt(discountPerCustomer), includingCustomers);
            Connection.sendToServer("Create discountCode: " + new Gson().toJson(discount));
            Discount.getAllDiscounts().remove(discount);
        }
    }

    public TableView getAllDiscountsTable(TableView allDiscountsTable) {
        TableColumn<String, Discount> column = new TableColumn<>("Discount Code");
        column.setCellValueFactory(new PropertyValueFactory<>("discountCode"));

        allDiscountsTable.getColumns().add(column);

        Connection.sendToServer("getDiscounts");
        ArrayList<Discount> allDiscounts = new Gson().fromJson(Connection.receiveFromServer(), new TypeToken<ArrayList<Discount>>(){}.getType());
        for (Discount discount : allDiscounts) {
            allDiscountsTable.getItems().add(discount);
        }

        allDiscountsTable.setPlaceholder(new Label("No Data To Display"));
        return allDiscountsTable;
    }

    public ArrayList<Discount> getAllDiscountCodes() {
        return Discount.getAllDiscounts();
    }

    public void editDiscountStartTime(String discountCode, Date startTime) throws NullPointerException {
        Connection.sendToServer("get Discount: " + discountCode);
        Discount discount = new Gson().fromJson(Connection.receiveFromServer(), Discount.class);
        if (discount == null) {
            throw new NullPointerException();
        }
        //discount.setStartTime(startTime);
        Connection.sendToServer("edit discount startTime: ," + discountCode + "," + new Gson().toJson(startTime));
    }

    public void editDiscountEndTime(String discountCode, Date endTime) throws NullPointerException {
        Connection.sendToServer("get Discount: " + discountCode);
        Discount discount = new Gson().fromJson(Connection.receiveFromServer(), Discount.class);
        if (discount == null) {
            throw new NullPointerException();
        }
        //discount.setEndTime(endTime);
        Connection.sendToServer("edit discount endTime: ," + discountCode + "," + new Gson().toJson(endTime));
    }

    public void editDiscountPercent(String discountCode, String discountPercent) throws NullPointerException, Exception {
        Connection.sendToServer("get Discount: " + discountCode);
        Discount discount = new Gson().fromJson(Connection.receiveFromServer(), Discount.class);
        if (discount == null) {
            throw new NullPointerException();
        } else if (checkDiscountPercentValidity(discountPercent)) {
            Connection.sendToServer("edit discount percent: ," + discountCode + "," + discountPercent);
        }
    }

    public void editDiscountMaxPossibleDiscount(String discountCode, String maxPossibleDiscount) throws NullPointerException, Exception {
        Connection.sendToServer("get Discount: " + discountCode);
        Discount discount = new Gson().fromJson(Connection.receiveFromServer(), Discount.class);
        if (discount == null) {
            throw new NullPointerException();
        } else if (checkMaxPossibleDiscountValidity(maxPossibleDiscount)) {
            //discount.setMaxPossibleDiscount(Double.parseDouble(maxPossibleDiscount));
            Connection.sendToServer("edit discount maxPossibleDiscount: ," + discountCode + "," + maxPossibleDiscount);
        }
    }

    public void editDiscountPerCustomer(String discountCode, String discountPerCustomer) throws NullPointerException, Exception {
        Connection.sendToServer("get Discount: " + discountCode);
        Discount discount = new Gson().fromJson(Connection.receiveFromServer(), Discount.class);
        if (discount == null) {
            throw new NullPointerException();
        } else if (checkDiscountPercentValidity(discountPerCustomer)) {
            //discount.setDiscountPerCustomer(Integer.parseInt(discountPerCustomer));
            Connection.sendToServer("edit discount discountPerCustomer: ," + discountCode + "," + discountPerCustomer);
        }
    }

    public void editDiscountIncludingCustomers(String discountCode, ArrayList<String> customersUsername) throws Exception {
        if (checkCustomersValidity(customersUsername) && checkDiscountCodeValidity(discountCode)) {
            Connection.sendToServer("get Discount: " + discountCode);
            Discount discount = new Gson().fromJson(Connection.receiveFromServer(), Discount.class);
            //discount.setIncludingCustomers(customersUsername);
            String message = "edit discount includingCustomers: ," + discountCode + ",";
            for (String customer : customersUsername) {
                message = message + customer + ",";
            }
            Connection.sendToServer(message + " ");
        }
    }

    private boolean checkDiscountCodeValidity(String discountCode) throws IllegalArgumentException {
        if (discountCode.trim().equals("")) {
            throw new IllegalArgumentException("Invalid Discount Code");
        } else {
            return true;
        }
    }

    private boolean checkDiscountPercentValidity(String discountPercent) throws Exception {
        try {
            double discountPercentValue = Double.parseDouble(discountPercent);
            if (discountPercentValue < 100 && discountPercentValue > 0) {
                return true;
            }
            else {
                throw new Exception("Discount Percent should be between 0 and 100");
            }
        } catch (Exception e) {
            throw new Exception("Invalid Discount Percent");
        }
    }

    private boolean checkMaxPossibleDiscountValidity(String maxPossibleDiscount) throws Exception {
        try {
            Double.parseDouble(maxPossibleDiscount);
            return true;
        } catch (Exception e) {
            throw new Exception("Invalid Maximum Possible Discount");
        }
    }

    private boolean checkDiscountPerCustomerValidity(String discountPerCustomer) throws IllegalArgumentException {
        if (discountPerCustomer.trim().equals("") || !(discountPerCustomer.matches("\\d+"))) {
            throw new IllegalArgumentException("Invalid number of discount user per customer.");
        } else {
            return true;
        }
    }

    private boolean checkCustomersValidity(ArrayList<String> customersUsername) throws IllegalArgumentException {
        for (String s : customersUsername) {
            Connection.sendToServer("get account :" + s);
            Account account = Connection.getAccountFromServer();
            if (account == null || !(account instanceof Customer)) {
                throw new IllegalArgumentException("Invalid Customer Username.");
            }
        }
        return true;
    }

    public void removeDiscount(String discountCode) throws NullPointerException {
        Connection.sendToServer("get Discount: " + discountCode);
        Discount discount = new Gson().fromJson(Connection.receiveFromServer(), Discount.class);
        if (discount == null) {
            throw new NullPointerException();
        }
        Connection.sendToServer("remove discount: " + discountCode);
    }

    public TableView getAllRequestsTable(TableView allRequestsTable) {
        TableColumn<String, Request> column1 = new TableColumn<>("Request ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("requestId"));

        allRequestsTable.getColumns().add(column1);

        ArrayList<Request> allRequests = new ArrayList<>();

        for (Request request : Request.getAllRequests()) {
            allRequestsTable.getItems().add(request);
        }
        allRequestsTable.setPlaceholder(new Label("No Data to display"));
        return allRequestsTable;
    }

    public ArrayList<String> getAllRequests() {
        ArrayList<Request> allRequests = Request.getAllRequests();
        ArrayList<String> allRequestIds = new ArrayList<>();
        for (Request request : allRequests) {
            allRequestIds.add(request.getRequestId());
        }
        return allRequestIds;
    }

    public String getDetailsOfRequest(String requestId) throws NullPointerException {
        Request request = Request.getRequestById(requestId);
        if (request == null) {
            throw new NullPointerException();
        }
        return request.toString();
    }

    public void acceptRequest(String requestId) throws NullPointerException, IllegalArgumentException {
        Request request = Request.getRequestById(requestId);
        if (request == null) {
            throw new NullPointerException();
        } else {
            request.acceptRequest();
            Request.removeRequest(request);
        }
    }

    public void declineRequest(String requestId) throws NullPointerException {
        Request request = Request.getRequestById(requestId);
        if (request == null) {
            throw new NullPointerException();
        }
        Request.removeRequest(request);
    }

    public ArrayList<Category> getAllCategories() {
        return Category.getAllCategories();
    }

    public Category addAndGetCategory(String categoryName, ArrayList<String> specialFeatures) throws Exception {
        if (Category.getCategoryByName(categoryName) == null) {
            if (specialFeatures.isEmpty()) {
                throw new Exception("Special Features Field is Empty");
            } else {
                Category category = new Category(categoryName);
                category.setSpecialFeatures(specialFeatures);
                return category;
            }
        } else {
            throw new Exception("There is another Category with this name");
        }
    }

    public Category addAndGetSubCategory(String subCategoryName, Category parentCategory, ArrayList<String> specialFeatures) throws Exception {
        if (parentCategory == null) {
            throw new Exception("You haven't add category yet");
        } else {
            if (subCategoryName.isEmpty() || subCategoryName.trim().isEmpty()) {
                throw new Exception("You haven't entered subCategory name");
            } else if (specialFeatures.isEmpty()) {
                throw new Exception("Special Features Field is Empty");
            }
        }
        Category subCategory = parentCategory.addSubCategoryWithName(subCategoryName);
        subCategory.getSpecialFeatures().addAll(specialFeatures);
        return subCategory;
    }

    public void editCategoryName(Category category, String newCategoryName) throws IllegalArgumentException {
        if (Category.getCategoryByName(newCategoryName) != null) {
            throw new IllegalArgumentException();
        }
        category.setName(newCategoryName);
    }

    //todo: checking this
    public void removeCategorySpecialFeature(Category category, String specialFeature) throws NullPointerException {
        if (category.getSpecialFeatures().contains(specialFeature)) {
            category.removeSpecialFeature(specialFeature);
        } else {
            throw new NullPointerException();
        }
    }

    //todo: checking this
    public void addSpecialFeature(Category category, String specialFeature) throws IllegalArgumentException {
        if (category.getSpecialFeatures().contains(specialFeature)) {
            throw new IllegalArgumentException();
        } else {
            category.addSpecialFeature(specialFeature);
        }
    }

    //todo: checking this
    public void removeCategory(String categoryName) throws NullPointerException {
        Category category = Category.getCategoryByName(categoryName);
        if (category == null) {
            throw new NullPointerException();
        }
        Category.removeCategory(category);
    }

    public TableView getAllCategoriesTable(TableView allCategoriesTable) {
        TableColumn<String, Category> column = new TableColumn<>("Category Name");
        column.setCellValueFactory(new PropertyValueFactory<>("name"));

        allCategoriesTable.getColumns().add(column);
        for (Category category : Category.getAllCategories()) {
            allCategoriesTable.getItems().add(category);
        }
        allCategoriesTable.setPlaceholder(new Label("No Data To Display"));
        return allCategoriesTable;
    }

    //todo: checking this
    public void removeSubCategory(Category parentCategory, String subCategory) {
        parentCategory.removeSubCategory(Category.getCategoryByName(subCategory));
    }

    //phase1
    public String viewUser(String username) throws NullPointerException {
        Account account = Account.getAccountByUsername(username);
        if (account == null) {
            throw new NullPointerException();
        }
        return account.toString();
    }

    public static boolean isProductWithThisID(String productID) {
        for (Product product : Product.getAllProducts()) {
            if (product.getProductId().equals(productID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCategoryWithThisName(String categoryName) {
        for (Category category : Category.getAllCategories()) {
            if (category.getName().equals(categoryName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDiscountWithThisID(String ID) {
        for (Discount discount : Discount.getAllDiscounts()) {
            if (discount.getDiscountCode().equals(ID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInputValidForDiscountCode(String input) {
        if (!input.trim().matches("\\s")) {
            return true;
        }
        return false;
    }

    public static boolean isInputValidForDiscountPercent(String input) {
        if (input.matches("\\A\\d\\d\\z")) {
            return true;
        }
        return false;
    }

    public String viewDiscount(String discountCode) throws NullPointerException {
        Discount discount = Discount.getDiscountByDiscountCode(discountCode);
        if (discount == null) {
            throw new NullPointerException();
        }
        return discount.toString();
    }

    public void editDiscountCode(String discountCodeBefore, String discountCodeAfter) throws NullPointerException, IllegalArgumentException {
        Discount discount = Discount.getDiscountByDiscountCode(discountCodeBefore);
        if (discount == null) {
            throw new NullPointerException();
        } else if (Discount.getDiscountByDiscountCode(discountCodeAfter) != null) {
            throw new IllegalArgumentException();
        } else {
            discount.setDiscountCode(discountCodeAfter);
        }
    }
}
