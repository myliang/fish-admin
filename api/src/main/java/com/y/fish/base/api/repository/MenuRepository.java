package com.y.fish.base.api.repository;

import com.y.fish.base.api.model.Menu;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created by myliang on 11/7/17.
 */
@Repository
public class MenuRepository extends BaseRepository<Menu> {

    @Override
    public String tableName() {
        return Menu.TABLE_NAME;
    }

    @Override
    public String[] columnNames() {
        return new String[] {
                "name", "key", "parent_id", "url"
        };
    }

    @Override
    public int[] columnTypes() {
        return new int[] {
                Types.VARCHAR, Types.VARCHAR, Types.BIGINT, Types.VARCHAR
        };
    }

    public List<Menu> findByParentId(long parentId) {
        return where("parent_id = ?", new Object[]{ parentId });
    }

    @Override
    public Menu convert(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setId(rs.getLong("id"));
        menu.setName(rs.getString("name"));
        menu.setKey(rs.getString("key"));
        menu.setParentId(rs.getLong("parent_id"));
        menu.setUrl(rs.getString("url"));
        return menu;
    }
}
