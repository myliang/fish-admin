package com.y.fish.base.api.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.escape.ArrayBasedUnicodeEscaper;
import com.y.fish.base.api.model.Storage;
import com.y.fish.base.api.sql.Pagination;
import com.y.fish.base.api.sql.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by myliang on 11/7/17.
 * 涉及到插入和更新，如果在存在created_at, updated_at字段，那么请不要设置任何值，系统会自动设置time更新数据库
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
    public abstract int[] columnTypes();
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
    public String createdAtName() { return "created_at"; }
    public String updatedAtName() { return "updated_at"; }

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
        int[] columnTypes = columnTypes();
        List<Object> values = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        List<Integer> types = new ArrayList<>();
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            String fieldName = changeColumnToFieldName(columnName);
            Field field = getDeclaredField(t.getClass(), fieldName);
            Object v = field.get(t);
            if (v != null) {
                keys.add(columnName);
                values.add(v);
                types.add(columnTypes[i]);
            }
        }

        long id = insert(keys, values, types.stream().mapToInt(type -> type).toArray());
        Field idField = getDeclaredField(t.getClass(), primaryKeyName());
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
            if (columnName.equals(createdAtName())) continue;

            String fieldName = changeColumnToFieldName(columnName);
            Field field = getDeclaredField(t.getClass(), fieldName);
            Object v = field.get(t);
            Object oldV = field.get(old);
            if (v != null && !v.equals(oldV)) {
                keys.add(columnName);
                values.add(v);
            }

        }
        update(keys.toArray(new String[keys.size()]), values.toArray(new Object[values.size()]), id);
    }

    public long insert(List<String> columns, List<Object> args, int[] types) {
        String insertColumns = primaryKeyName();
        String insertPlaceholders = "nextval('"+tableName()+"_id_seq')";
        for (String column : columns) {
            insertColumns += "," + column;
            insertPlaceholders += ",?";
        }

        insertColumns += ", " + createdAtName();
        insertPlaceholders += ", now()";
        insertColumns += ", " + updatedAtName();
        insertPlaceholders += ", now()";

        String sql = "insert into " + tableName() +
                " ("+insertColumns+") values ("+insertPlaceholders+")";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sql, types);
        pscf.setReturnGeneratedKeys(true);
        try {
            logger.info("insert.sql: {}, args: {}", sql, jsonMapper.writeValueAsString(args));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        writeJdbcTemplate.update(pscf.newPreparedStatementCreator(args), keyHolder);
        if (keyHolder.getKeyList() != null && !keyHolder.getKeyList().isEmpty()) {
            return (long) keyHolder.getKeyList().get(0).get(primaryKeyName());
        }
        return -1;
    }

    public void update(String[] columns, Object[] args, long id) {
        if (columns == null || columns.length <= 0) return;

        String sql = "update " + tableName() + " set ";
        sql += updatedAtName() + " = now(), ";

        sql += Arrays.stream(columns).map((column) -> column + " = ?").collect(Collectors.joining(","));
        sql += " where id = " + id;
        try {
            logger.info("update.sql: {}, args: {}", sql, jsonMapper.writeValueAsString(args));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
        StringBuffer sb = new StringBuffer(array[0]);
        for (int i = 1; i < array.length; i++) {
            String cn = array[i];
            sb.append(cn.substring(0, 1).toUpperCase()).append(cn.substring(1));
        }
        return sb.toString();
    }

    static Map<Class, Map<String, Field>> fieldMapCache = new HashMap();

    static Field getDeclaredField(Class target, String fieldName) throws NoSuchFieldException {
        if (!fieldMapCache.containsKey(target)) {
            fieldMapCache.put(target, new HashMap<>());
        }
        Map<String, Field> targetMap = fieldMapCache.get(target);
        if (!targetMap.containsKey(fieldName)) {
            Field field = target.getDeclaredField(fieldName);
            field.setAccessible(true);
            targetMap.put(fieldName, field);
        }
        return targetMap.get(fieldName);
    }

}
