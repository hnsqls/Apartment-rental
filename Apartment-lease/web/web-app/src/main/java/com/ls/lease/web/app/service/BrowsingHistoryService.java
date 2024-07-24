package com.ls.lease.web.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.lease.model.entity.BrowsingHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ls.lease.web.app.vo.history.HistoryItemVo;
import org.springframework.scheduling.annotation.Async;

/**
* @author liubo
* @description 针对表【browsing_history(浏览历史)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface BrowsingHistoryService extends IService<BrowsingHistory> {
    IPage<HistoryItemVo> pageItem(Page<HistoryItemVo> page, Long id);

    /**
     * 保存浏览信息
     * @param userId
     * @param id
     */
    @Async
    void saveHistory(Long userId, Long id);
}
