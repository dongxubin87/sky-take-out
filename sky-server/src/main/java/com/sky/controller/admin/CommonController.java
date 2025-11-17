package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "common apis")
@Slf4j
public class CommonController {

//    @Autowired
//    private AliOssUtil aliOssUtil;
//
//    /**
//     * upload image
//     *
//     * @return
//     */
//    @PostMapping("/upload")
//    @ApiOperation("upload image")
//    public Result<String> upload(MultipartFile file) {
//        log.info("upload image:{}", file);
//        try {
//            // get original file name
//            String originalFilename = file.getOriginalFilename();
//            // get extension  xxxxx.jpg
//            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//
//            //set new filename in ali cloud
//            String objectName = UUID.randomUUID().toString() + extension;
//            String flePath = aliOssUtil.upload(file.getBytes(), objectName);
//
//            return Result.success(flePath);
//        } catch (IOException e) {
//           log.error("upload file failed: {}", e);
//        }
//        return Result.error(MessageConstant.UPLOAD_FAILED);
//    }

    private static String FILE_UPLOAD_PATH = "/Users/jackdong/work/sky-take-out/sky-server/src/main/resources/upload/";

    @PostMapping("/upload")
    @ResponseBody
    public Result uploadfile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("file is empty");
        }

        File dir = new File(FILE_UPLOAD_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            boolean created = dir.mkdirs();
            if(created) {
                log.info("create file successfully: {}", FILE_UPLOAD_PATH);
            } else {
                log.warn("failed to create file: {}", FILE_UPLOAD_PATH);
            }
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error("file name is invalid");
        }
        // get extension
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!extension.equalsIgnoreCase(".png") && !extension.equalsIgnoreCase(".jpg") && !extension.equalsIgnoreCase(".jpeg")) {
            return Result.error("file type not supported");
        }
        // get random fle name
        originalFilename = UUID.randomUUID().toString() + extension;


        // ensure path is safe
        Path targetLocation = Paths.get(FILE_UPLOAD_PATH).resolve(originalFilename).normalize();
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("file upload successfully: {}", originalFilename);
        } catch (IOException e) {
            log.error("failed to upload file: {}", originalFilename, e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

        // url to the file
        String fileUrl = "http://localhost:8080/static/" + originalFilename;
        return Result.success(fileUrl);
    }

}
