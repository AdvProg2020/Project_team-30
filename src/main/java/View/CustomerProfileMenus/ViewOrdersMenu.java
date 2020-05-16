package View.CustomerProfileMenus;

import Controller.CustomerProfileManager;
import Controller.ProductsManager;
import Model.Account.Customer;
import Model.Product.Product;
import View.Menu;

import java.util.ArrayList;

public class ViewOrdersMenu extends Menu {
    private CustomerProfileManager customerProfileManager;
    public ViewOrdersMenu(Customer customer, Menu parentMenu) {
        super("View Orders Menu", parentMenu);
        ArrayList<Menu> subMenus = new ArrayList<>();
        submenus.add(getShowOrderMenu());
        submenus.add(getRateMenu());
        this.setSubMenus(subMenus);
    }

    @Override
    public void show() {
        System.out.println(customerProfileManager.showOrdersSellerNameAndDate());
        super.show();
    }

    private Menu getShowOrderMenu() {
        return new Menu("Show Order Menu", this) {
            @Override
            public void show() {
                System.out.println(this.getName() + ":");
                System.out.println("Enter orderID to view order or (back) to return or (Logout) to leave your account:");
            }

            @Override
            public void execute() {
                show();
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("Back")) {
                        this.parentMenu.execute();
                    } else if (input.equals("Logout")) {
                        loginAndRegisterManager.logoutUser();
                    } else if (input.matches("\\d+")) {
                        if (customerProfileManager.isInputValidForBuyLogID(input)) {
                            System.out.println(customerProfileManager.showOrder(input));
                            parentMenu.execute();
                        } else {
                            System.out.println("There is no order with this ID");
                        }
                    } else {
                        System.out.println("Please enter valid number:");
                    }
                }
            }
        };
    }

    private Menu getRateMenu() {
        return new Menu("Get Rate Menu", this) {
            @Override
            public void show() {
                System.out.println(this.getName() + ":");
                System.out.println("Enter productID or (back) to return or (Logout) to leave your account:");
            }

            @Override
            public void execute() {
                show();
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("Back")) {
                        this.parentMenu.execute();
                    } else if (input.equals("Logout")) {
                        loginAndRegisterManager.logoutUser();
                    } else if (input.matches("\\d+^$")) { //todo:
                        if (ProductsManager.isValidNumberForProductID(input)) {
                            System.out.println("Enter rate(1-5) or (back) to return or (Logout) to leave your account:");
                            String productID = input;
                            while(true) {
                                input = scanner.nextLine();
                                if (input.equalsIgnoreCase("Back")) {
                                    this.show();
                                    break;
                                } else if (input.equals("Logout")) {
                                    loginAndRegisterManager.logoutUser();
                                } else if (input.matches("\\d^$")) { //todo:
                                    if (Integer.parseInt(input) <= 5 && Integer.parseInt(input) >= 1) {
                                        customerProfileManage//todo:add rate ali! please complete
                                        System.out.println("Your rate submitted");
                                        parentMenu.execute();
                                    } else {
                                        System.out.println("please enter a number in range(1-5):");
                                    }
                                } else {
                                    System.out.println("please enter valid number:");
                                }
                            }
                        } else {
                            System.out.println("There is no product with this ID");
                        }
                    } else {
                        System.out.println("Please enter valid number:");
                    }
                }
            }
        };
    }

    //todo: w8 for answer in group for adding override for execute to give inputs for rate
}
