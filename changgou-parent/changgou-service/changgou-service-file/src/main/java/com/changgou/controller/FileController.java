package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhouzhu
 * @Description
 * @create 2020-03-12 17:05
 */
@RestController
@RequestMapping("upload")
@CrossOrigin
public class FileController {

    @PostMapping
    public Result upload(@RequestParam(value = "file")MultipartFile file) throws Exception{
        //封装文件信息
        FastDFSFile fastDFSFile = new FastDFSFile(file.getOriginalFilename(),file.getBytes(), StringUtils.getFilenameExtension(file.getOriginalFilename()));

        //上传文件
        FastDFSUtil.upload(fastDFSFile);
        return new Result(true, StatusCode.OK,"上传成功");
    }
}
