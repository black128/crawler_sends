package com.bjksrs.crawler_sends.service.impl;

import com.bjksrs.crawler_sends.mapper.UpMapper;
import com.bjksrs.crawler_sends.pojo.UpBean;
import com.bjksrs.crawler_sends.service.UpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpServiceImpl implements UpService {

    @Autowired
    private UpMapper upMapper;

    @Override
    public List<UpBean> getData(String createTime) {
        return upMapper.getData(createTime);
    }
}
