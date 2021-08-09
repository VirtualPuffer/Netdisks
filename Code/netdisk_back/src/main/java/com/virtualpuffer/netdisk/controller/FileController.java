package com.virtualpuffer.netdisk.controller;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FileController {
    @RequestMapping(value = "/download/{fileCode}")
    public static String test(@PathVariable String fileCode){

        return fileCode;
    }
}
