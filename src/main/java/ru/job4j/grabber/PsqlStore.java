package ru.job4j.grabber;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private final Connection cnn;

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
        ps.setString(3, post.getLink());
        ps.setString(2, post.getDescription());
        ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
        ps.execute();
    }

    private Post createPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getString("name"),
                rs.getString("link"),
                rs.getString("text"),
                rs.getTimestamp("created").toLocalDateTime()
        );
    }

    @Override
    public List<Post> getAll() throws SQLException {
        List<Post> posts = new ArrayList<>();
        PreparedStatement ps = cnn.prepareStatement("SELECT * FROM post");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Post post = createPost(rs);
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
           post = createPost(rs);
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
