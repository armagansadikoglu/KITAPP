package com.armagansadikoglu.kitapp;

public class Notice {
    private String BookName;
    private String Seller;
    private Long Price;
    private String UserID;
    private String BookDetails;
    private String NoticeID;
    private String Genre;

    public Notice(String bookName, Long price, String seller, String userID, String bookDetails, String noticeID,String genre) {
        BookName = bookName;
        Seller = seller;
        Price = price;
        UserID = userID;
        BookDetails = bookDetails;
        NoticeID = noticeID;
        Genre = genre;
    }


    public Notice() {
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getSeller() {
        return Seller;
    }

    public void setSeller(String seller) {
        Seller = seller;
    }

    public Long getPrice() {
        return Price;
    }

    public void setPrice(Long price) {
        Price = price;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }


    public String getBookDetails() {
        return BookDetails;
    }

    public void setBookDetails(String bookDetails) {
        BookDetails = bookDetails;
    }

    public String getNoticeID() {
        return NoticeID;
    }

    public void setNoticeID(String noticeID) {
        NoticeID = noticeID;
    }
}
