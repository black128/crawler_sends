package com.bjksrs.crawler_sends.mapper;

import com.bjksrs.crawler_sends.pojo.UpBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UpMapper {
    List<UpBean> getData(String createTime);
}
