package com.bjksrs.crawler_sends.controller;

import com.bjksrs.crawler_sends.pojo.UpBean;
import com.bjksrs.crawler_sends.service.UpService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;


import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@MultipartConfig
@RestController
public class UpController {

    @Value("${file.uploadFolder}")
    private String uploadFolder;
    @Value("${sends.tempFolder}")
    private String tempFolder;
    @Value("${sends.posturl}")
    private String posturl;

    @Autowired
    private ApplicationArguments applicationArguments;

    @Autowired
    private UpService upService;

    private Logger log = LoggerFactory.getLogger(UpController.class);


    @RequestMapping("index")
    public String index(){
        return "hello";
    }
    @RequestMapping("upload")
    public void processUpload(HttpServletRequest request, HttpServletResponse response) {
        List<String> nonOptionArgs = null;
        try {
            nonOptionArgs = applicationArguments.getNonOptionArgs();
            System.out.println(nonOptionArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        for (String param : nonOptionArgs) {
            String[] arr = param.split("=");
            map.put(arr[0], arr[1]);
        }
        System.out.println(map);
        if ("service".equals(map.get("module"))) {
            log.info("module="+map.get("module"));
            try {
                request.setCharacterEncoding("UTF-8");
                response.setCharacterEncoding("utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Collection<javax.servlet.http.Part> coll = request.getParts();
                for (javax.servlet.http.Part part : coll) {

                    Date date = new Date();
                    DateFormat df = new SimpleDateFormat("yyyyMMdd");
                    String newName = df.format(date);
                    String suffixName = part.getName().substring(part.getName().lastIndexOf("."));

                    if(!uploadFolder.endsWith("/")){
                        uploadFolder += "/";
                    }

                    File uploadFile = new File(uploadFolder + newName + suffixName);
                    InputStream input = part.getInputStream();

                    int index;
                    byte[] bytes = new byte[1024];
                    FileOutputStream downloadFile = new FileOutputStream(uploadFile);
                    while ((index = input.read(bytes)) != -1) {
                        downloadFile.write(bytes, 0, index);
                        downloadFile.flush();
                    }
                    downloadFile.close();
                    input.close();


                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            response.addHeader("token", "hello");
        }
    }

    private File getData() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String createTime = df.format(date);

        List<UpBean> list = upService.getData(createTime);
        List<String> nlist = new ArrayList<String>();
        if (list == null || list.isEmpty()) {
            log.info("data is null");
            nlist.add("Null");
        } else {
            for (UpBean upBean : list) {
                nlist.add(upBean.getCompanyName());
            }
        }
        try {
            if(!tempFolder.endsWith("/")){
                tempFolder += "/";
            }
            File file = new File(tempFolder + createTime + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter writer = new BufferedWriter(fw);
            for (String str : nlist) {
                writer.write(str);
                writer.newLine();
                writer.flush();
            }

            writer.close();
            fw.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @RequestMapping("up")
    @Scheduled(cron = "0 0 19 * * ?")
    public void upload() {
        log.info("up");
        List<String> nonOptionArgs = null;
        try {
            nonOptionArgs = applicationArguments.getNonOptionArgs();
            System.out.println(nonOptionArgs);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        for (String param : nonOptionArgs) {
            log.info("param:"+param);
            String[] arr = param.split("=");
            map.put(arr[0], arr[1]);
        }
        if ("local".equals(map.get("module"))) {
            log.info("file");
            File file = getData();
            String uri = posturl + "/upload";
            PostMethod filePost = new PostMethod(uri);
            HttpClient client = new HttpClient();

            try {
                // 通过以下方法可以模拟页面参数提交
//            filePost.setParameter("userName", userName);
//            filePost.setParameter("passwd", passwd);

                Part[] parts = {new FilePart(file.getName(), file)};
                filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

                client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);

                int status = client.executeMethod(filePost);
                if (status == HttpStatus.SC_OK) {
                    log.info("success");
                } else {
                    log.info("default");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex.getMessage());
            } finally {
                filePost.releaseConnection();
            }

        }
    }

}