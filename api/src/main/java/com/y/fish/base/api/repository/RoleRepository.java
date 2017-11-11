package com.y.fish.base.api.repository;

import com.y.fish.base.api.model.Role;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by myliang on 11/7/17.
 */
@Repository
public class RoleRepository extends BaseRepository<Role> {

    @Override
    public String tableName() {
        return Role.TABLE_NAME;
    }

    @Override
    public String[] columnNames() {
        return new String[] {
                "name", "permissions"
        };
    }

    @Override
    public Role convert(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        role.setPermissions(rs.getString("permissions"));
        return role;
    }

}
