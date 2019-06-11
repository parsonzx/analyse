package com.example.wordanalyze.Service;

import com.example.wordanalyze.util.HttpClientUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyseService {
    private final static String URL="https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token=";
    public String analyseUrl(String accessToken,String url) {

        Map<String,String> params=new HashMap<>();
        params.put("url",url);
        String   content = HttpClientUtil.doPost(URL+accessToken,params);

       return content;
    }
    public String analyseFile(String accessToken,String file){
        Map<String,String> params=new HashMap<>();
        params.put("image",file);
        String content=HttpClientUtil.doPost(URL+accessToken,params);
        return content;
    }
}
