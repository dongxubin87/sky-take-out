package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "common apis")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * upload image
     *
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("upload image")
    public Result<String> upload(MultipartFile file) {
        log.info("upload image:{}", file);
        try {
            // get original file name
            String originalFilename = file.getOriginalFilename();
            // get extension  xxxxx.jpg
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            //set new filename in ali cloud
            String objectName = UUID.randomUUID().toString() + extension;
            String flePath = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(flePath);
        } catch (IOException e) {
           log.error("upload file failed: {}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
