package com.y.fish.base.api.sql;

import lombok.Data;

import java.util.List;

/**
 * Created by myliang on 11/7/17.
 */
@Data
public class Pagination<T> {

    private long total;
    private List<T> content;

}
