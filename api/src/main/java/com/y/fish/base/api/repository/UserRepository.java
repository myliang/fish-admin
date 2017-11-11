package com.y.fish.base.api.repository;

import com.y.fish.base.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by myliang on 7/20/17.
 */
@Repository
public class UserRepository extends BaseRepository<User> {

    @Autowired
    @Qualifier("writeJdbcTemplate")
    JdbcTemplate writeJdbcTemplate;

    public User findByUserName(String userName) {
        return this.find("user_name = ?", new Object[]{ userName });
    }

    @Override
    public String tableName() {
        return User.TABLE_NAME;
    }

    @Override
    public String[] columnNames() {
        return new String[] {
                "user_name", "encrypted_password", "state", "created_at"
        };
    }

    @Override
    public User convert(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUserName(rs.getString("user_name"));
        user.setEncryptedPassword(rs.getString("encrypted_password"));
        user.setState(User.State.valueOf(rs.getString("state")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }

}
