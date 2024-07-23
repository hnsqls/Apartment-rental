package com.ls.lease.web.app.service.impl;

import com.ls.lease.model.entity.ApartmentInfo;
import com.ls.lease.model.entity.LabelInfo;
import com.ls.lease.model.enums.ItemType;
import com.ls.lease.web.app.mapper.ApartmentInfoMapper;
import com.ls.lease.web.app.mapper.GraphInfoMapper;
import com.ls.lease.web.app.mapper.LabelInfoMapper;
import com.ls.lease.web.app.mapper.RoomInfoMapper;
import com.ls.lease.web.app.service.ApartmentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.lease.web.app.vo.apartment.ApartmentItemVo;
import com.ls.lease.web.app.vo.graph.GraphVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;


    @Override
    public ApartmentItemVo selectApartmentItemVoById(Long id) {

        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);

        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);

        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);

        BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(id);

        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentItemVo);

        apartmentItemVo.setGraphVoList(graphVoList);
        apartmentItemVo.setLabelInfoList(labelInfoList);
        apartmentItemVo.setMinRent(minRent);
        return apartmentItemVo;

    }
}




