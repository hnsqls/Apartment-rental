package com.ls.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.lease.model.entity.*;
import com.ls.lease.model.enums.ItemType;
import com.ls.lease.web.admin.mapper.*;
import com.ls.lease.web.admin.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.ls.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.ls.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.ls.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.ls.lease.web.admin.vo.fee.FeeValueVo;
import com.ls.lease.web.admin.vo.graph.GraphVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private ApartmentFacilityService apartmentFacilityService;
    @Autowired
    private ApartmentLabelService apartmentLabelService;
    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;
    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private ApartmentInfoMapper mapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private ApartmentFacilityMapper facilityMapper;

    @Autowired
    private  FeeValueMapper feeValueMapper;

    /**
     * 保存或更新公寓信息
     * @param apartmentSubmitVo
     */
    @Override
    public void saveOrUpdateapart(ApartmentSubmitVo apartmentSubmitVo) {
        //手动处理其他信息即Vo新增的信息facilityInfoIds,labelIds,feeValueIds,graphVoList
        //保存操作直接保存，更新操作，所以先删除公寓所对应的信息，在保存参数的信息。
        Boolean is_update = apartmentSubmitVo.getId() != null;
        //调用父方法保存apartment基本信息
        super.saveOrUpdate(apartmentSubmitVo);
        if(is_update){
            //删除 对应的信息
            //删除修改公寓所公寓配套信息
            LambdaQueryWrapper<ApartmentFacility> apartmentFacilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<ApartmentFacility> eq = apartmentFacilityLambdaQueryWrapper.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId());
            apartmentFacilityService.remove(eq);
            //删除 修改公寓所对应公寓标签信息
            LambdaQueryWrapper<ApartmentLabel> apartmentLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apartmentLabelLambdaQueryWrapper.eq(ApartmentLabel::getApartmentId,apartmentSubmitVo.getId());
            apartmentLabelService.remove(apartmentLabelLambdaQueryWrapper);
            //删除公寓杂费信息
            LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apartmentFeeValueLambdaQueryWrapper.eq(ApartmentFeeValue::getApartmentId,apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(apartmentFeeValueLambdaQueryWrapper);

            //删除图片信息
            LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemId,apartmentSubmitVo.getId())
                    .eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphInfoService.remove(graphInfoLambdaQueryWrapper);
        }
        //无论是保存还是更细操作，都要保存

        /**
         * 保存公寓配套信息
         */
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)){
            //savabatch要求ApartmentFacility 泛型参数  Long -》ApartmentFacility
            ArrayList<ApartmentFacility> facilityArrayList = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                ApartmentFacility apartmentFacility = new ApartmentFacility();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(facilityInfoId);
                facilityArrayList.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(facilityArrayList);
        }


        /**
         * 保存图片信息
         */
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
            // graphInfoService.saveBatch(); 要求参数的集合泛型是<GraphInfo>,但是图片的信息是GraphVo
            //类型转换为info
        if(!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> graphInfos = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfo.setName(graphVo.getName());
                graphInfos.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfos);
        }

        /**
         * 保存标签信息
         */
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)){
            ArrayList<ApartmentLabel> labelArrayList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                labelArrayList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(labelArrayList);

        }
        /**
         * 保存公寓杂费
         */
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)){
            ArrayList<ApartmentFeeValue> feeValueList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                feeValueList.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(feeValueList);
        }


    }

    /**
     * 根据条件分页查询
     * @param apartmentItemVoPage
     * @param queryVo
     * @return
     */
    @Override
    public IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> apartmentItemVoPage, ApartmentQueryVo queryVo) {

        return  mapper.pageItem(apartmentItemVoPage,queryVo);
    }

    /**
     * 根据id查询公寓详细信息
     * @param id
     * @return ApartmentDetailVo
     */
    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        //查询公寓信息
        ApartmentInfo apartmentInfo = this.getById(id);
        if (apartmentInfo == null){
            return null;
        }

        //查询图片列表
            //查公寓id和公寓类型的图片
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemAndId(ItemType.APARTMENT,id);

        //查询标签列表
            //查询公寓id对应的标签列表 得到标签id列表，在根据得到的标签列表id，查标签信息
        List<LabelInfo>  labelInfoList= labelInfoMapper.selectListById(id);

        //查询配套列表
            //根据公寓id查到配套id集合，根基配套id集合，查到配套的名字
        List<FacilityInfo> facilityInfoList = facilityMapper.selectListById(id);

        //查询杂费列表
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListById(id);


        //组装结果
            //可以创建一个要求返回的对象，然后每个属性set。但是麻烦
            //spring 提供了工具类 BeanUtils import org.springframework.beans.BeanUtils;
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        //第一个是原对象，第二个参数是目标对象，只要属性名一样就可以转换
        BeanUtils.copyProperties(apartmentInfo,apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);

        return apartmentDetailVo;

//        return  mapper.getDetailById();
    }

}




