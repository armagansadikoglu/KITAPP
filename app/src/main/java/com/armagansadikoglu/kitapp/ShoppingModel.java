package com.armagansadikoglu.kitapp;

public class ShoppingModel {
    private String BookName;
    private String ShoppingID;
    private String SellerName;
    private String SellerID;
    private String BuyerName;
    private String BuyerID;
    private Long Price;

    public ShoppingModel(String bookName, String shoppingID, String sellerName, String sellerID, String buyerName, String buyerID, Long price) {
        BookName = bookName;
        ShoppingID = shoppingID;
        SellerName = sellerName;
        SellerID = sellerID;
        BuyerName = buyerName;
        BuyerID = buyerID;
        Price = price;
    }

    public ShoppingModel() { // no-argmunet constructor için
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getShoppingID() {
        return ShoppingID;
    }

    public void setShoppingID(String shoppingID) {
        ShoppingID = shoppingID;
    }

    public String getSellerName() {
        return SellerName;
    }

    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }

    public String getSellerID() {
        return SellerID;
    }

    public void setSellerID(String sellerID) {
        SellerID = sellerID;
    }

    public String getBuyerName() {
        return BuyerName;
    }

    public void setBuyerName(String buyerName) {
        BuyerName = buyerName;
    }

    public String getBuyerID() {
        return BuyerID;
    }

    public void setBuyerID(String buyerID) {
        BuyerID = buyerID;
    }

    public Long getPrice() {
        return Price;
    }

    public void setPrice(Long price) {
        Price = price;
    }
}
