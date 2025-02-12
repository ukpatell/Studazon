package com.studazon.portal.dao;

import com.studazon.portal.entity.Book;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BookDAO {
    protected static Properties props;
    private static final String SELECT_ALL_BOOKS_SQL = "SELECT * FROM books";
    private static final String SELECT_ALL_USER_BOOKS_SQL = "SELECT * FROM books WHERE user_id = ?";
    private static final String SELECT_BOOK_BY_ID_SQL = "SELECT * FROM books WHERE id = ?";
    private static final String INSERT_BOOK_SQL = "INSERT INTO books (user_id, title, author, isbn, book_condition, image_url,comments,price) VALUES (?, ?, ?, ?, ?, ?,?,?)";
    private static final String UPDATE_BOOK_SQL = "UPDATE books SET user_id = ?, title = ?, author = ?, isbn = ?, book_condition = ?, image_url = ?, comments = ?, price = ? WHERE id = ?";
//    private static final String UPDATE_BOOK_W_IMAGE_SQL = "UPDATE books SET user_id = ?, title = ?, author = ?, isbn = ?, book_condition = ?, image_url = ?, comments = ?, price = ? WHERE id = ?";

    private static final String DELETE_BOOK_SQL = "DELETE FROM books WHERE id = ?";


    protected static Connection getConnection() {
        props = new Properties();
        Connection currentCon = null;

        try {
            props.load(BookDAO.class.getClassLoader().getResourceAsStream("database.properties"));
            Class.forName("com.mysql.cj.jdbc.Driver");
            currentCon = DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.username"), props.getProperty("db.password"));
        } catch (IOException | SQLException | ClassNotFoundException e) {
            System.out.println("Error loading database.properties: " + e);
            throw new RuntimeException(e);
        }
        return currentCon;
    }

    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try {
            System.out.println("BookDAO (getAllBooks): service requested");
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL_BOOKS_SQL);
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getInt("user_id"), rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getString("book_condition"), rs.getBytes("image_url"), rs.getString("comments"), rs.getDouble("price"));
                books.add(book);
            }
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("BookDAO (getAllBooks): service complete");
        return books;
    }
    public static List<Book> getAllBooks(String search) {
        List<Book> books = new ArrayList<>();
        try {
            System.out.println("BookDAO (getAllBooks w. search): service requested");
            Connection conn = getConnection();
            String sql = SELECT_ALL_BOOKS_SQL;
            if (search != null && !search.isEmpty()) {
                sql += " WHERE title LIKE '%" + search + "%' OR author LIKE '%" + search + "%' OR isbn LIKE '%" + search + "%'";
            }
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getInt("user_id"), rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getString("book_condition"), rs.getBytes("image_url"), rs.getString("comments"), rs.getDouble("price"));
                System.out.println(book.getTitle());
                books.add(book);
            }
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("BookDAO (getAllBooks w. search): service complete");
        return books;
    }

    public static List<Book> getAllBooks(int userId) {
        List<Book> books = new ArrayList<>();
        try {
            System.out.println("BookDAO (getAllBooks): service requested");
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_USER_BOOKS_SQL);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getInt("user_id"), rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getString("book_condition"), rs.getBytes("image_url"), rs.getString("comments"), rs.getDouble("price"));
                System.out.println(book.getTitle());
                books.add(book);
            }
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("BookDAO (getAllBooks): service complete");
        return books;
    }

    public static void insertBook(Book book) {
        try {
            System.out.println("BookDAO (insertBook): service requested");
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_BOOK_SQL);
            stmt.setInt(1, book.getUserId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getISBN());
            stmt.setString(5, book.getBook_condition());
            stmt.setBlob(6, new ByteArrayInputStream(book.getImageUrl()));
            stmt.setString(7, book.getComments());
            stmt.setDouble(8, book.getPrice());
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("BookDAO (insertBook): service complete");
    }

    public static void updateBook(Book book) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_BOOK_SQL)) {
            stmt.setInt(1, book.getUserId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getISBN());
            stmt.setString(5, book.getBook_condition());
            stmt.setBlob(6, new ByteArrayInputStream(book.getImageUrl()));
            stmt.setString(7, book.getComments());
            stmt.setDouble(8, book.getPrice());
            stmt.setInt(9, book.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Book getBookById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BOOK_BY_ID_SQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getInt("user_id"), rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getString("book_condition"), rs.getBytes("image_url"), rs.getString("comments"), rs.getDouble("price"));
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteBook(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_BOOK_SQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



