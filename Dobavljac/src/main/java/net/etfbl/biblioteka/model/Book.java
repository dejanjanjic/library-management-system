package net.etfbl.biblioteka.model;

import java.io.Serializable;
import java.util.Objects;

public class Book implements Serializable {
    private int id;
    private String title;
    private String author;
    private String year;
    private String language;
    private String picturePath;
    private String content;
    private static final long serialVersionUID = 2244766253053086191L;


    public Book() {
        super();
    }


    public Book(int id, String title, String author, String year, String language, String picturePath, String content) {
        super();
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.language = language;
        this.picturePath = picturePath;
        this.content = content;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getAuthor() {
        return author;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public String getYear() {
        return year;
    }


    public void setYear(String year) {
        this.year = year;
    }


    public String getLanguage() {
        return language;
    }


    public void setLanguage(String language) {
        this.language = language;
    }


    public String getPicturePath() {
        return this.picturePath;
    }


    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Book other = (Book) obj;
        return id == other.id;
    }


    @Override
    public String toString() {
        return "Book [id=" + id + ",\ntitle=" + title + ",\nauthor=" + author + ",\nyear=" + year + ",\nlanguage="
                + language + ",\ncontent=" + content + "]";
    }


}
