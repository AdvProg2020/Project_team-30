package Controller;

import Model.Product.Category;
import Model.Product.Product;
import Model.Product.ProductComparatorForVisitNumber;
import View.FilteringType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductsManager {
    private ArrayList<Product> allProducts = Product.getAllProducts();

    private Comparator<Product> currentSortMode = new ProductComparatorForVisitNumber();
    private ArrayList<FilteringType> currentFilters = new ArrayList<>();
    private String nameFilter;
    private Category categoryFilter;
    private boolean existenceFilter;

    public void showAllCategories () {
        Category.showAllCategories();
    }

    public ArrayList<Product> showProducts () {
        ArrayList<Product> sortedFilteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (nameFilter != null) {
                if (!getMatcher(product.getProductName(), "(?i)" + nameFilter).find()){
                    continue;
                }
            }
            if (categoryFilter != null){
                if (!product.getProductCategory().equals(categoryFilter)){
                    continue;
                }
            }
            if (existenceFilter){
                if (!product.getExistenceStatus()){
                    continue;
                }
            }
            sortedFilteredProducts.add(product);
        }
        sortedFilteredProducts.sort(currentSortMode);
        return sortedFilteredProducts;
    }

    public void addCategoryFilter(String name) throws IllegalArgumentException{
        Category category = Category.getCategoryByName(name);
        if (categoryFilter == null){
            throw new IllegalArgumentException();
        }
        categoryFilter = category;
        if (!currentFilters.contains(FilteringType.CATEGORY_FILTER)){
            currentFilters.add(FilteringType.CATEGORY_FILTER);
        }
    }

    public void disableCategoryFilter(){
        categoryFilter = null;
        currentFilters.remove(FilteringType.CATEGORY_FILTER);
    }

    public void addNameFiltering(String name){
        nameFilter = name;
        if (!currentFilters.contains(FilteringType.NAME_FILTER)){
            currentFilters.add(FilteringType.NAME_FILTER);
        }
    }

    public void disableNameFiltering(){
        nameFilter = null;
        currentFilters.remove(FilteringType.NAME_FILTER);
    }

    public void addExistenceFilter(boolean existenceFilter){
        this.existenceFilter = existenceFilter;
        if (!currentFilters.contains(FilteringType.EXISTENCE_FILTER) && existenceFilter){
            currentFilters.add(FilteringType.EXISTENCE_FILTER);
        }
        if (!existenceFilter){
            currentFilters.remove(FilteringType.EXISTENCE_FILTER);
        }
    }

    public void printCurrentFilters(){
        System.out.println("Active Filters:");
        for (FilteringType filter : currentFilters) {
            System.out.println("\t" + filter.getFilterType());
        }
        System.out.println();
    }

    private static Matcher getMatcher(String input, String regex){
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }
}