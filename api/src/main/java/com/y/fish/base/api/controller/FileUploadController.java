package com.y.fish.base.api.controller;

import com.y.fish.base.api.model.Storage;
import com.y.fish.base.api.service.StorageService;
import com.y.fish.base.api.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by myliang on 2/26/17.
 */

@RestController
@RequestMapping("/api/files/")
public class FileUploadController {

    @Autowired
    StorageService storageService;

    @GetMapping("{fileName:.+}")
    public ResponseEntity<Resource> show(@PathVariable String fileName) {
        Resource file = storageService.loadAsResource(fileName);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileName+"\"")
                .body(file);
    }

    @GetMapping("{size}/{fileName:.+}")
    public ResponseEntity<Resource> cut(@PathVariable String fileName, @PathVariable String size) throws IOException {

        try {
            Resource file = storageService.loadAsResource(fileName);
            String fileExtendName = fileName.substring(fileName.lastIndexOf(".") + 1);
            String[] wh = size.split("_");
            int w = Integer.parseInt(wh[0]);
            int h = Integer.parseInt(wh[1]);

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileName+"\"")
                    .body(new ByteArrayResource(ImageUtil.compress(file.getInputStream(), fileExtendName, w, h)));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileName+"\"")
                    .body(new ByteArrayResource(new byte[]{}));
        }
    }

    @PostMapping({"upload"})
    public String handle(@RequestParam MultipartFile file) throws Exception {
        Storage storage = storageService.store(file);
        return storage.getPath();
    }

}
