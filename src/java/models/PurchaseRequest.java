package models;

import java.sql.Date;
import java.util.List;

public class PurchaseRequest {

    private int id;
    private Date requestDate;
    private String status;
    private int totalItems;
    private List<PurchaseItem> items; // внутренний класс
    // геттеры/сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<PurchaseItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseItem> items) {
        this.items = items;
    }

    public static class PurchaseItem {

        private String bookTitle;
        private String author;
        private String isbn;
        private int year;
        private String publisher;
        private int quantity;
        // геттеры/сеттеры
    }
}
