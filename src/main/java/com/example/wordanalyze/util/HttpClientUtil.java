package com.example.wordanalyze.util;


import io.micrometer.core.instrument.util.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpClientUtil {
    public static Logger logger = LogManager.getLogger(HttpClientUtil.class);

    public static String doGet(String url, Map<String, String> params) {
        HttpResponse response = null;
        String content = "";
        HttpClient httpClient = HttpClients.custom()
                .build();
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key));
                }
            }
            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(150000).setConnectionRequestTimeout(150000)
                    .setSocketTimeout(150000
                    ).build();
            request.setConfig(requestConfig);

            response = httpClient.execute(request);
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return content;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doPost(String url, Map<String, String> params) {
        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type","application/x-www-form-urlencoded");
        HttpResponse response = null;
        String content = "";
        HttpClient httpClient = HttpClients.custom()
                .build();
       RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000).setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000).build();
        request.setConfig(requestConfig);
        if (params != null) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (String key : params.keySet()) {
                paramList.add(new BasicNameValuePair(key, params.get(key)));
            }
            request.setEntity(new UrlEncodedFormEntity(paramList, Consts.UTF_8));
        }

        try {
            response = httpClient.execute(request);
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String doPostJson(String url, String data) {
        HttpPost request = new HttpPost(url);
        HttpResponse response = null;
        String content = "";
        HttpClient httpClient = HttpClients.custom()
                .build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000).setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000).build();
        request.setConfig(requestConfig);
        if (StringUtils.isNotBlank(data)) {
            StringEntity se = new StringEntity(data, "utf-8");
            se.setContentType("application/json");
            request.setEntity(se);
        }
        try {
            logger.info("post json url " + url);
            logger.info("post json data :" + data);
            response = httpClient.execute(request);
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String doPost(String url) {
        return doPostJson(url, null);
    }





}
