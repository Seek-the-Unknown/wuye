package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsUserOwnerMapper;
import com.ruoyi.property.domain.PmsUserOwner;
import com.ruoyi.property.service.IPmsUserOwnerService;

@Service
public class PmsUserOwnerServiceImpl implements IPmsUserOwnerService {
    @Autowired
    private PmsUserOwnerMapper pmsUserOwnerMapper;

    @Override
    public PmsUserOwner selectByUserId(Long userId) {
        return pmsUserOwnerMapper.selectByUserId(userId);
    }

    @Override
    public PmsUserOwner selectPmsUserOwnerById(Long id) {
        return pmsUserOwnerMapper.selectPmsUserOwnerById(id);
    }

    @Override
    public List<PmsUserOwner> selectPmsUserOwnerList(PmsUserOwner query) {
        return pmsUserOwnerMapper.selectPmsUserOwnerList(query);
    }

    @Override
    public int insertPmsUserOwner(PmsUserOwner pmsUserOwner) {
        pmsUserOwner.setCreateTime(DateUtils.getNowDate());
        return pmsUserOwnerMapper.insertPmsUserOwner(pmsUserOwner);
    }

    @Override
    public int updatePmsUserOwner(PmsUserOwner pmsUserOwner) {
        return pmsUserOwnerMapper.updatePmsUserOwner(pmsUserOwner);
    }

    @Override
    public int deletePmsUserOwnerById(Long id) {
        return pmsUserOwnerMapper.deletePmsUserOwnerById(id);
    }
}
