package net.etfbl.service;

import java.util.ArrayList;

import com.google.gson.Gson;

import net.etfbl.config.ConfigLoader;
import net.etfbl.logger.ServerLogger;
import net.etfbl.model.Book;
import net.etfbl.model.User;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
public class BookService {

	private JedisPool jedisPool = new JedisPool(ConfigLoader.getInstance().getProperty("jedis.address"), Integer.parseInt(ConfigLoader.getInstance().getProperty("jedis.port")));
	private ArrayList<Book> books = new ArrayList<Book>();
	
	
	
	public void loadBooksFromRedis() {
        try (Jedis jedis = jedisPool.getResource()) {
            Gson gson = new Gson();
            books.clear();
            for (String bookJson : jedis.lrange("books", 0, -1)) {
                Book book = gson.fromJson(bookJson, Book.class);
                books.add(book);
            }
        } catch (Exception e) {
        	ServerLogger.logger.severe("Error: " + e);
        }
    }

    public void saveBooksToRedis() {
        try (Jedis jedis = jedisPool.getResource()) {
            Gson gson = new Gson();
            jedis.del("books");  
            for (Book book : books) {
                String bookJson = gson.toJson(book);
                jedis.rpush("books", bookJson);
            }
        } catch (Exception e) {
        	ServerLogger.logger.severe("Error: " + e);
        }
    }
	public ArrayList<Book> getBooks() {
		loadBooksFromRedis();
		return books;
	}

	public Book getBookByTitle(String title) {
		loadBooksFromRedis();
		Book book = null;
		for (Book b : books) {
			if(b.getTitle().equals(title)) {
				book = b;
				return book;
			}
		}
		return book;
	}

	public boolean addBook(Book book) {
		loadBooksFromRedis();
		for (Book b : books) {
			if(b.equals(book)) {
				return false;
			}
		}
		books.add(book);
		saveBooksToRedis();
		return true;
	}

	public boolean update(Book book) {
		loadBooksFromRedis();
		ArrayList<Book> temp = new ArrayList<>(books);
		for (Book b : temp) {
			if(book.equals(b)) {
				books.remove(b);
				books.add(book);
				saveBooksToRedis();
				return true;
			}
		}
		
		return false;
	}

	public boolean delete(Book book) {
		loadBooksFromRedis();
		for (Book b : books) {
			if(b.equals(book)) {
				books.remove(b);
				saveBooksToRedis();
				return true;
			}
		}
		return false;
	}

	public boolean deleteById(int id) {
		loadBooksFromRedis();
		for (Book b : books) {
			if(b.getId() == id) {
				books.remove(b);
				saveBooksToRedis();
				return true;
			}
		}
		return false;
	}

}
