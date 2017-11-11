package com.y.fish.base.api.repository;

import com.y.fish.base.api.model.Permission;
import com.y.fish.base.api.model.Resource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by myliang on 11/7/17.
 */
@Repository
public class ResourceRepository extends BaseRepository<Resource> {

    @Override
    public String tableName() {
        return Resource.TABLE_NAME;
    }

    @Override
    public String[] columnNames() {
        return new String[] {
                "entity_name", "menu_id", "permission"
        };
    }

    @Override
    public Resource convert(ResultSet rs) throws SQLException {
        Resource resource = new Resource();
        resource.setId(rs.getLong("id"));
        resource.setEntityName(rs.getString("entity_name"));
        resource.setMenuId(rs.getLong("menu_id"));
        resource.setPermission(Permission.valueOf(rs.getString("permission")));
        return resource;
    }

}
