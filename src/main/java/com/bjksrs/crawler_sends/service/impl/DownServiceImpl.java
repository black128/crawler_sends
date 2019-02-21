package com.bjksrs.crawler_sends.service.impl;

import com.bjksrs.crawler_sends.mapper.DownMapper;
import com.bjksrs.crawler_sends.service.DownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DownServiceImpl implements DownService {

    @Autowired
    private DownMapper downMapper;

    @Override
    public void insert(List<String> list) {
        if(list.size() <= 5000){
            downMapper.insertSeedIc(list);
            downMapper.insertSeedAnnual(list);
            return;
        }

        // 每隔多少条拆分list（拆分基数）
        int maxLimit = 5000;
        int size = list.size();

        // 拆分次数times
        int times = (size % maxLimit == 0) ? size / maxLimit : size / maxLimit + 1;
        System.out.println("共拆分" + times + "次");

        for (int i = 0; i < times; i++) {
            // 最后一次拆分，拆分的条数为list长度
            maxLimit = maxLimit >= list.size() ? list.size() : maxLimit;
            List<String> listPage = list.subList(0, maxLimit);
            downMapper.insertSeedIc(listPage);
            downMapper.insertSeedAnnual(listPage);
            // 每次取出来的list，去掉
            list.subList(0, maxLimit).clear();
        }
    }
}
