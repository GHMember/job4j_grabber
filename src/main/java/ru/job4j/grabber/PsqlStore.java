package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException, ClassNotFoundException {
        Class.forName(cfg.getProperty("jdbc.driver"));
        this.cnn = DriverManager.getConnection(
                cfg.getProperty("url"),
                cfg.getProperty("username"),
                cfg.getProperty("password")
        );
    }

    @Override
    public void save(Post post) throws SQLException {
        PreparedStatement ps = cnn.prepareStatement(
                "INSERT INTO post(name, text, link, created) VALUES(?, ?, ?, ?)"
                        + "ON CONFLICT(id) DO UPDATE SET link = EXCLUDED.link");
        ps.setString(1, post.getTitle());
        ps.setString(2, post.getDescription());
        ps.setString(3, post.getLink());
        ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
        ps.execute();
    }

    @Override
    public List<Post> getAll() throws SQLException {
        List<Post> posts = new ArrayList<>();
        PreparedStatement ps = cnn.prepareStatement("SELECT * FROM post");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Post post = new Post(
                    rs.getString("name"),
                    rs.getString("text"),
                    rs.getString("link"),
                    rs.getTimestamp("created").toLocalDateTime()
            );
            post.setId(rs.getInt("id"));
            posts.add(post);
        }
        return posts;
    }

    @Override
    public Post findById(int id) throws SQLException {
        Post post = null;
        PreparedStatement ps = cnn.prepareStatement("SELECT * FROM post WHERE id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
           post = new Post(
                   rs.getString("name"),
                   rs.getString("text"),
                   rs.getString("link"),
                   rs.getTimestamp("created").toLocalDateTime()
           );
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws Exception {
        InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("grabber.properties");
        Properties config = new Properties();
        config.load(in);
        try (PsqlStore store = new PsqlStore(config)) {
            store.save(new Post("Title", "Text", "Link", LocalDateTime.now()));
            List<Post> posts = store.getAll();
            for (Post post : posts) {
                System.out.println(post.getTitle());
                System.out.println(post.getLink());
                System.out.println(post.getCreated());
                System.out.println(post.getDescription());
            }
            Post post = store.findById(1);
            System.out.println(post.getTitle());
            System.out.println(post.getLink());
            System.out.println(post.getCreated());
            System.out.println(post.getDescription());
        }
    }
}
