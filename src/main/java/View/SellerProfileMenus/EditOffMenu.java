package View.SellerProfileMenus;

import Controller.SellerProfileManager;
import Model.Request.EditOffRequest;
import View.Menu;

import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;

import static View.SellerProfileMenus.AddOffMenu.getDate;

public class EditOffMenu extends Menu {
    private SellerProfileManager sellerProfileManager;
    private EditOffRequest editOffRequest;

    public EditOffMenu(Menu parentMenu, SellerProfileManager sellerProfileManager) {
        super("Edit Off Menu", parentMenu);
        this.sellerProfileManager = sellerProfileManager;
        ArrayList<Menu> submenus = new ArrayList<>();
        submenus.add(getEditOffID());
        submenus.add(getEditOffStartTime());
        submenus.add(getEditOffEndTime());
        submenus.add(getEditOffAmount());
        this.setSubMenus(submenus);
    }

    @Override
    public void show() {
        System.out.println("Enter ID of the Off you want to edit:");
        String offID = scanner.nextLine();
        if (offID.equalsIgnoreCase("back")) {
            parentMenu.execute();
        } else if (offID.equalsIgnoreCase("logout")) {
            loginAndRegisterManager.logoutUser();
        }
        try {
            this.editOffRequest = sellerProfileManager.makeNewEditOffRequest(offID);
        }
        catch (NullPointerException e) {
            System.out.println("There is no Off with this ID.");
            this.show();
        }
        super.show();
    }

    private Menu getEditOffID() {
        return new Menu("Edit Off ID", this) {
            @Override
            public void execute() {
                System.out.println("Enter Off ID:");
                String offID = scanner.nextLine();
                if (offID.equalsIgnoreCase("back")) {
                    parentMenu.execute();
                } else if (offID.equalsIgnoreCase("logout")) {
                    loginAndRegisterManager.logoutUser();
                }
                try {
                    sellerProfileManager.editOffId(editOffRequest, offID);
                    System.out.println("Off ID " + offID + " successfully added to your request.");
                }
                catch (IllegalArgumentException e) {
                    System.out.println("There is already an Off with this ID.");
                }
                this.parentMenu.execute();
            }
        };
    }

    private Menu getEditOffStartTime() {
        return new Menu("Edit Off Start Time", this) {
            @Override
            public void execute() {
                System.out.println("Enter Off Start Time:");
                Date offStartTime = getDate(parentMenu);
                if (offStartTime == null){
                    execute();
                }
                sellerProfileManager.editOffStartTime(editOffRequest, offStartTime);
                System.out.println("Off Start Time " + offStartTime + " successfully added to your request.");
                this.parentMenu.execute();
            }
        };
    }

    private Menu getEditOffEndTime() {
        return new Menu("Edit Off End Time", this) {
            @Override
            public void execute() {
                System.out.println("Enter Off End Time:");
                Date offEndTime = getDate(parentMenu);
                if (offEndTime == null){
                    execute();
                }
                sellerProfileManager.editOffEndTime(editOffRequest, offEndTime);
                System.out.println("Off End Time " + offEndTime + " successfully added to your request.");
                this.parentMenu.execute();
            }
        };
    }

    private Menu getEditOffAmount() {
        return new Menu("Add Off Amount", this) {
            @Override
            public void execute() {
                System.out.println("Enter Off Amount:");
                String offAmount = scanner.nextLine();
                if (offAmount.equalsIgnoreCase("back")) {
                    parentMenu.execute();
                } else if (offAmount.equalsIgnoreCase("logout")) {
                    loginAndRegisterManager.logoutUser();
                }
                try {
                    sellerProfileManager.editOffAmount(editOffRequest, offAmount);
                    System.out.println("Off Amount " + offAmount + " successfully added to your request.");
                }
                catch (InputMismatchException e) {
                    System.out.println("You should enter an integer number.");
                }
                this.parentMenu.execute();
            }
        };
    }
}
