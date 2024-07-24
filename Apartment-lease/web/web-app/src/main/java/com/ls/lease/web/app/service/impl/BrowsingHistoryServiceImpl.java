package com.ls.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.lease.common.login.LoginUserHolder;
import com.ls.lease.model.entity.BrowsingHistory;
import com.ls.lease.web.app.mapper.BrowsingHistoryMapper;
import com.ls.lease.web.app.service.BrowsingHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.lease.web.app.vo.history.HistoryItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    /**
     * 保存浏览房间历史记录
     * @param userId
     * @param id  房间id
     */
    @Override
    public void saveHistory(Long userId, Long id) {
//        System.out.println("保存信息------------------------------------------------------");
        System.out.println(LoginUserHolder.getLoginUser()+"用户i想你想");
        LambdaQueryWrapper<BrowsingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowsingHistory::getUserId,userId)
                         .eq(BrowsingHistory::getRoomId,id);
        BrowsingHistory browsingHistory = browsingHistoryMapper.selectOne(queryWrapper);
        if (browsingHistory != null){
            //数据库存在记录，就更新时间
            browsingHistory.setBrowseTime(new Date());
            browsingHistoryMapper.updateById(browsingHistory);

        }else {
            //数据库不存在记录，就插入
            BrowsingHistory browsingHistory1 = new BrowsingHistory();
            browsingHistory1.setBrowseTime(new Date());
            browsingHistory1.setUserId(userId);
            browsingHistory1.setRoomId(id);
            browsingHistoryMapper.insert(browsingHistory1);

        }

    }
}