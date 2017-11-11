package com.y.fish.base.api.sql;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by myliang on 11/7/17.
 */
public class SqlQuery {

    HttpServletRequest request;
    List<Express> expresses = new ArrayList<>();
    List<Object> params = new ArrayList<>();
    String sql = "1=1";
    String orderKey = "id";
    Boolean orderDesc = false;

    private SqlQuery(HttpServletRequest request) {
        this.request = request;
    }

    public static SqlQuery build(HttpServletRequest request) {
        return new SqlQuery(request);
    }

    public SqlQuery eq(String key) {
        expresses.add(new Express(key, "="));
        return this;
    }

    public SqlQuery like(String key) {
        expresses.add(new Express(key, "like"));
        return this;
    }

    public int pageRows() {
        String rows = request.getParameter("rows");
        if (rows != null && !"".equalsIgnoreCase(rows)) {
            return Integer.parseInt(rows);
        }
        return 20;
    }

    public int pageOffset() {
        String offset = request.getParameter("page");
        if (offset != null && !"".equalsIgnoreCase(offset)) {
            return Integer.parseInt(offset);
        }
        return 0;
    }

    public SqlQuery order(String key) {
        return this.order(key, true);
    }

    public SqlQuery order(String key, boolean desc) {
        this.orderKey = key;
        this.orderDesc = desc;
        return this;
    }

    public SqlQuery toSql() {
        for (Express express : expresses) {
            String v = request.getParameter(express.key);
            if (v != null && !"".equalsIgnoreCase(v)) {
                sql += " and " + camel2Underline(express.key) + " "+express.operator+" ?";
                if ("like".equalsIgnoreCase(express.operator)) {
                    params.add("%" + v + "%");
                } else {
                    params.add(v);
                }
            }
        }
        return this;
    }

    public String getOrderBy() {
        return " " + this.orderKey + " " + (orderDesc ? "desc" : "asc");
    }

    public String getSql() {
        return sql;
    }

    public Object[] getParams() {
        return params.toArray(new Object[params.size()]);
    }

    class Express {
        public String key;
        public String operator;
        public Express(String key, String operator) {
            this.key = key;
            this.operator = operator;
        }
    }

    public static String camel2Underline(String fieldName) {
        if(fieldName == null||"".equals(fieldName)){
            return "";
        }
        fieldName = String.valueOf(fieldName.charAt(0)).toUpperCase().concat(fieldName.substring(1));
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher=pattern.matcher(fieldName);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end() == fieldName.length() ? "" : "_");
        }
        return sb.toString();
    }

}
