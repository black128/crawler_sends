package com.bjksrs.crawler_sends.service;

import com.bjksrs.crawler_sends.pojo.UpBean;

import java.util.List;

public interface UpService {
    List<UpBean> getData(String createTime);
}
