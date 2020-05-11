package com.armagansadikoglu.kitapp;

public class Notice {
    private String BookName;
    private String Seller;
    private Long Price;
    private String UserID;
    private String BookDetails;
    private String NoticeID;
    private String Genre;
    private String City;
    private String Country;
    private String NoticeDate;

    public Notice(String bookName, Long price, String seller, String userID, String bookDetails, String noticeID,String genre,String city,String country, String date) {
        BookName = bookName;
        Seller = seller;
        Price = price;
        UserID = userID;
        BookDetails = bookDetails;
        NoticeID = noticeID;
        Genre = genre;
        City = city;
        Country = country;
        NoticeDate = date;
    }


    public Notice() {
    }

    public String getNoticeDate() {
        return NoticeDate;
    }

    public void setNoticeDate(String noticeDate) {
        NoticeDate = noticeDate;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
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
