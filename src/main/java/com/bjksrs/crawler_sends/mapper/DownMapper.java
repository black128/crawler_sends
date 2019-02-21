package com.bjksrs.crawler_sends.mapper;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DownMapper {
    /**
     * 插入seed_ic表
     * @param list
     */
    void insertSeedIc(List<String> list);

    /**
     * 插入seed_annual表
     * @param list
     */
    void insertSeedAnnual(List<String> list);
}
