package com.y.fish.base.api.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.y.fish.base.api.model.Storage;
import com.y.fish.base.api.sql.Pagination;
import com.y.fish.base.api.sql.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by myliang on 11/7/17.
 */
public abstract class BaseRepository<T> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    static ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    @Qualifier("writeJdbcTemplate")
    JdbcTemplate writeJdbcTemplate;

    @Autowired
    @Qualifier("readJdbcTemplate")
    JdbcTemplate readJdbcTemplate;

    public abstract String tableName();
    public abstract String[] columnNames();
    public abstract T convert(ResultSet rs) throws SQLException;

    public String selectSql() {
        return "select * from " + tableName() + "";
    }
    public String selectWhereSql () {
        return "select * from " + tableName() + " where ";
    }
    public String primaryKeyName() {
        return "id";
    }

    public T find(long id) {
        return find(primaryKeyName() + " = ?", new Object[]{id});
    }

    public T find(String sql, Object[] args) {
        try {
            sql = selectWhereSql() + sql;
            logger.info("find.sql: " + sql + ", args: " + jsonMapper.writeValueAsString(args));
            return readJdbcTemplate.queryForObject(sql, args, new BaseRowMapper());
        } catch (EmptyResultDataAccessException e) {
        } catch (JsonProcessingException e) {
        }
        return null;
    }

    public List<T> where(String sql, Object[] args) {
        if (sql == null || "".equalsIgnoreCase(sql)) {
            sql = selectSql() + sql;
        } else {
            sql = selectWhereSql() + sql;
        }
        try {
            logger.info("where.sql: " + sql + ", args: " + jsonMapper.writeValueAsString(args));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return readJdbcTemplate.query(sql, args, new BaseRowMapper());
    }

    public List<T> all() {
        return where("", new Object[]{});
    }

    public long count(String sql, Object[] args) {
        return readJdbcTemplate.queryForObject(
                "select count(1) from " + tableName() + " where " + sql, args, Long.class);
    }

    public List<T> where(String sql, Object[] args, String order, int limit, int offset) {
        if (sql == null || "".equalsIgnoreCase(sql)) {
            sql = selectSql() + sql;
        } else {
            sql = selectWhereSql() + sql;
        }
        sql += " order by " + order + " limit " + limit + " offset " + offset * limit;
        try {
            logger.info("list.where.sql: " + sql + ", args: " + jsonMapper.writeValueAsString(args));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return readJdbcTemplate.query(sql, args, new BaseRowMapper());
    }

    public Pagination<T> where(SqlQuery query) {
        Pagination<T> pagination = new Pagination();
        query.toSql();
        pagination.setTotal(count(query.getSql(), query.getParams()));
        pagination.setContent(where(query.getSql(), query.getParams(),
                query.getOrderBy(), query.pageRows(), query.pageOffset()));
        return pagination;
    }

    public T save(T t) throws Exception {
        String[] columnNames = columnNames();
        List<Object> values = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            String fieldName = changeColumnToFieldName(columnName);
            Field field = t.getClass().getDeclaredField(fieldName);
            Object v = field.get(t);
            if (v != null) {
                keys.add(columnName);
                values.add(v);
            }
        }
        long id = insert(keys.toArray(new String[keys.size()]), values.toArray(new Object[values.size()]));
        Field idField = t.getClass().getDeclaredField(primaryKeyName());
        idField.setAccessible(true);
        idField.set(t, id);
        return t;
    }

    public void update(T t, long id) throws Exception {
        T old = find(id);
        String[] columnNames = columnNames();
        List<Object> values = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            if (columnName.equals("created_at")) continue;

            String fieldName = changeColumnToFieldName(columnName);
            Field field = t.getClass().getDeclaredField(fieldName);
            Object v = field.get(t);
            Object oldV = field.get(old);
            if (v != null && !v.equals(oldV)) {
                keys.add(columnName);
                values.add(v);
            }

        }
        update(keys.toArray(new String[keys.size()]), values.toArray(new Object[values.size()]), id);
    }

    public long insert(String[] columns, Object[] args) {
        String insertColumns = "";
        String insertPlaceholders = "";
        for (String column : columns) {
            insertColumns += column + ",";
            insertPlaceholders += "?,";
        }

        String sql = "insert into " + Storage.TABLE_NAME +
                " ("+insertColumns.substring(0, insertColumns.length() - 1)+") values ("+
                insertPlaceholders.substring(0, insertPlaceholders.length() - 1)+")";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sql);
        writeJdbcTemplate.update(pscf.newPreparedStatementCreator(args), keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void update(String[] columns, Object[] args, long id) {
        String sql = "update " + tableName() + " set ";
        sql += Arrays.stream(columns).map((column) -> column + " = ?").collect(Collectors.joining(","));
        sql += " where id = ?";
        writeJdbcTemplate.update(sql, args);
    }

    public void delete(long id) {
        writeJdbcTemplate.update("delete from " + tableName() + " where "+primaryKeyName()+" = ?",
                new Object[] { id });
    }

    class BaseRowMapper implements RowMapper<T> {
        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            return convert(rs);
        }
    }

    public static String changeColumnToFieldName(String columnName) {
        String[] array = columnName.split("_");
        StringBuffer sb = new StringBuffer();
        for (String cn : array) {
            sb.append(cn.substring(0, 1).toUpperCase()).append(cn.substring(1));
        }
        return sb.toString();
    }

}
