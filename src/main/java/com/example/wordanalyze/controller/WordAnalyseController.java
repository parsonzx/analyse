package com.example.wordanalyze.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.wordanalyze.Service.AnalyseService;
import com.example.wordanalyze.Service.AuthService;

import com.example.wordanalyze.util.ImageCut;
import com.example.wordanalyze.util.RedisUtil;


import io.micrometer.core.instrument.util.StringUtils;
import jodd.util.ObjectUtil;
import jodd.util.StringUtil;
import org.springframework.boot.json.JsonParser;
import sun.misc.BASE64Encoder;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.io.IOException;
import java.util.Iterator;


@RestController
@RequestMapping("/analyse")
public class WordAnalyseController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AnalyseService analyseService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String analyse(@RequestParam(value="image_url" ,required = false) String imageUrl,
                          MultipartHttpServletRequest request )throws IllegalStateException, IOException {

        String content="";
        if(StringUtils.isNotEmpty(imageUrl)) {
            if (redisUtil.hasKey(imageUrl)) {
                content = redisUtil.get(imageUrl).toString();
            } else {
                ImageCut imageCut = new ImageCut();
                String image = imageCut.downImage(imageUrl);
                String accessToken = AuthService.getAuth();
                content = analyseService.analyseFile(accessToken,image);
                JSONObject obj = JSONObject.parseObject(content);
                if (obj.get("error_code") == null) {
                    redisUtil.set(imageUrl, content);
                }
            }
        }
        Iterator<String> itr = request.getFileNames();
        if(itr.hasNext()){
            MultipartFile mf = request.getFile(itr.next());
            ImageCut imageCut =new ImageCut();
            byte[] imageByte= imageCut.cutImage(mf);
            BASE64Encoder encoder=new BASE64Encoder();
            String image=encoder.encode(imageByte);
            //  image= URLEncoder.encode(image);
            String accessToken=AuthService.getAuth();
            content=analyseService.analyseFile(accessToken,image);
        }
    return content;
    }

}
