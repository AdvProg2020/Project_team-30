package graphics.SellerProfile;

import Controller.SellerProfileManager;
import Model.Account.Account;
import Model.Account.Seller;
import Model.Product.Product;
import graphics.AlertBox;
import graphics.App;
import graphics.products.ProductPageController;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ManageProducts {
    public TextField productIDField;
    public TextField productNameField;
    public TextField productCompanyNameField;
    public TextField productPriceField;
    public TextField productExistingNumberField;
    public TextField productCategoryField;
    public TextField productSpecialFeaturesField;

    public TableView productsTable;
    public ImageView backImage;
    public ImageView mainMenuImage;

    private SellerProfileManager sellerProfileManager;

    public void initialize() {
        this.sellerProfileManager = new SellerProfileManager((Seller) Account.getLoggedInAccount());
        productsTable = sellerProfileManager.getSellerProductsTable(productsTable);

        ProductPageController.setMainMenuButton(mainMenuImage);
        App.setBackButton(backImage, "SellerProfileMenu");
    }

    public void confirm(MouseEvent mouseEvent) {

    }

    public void showProductBuyers(MouseEvent mouseEvent) {
        Object selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            return;
        }
        Product product = (Product) selectedProduct;
        TableView productBuyersTable = sellerProfileManager.getProductBuyersTable(product.getProductId());

        Stage window = new Stage();
        window.setTitle("Product Buyers");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);

        Button OKButton = new Button("OK");
        OKButton.setFont(Font.font("Times New Roman", 16));
        OKButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                window.close();
            }
        });

        vBox.getChildren().addAll(productBuyersTable, OKButton);
        Scene scene = new Scene(vBox);
        window.setScene(scene);
        window.show();
    }

    public void removeProduct(MouseEvent mouseEvent) {
        Object selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            return;
        }
        Product product = (Product) selectedProduct;
        sellerProfileManager.removeProduct(product.getProductId());
        AlertBox.showMessage("Remove Product", "Product With ID <" + product.getProductId() + "> Removed Successfully");
        productsTable.getItems().remove(product);
    }

    public void showProductDetails(MouseEvent mouseEvent) {

    }
}
