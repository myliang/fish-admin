package com.y.fish.base.api.model;

import lombok.Data;

/**
 * Created by myliang on 11/7/17.
 */
@Data
public class Menu {

    public static final String TABLE_NAME = "menus";

    private long id, parentId;

    private String key, name, url;

}
