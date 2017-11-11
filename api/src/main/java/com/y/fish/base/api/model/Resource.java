package com.y.fish.base.api.model;

import lombok.Data;

/**
 * Created by myliang on 11/7/17.
 */
@Data
public class Resource {

    public static final String TABLE_NAME = "resources";

    private long id, menuId;

    private String entityName;

    private Permission permission;

}
