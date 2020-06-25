package graphics.AdminProfile.ManageDiscounts;

import Controller.AdminProfileManager;
import Model.Account.Account;
import Model.Account.Admin;
import Model.Account.Customer;
import graphics.AlertBox;
import graphics.App;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class CreateDiscountCode {
    private static String parentMenu = "AdminProfileMenu";
    private AdminProfileManager adminProfileManager;

    public TableView customerTable;

    public TextField discountCodeField;
    public TextField discountPercentField;
    public TextField maxPossibleDiscountField;
    public TextField discountPerCustomerField;
    public DatePicker startTimeDate;
    public DatePicker endTimeDate;
    public TextField includingCustomersField;

    private ArrayList<String> includingCustomers;

    public void initialize() {
        this.adminProfileManager = new AdminProfileManager((Admin) Account.getLoggedInAccount());
        customerTable = adminProfileManager.getAllCustomersTable(customerTable);
        includingCustomers = new ArrayList<>();
    }

    public void confirm() {
        String discountCode = discountCodeField.getText();
        //todo: checking this
        Date startTime = new Date(startTimeDate.getValue().toEpochDay());
        Date endTime = new Date(endTimeDate.getValue().toEpochDay());
        String discountPercent = discountPercentField.getText();
        String maxPossibleDiscount = maxPossibleDiscountField.getText();
        String discountPerCustomer = discountPerCustomerField.getText();
        String includingCustomersFieldText = includingCustomersField.getText();
        boolean checkConfirmButtonInability;
        checkConfirmButtonInability = discountCode.isEmpty() || startTimeDate.getValue() == null || endTimeDate.getValue() == null || discountPercent.isEmpty() || maxPossibleDiscount.isEmpty() || discountPerCustomer.isEmpty() || includingCustomersFieldText.isEmpty();
        if (!(checkConfirmButtonInability)) {
            try {
                adminProfileManager.createDiscountCode(discountCode, startTime, endTime, discountPercent, maxPossibleDiscount, discountPerCustomer, includingCustomers);
                AlertBox.showMessage("Create Discount Code", "Discount Code : " + discountCode + " successfully created.");
                try {
                    App.setRoot(parentMenu);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                AlertBox.showMessage("Failed to Create", e.getMessage());
            }
        }
    }

    public void addCustomerToDiscount(MouseEvent mouseEvent) {
        Object selectedUser = customerTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            return;
        }
        String customerUsername = ((Customer) selectedUser).getUsername();
        includingCustomers.add(customerUsername);

        includingCustomersField.setText(includingCustomersField.getText() + (customerUsername + ", "));
        customerTable.getItems().remove(selectedUser);
    }

    public void clearIncludingCustomers(MouseEvent mouseEvent) {
        customerTable.getItems().addAll(includingCustomers);
        includingCustomers.clear();
        includingCustomersField.setText("");
    }
}