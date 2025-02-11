package net.etfbl.dobavljac.model;

import net.etfbl.biblioteka.model.Book;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    List<String> books;
    Date proccessingDate;
    double price;
    public Bill(ArrayList<Book> books){
        this.books = books.stream().map(book -> book.getTitle()).toList();
        Random random = new Random();
        price = 0.0;
        for (Book b :
                books) {
            price += random.nextDouble(50) + 10;
        }
        proccessingDate = new Date();
    }

    public List<String> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<String> books) {
        this.books = books;
    }

    public Date getProccessingDate() {
        return proccessingDate;
    }

    public void setProccessingDate(Date proccessingDate) {
        this.proccessingDate = proccessingDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
