package com.bjksrs.crawler_sends.controller;

import com.bjksrs.crawler_sends.service.DownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.annotation.MultipartConfig;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
@MultipartConfig
@RestController
public class DownController {

    @Value("${file.downloadFolder}")
    private String downloadFolder;
    @Value("${sends.web}")
    private String web;

    @Autowired
    private DownService downService;
    @Autowired
    private ApplicationArguments applicationArguments;

    private Logger log = LoggerFactory.getLogger(DownController.class);

    @RequestMapping("download")
    @Scheduled(cron="0 30 19 * * ?")
    public void insert(){
        log.info("定时任务开启!");
        List<String> nonOptionArgs = null;
        try {
            nonOptionArgs = applicationArguments.getNonOptionArgs();
        }catch (Exception e){
            e.printStackTrace();
            log.info("请在执行时添加参数module=client");
            return ;
        }
        Map<String,String> map = new HashMap<String,String>();
        for (String param : nonOptionArgs){
            String[] arr = param.split("=");
            map.put(arr[0],arr[1]);
        }
        if("client".equals(map.get("module"))) {
            log.info("开始插入");
            List<String> list = download();
            downService.insert(list);
        }else{
            log.info("请在执行时添加参数module=client");
        }
    }

    private List<String> download() {
        log.info("获取文件!");
        //得到long类型当前时间
        long l = System.currentTimeMillis();
        //new日期对象
        Date date = new Date(l);
        //转换提日期输出格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        //把日期名做为名称传入
        String exe = dateFormat.format(date)+".txt";
        //下载当天数据，网址
        String photoUrl = web+"/api/file/"+exe;

        String fileName = photoUrl.substring(photoUrl.lastIndexOf("/"));

        List<String> list = new ArrayList<String>();
        BufferedReader bfr  = null;
        try {
            if(!downloadFolder.endsWith("/")){
                downloadFolder += "/";
            }
            //下载文件
            File file = saveUrlAs(photoUrl, downloadFolder+fileName,"GET");
            String str = "";
            bfr = new BufferedReader(new FileReader(file));
            while((str = bfr.readLine()) != null){
                list.add(str);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                bfr.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return list;

    }

    private File saveUrlAs(String url, String filePath, String method) throws Exception{
        log.info("下载文件！");
        File file=new File(filePath);
        //判断文件是否存在
        if (!file.exists()){
            //如果文件不存在，则创建新的的文件夹
            file.createNewFile();
        }
        FileOutputStream fileOut = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try{
            // 建立链接
            URL httpUrl=new URL(url);
            conn=(HttpURLConnection) httpUrl.openConnection();
            //以get方式提交表单，默认get方式
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // post方式不能使用缓存
            conn.setUseCaches(false);
            //连接指定的资源
            conn.connect();
            //获取网络输入流
            inputStream=conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            //判断文件的保存路径后面是否以/结尾

            fileOut = new FileOutputStream(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

            BufferedOutputStream bos = new BufferedOutputStream(fileOut);
            byte[] buf = new byte[4096];
            int length = bis.read(buf);
//            保存文件
            while(length != -1){
                bos.write(buf, 0, length);
                length = bis.read(buf);
            }
            bos.close();
            bis.close();
            conn.disconnect();
        } catch (Exception e){
            // e.printStackTrace();
            System.out.println("DownloadFile.java抛出异常！！"+e.getMessage());
        }
        return file;
    }
}
