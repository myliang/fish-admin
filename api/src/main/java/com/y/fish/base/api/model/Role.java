package com.y.fish.base.api.model;

import lombok.Data;
import org.springframework.boot.json.JsonParserFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by myliang on 11/7/17.
 */
@Data
public class Role {

    public static final String TABLE_NAME = "roles";

    private long id;

    private String name;

    private String permissions;

    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 验证是否有访问权限
     * @param entity entity名称
     * @param access 访问权限： read, create, update, destroy
     * @return
     */
    public boolean hasPermission(Object entity, Object access) {
        Map ps = JsonParserFactory.getJsonParser().parseMap(permissions);

        String entityName;
        if (entity instanceof String) {
            entityName = (String) entity;
        } else {
            entityName = (String) entity;
        }

        String permission;
        if (access instanceof Permission) {
            permission = ((Permission) access).name();
        } else {
            permission = (String) access;
        }

        if (ps.containsKey(entityName)) {
            List<String> methodNames = (List<String>) ps.get(entityName);
            return methodNames.stream().filter(m -> m.equals(permission)).count() > 0;
        }
        return false;
    }

    public Map getPermissionsMap() {
        return JsonParserFactory.getJsonParser().parseMap(permissions);
    }


}
