package com.ls.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.lease.model.entity.BrowsingHistory;
import com.ls.lease.web.app.mapper.BrowsingHistoryMapper;
import com.ls.lease.web.app.service.BrowsingHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.lease.web.app.vo.history.HistoryItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【browsing_history(浏览历史)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory>
        implements BrowsingHistoryService {

    @Autowired
    private BrowsingHistoryMapper browsingHistoryMapper;
    @Override
    public IPage<HistoryItemVo> pageItem(Page<HistoryItemVo> page, Long id) {
        return browsingHistoryMapper.pageItem(page,id);
    }
}