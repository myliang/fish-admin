package com.y.fish.base.api.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by myliang on 11/7/17.
 */
@Data
public class Storage {

    public static String TABLE_NAME = "storages";

    private Long id;

    private String fileName;

    private String originalFileName;

    private String fileType;

    private Long fileSize;

    private String fileHash;

    private LocalDateTime createdAt = LocalDateTime.now();

    public String getPath() {
        return fileHash + "." + originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }

}
