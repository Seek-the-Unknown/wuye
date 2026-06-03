package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsVisitorMapper;
import com.ruoyi.property.domain.PmsVisitor;
import com.ruoyi.property.service.IPmsVisitorService;

@Service
public class PmsVisitorServiceImpl implements IPmsVisitorService {
    @Autowired
    private PmsVisitorMapper pmsVisitorMapper;

    @Override
    public PmsVisitor selectPmsVisitorByVisitorId(Long visitorId) {
        return pmsVisitorMapper.selectPmsVisitorByVisitorId(visitorId);
    }

    @Override
    public List<PmsVisitor> selectPmsVisitorList(PmsVisitor pmsVisitor) {
        return pmsVisitorMapper.selectPmsVisitorList(pmsVisitor);
    }

    @Override
    public int insertPmsVisitor(PmsVisitor pmsVisitor) {
        pmsVisitor.setCreateTime(DateUtils.getNowDate());
        return pmsVisitorMapper.insertPmsVisitor(pmsVisitor);
    }

    @Override
    public int updatePmsVisitor(PmsVisitor pmsVisitor) {
        pmsVisitor.setUpdateTime(DateUtils.getNowDate());
        return pmsVisitorMapper.updatePmsVisitor(pmsVisitor);
    }

    @Override
    public int deletePmsVisitorByVisitorIds(Long[] visitorIds) {
        return pmsVisitorMapper.deletePmsVisitorByVisitorIds(visitorIds);
    }

    @Override
    public int deletePmsVisitorByVisitorId(Long visitorId) {
        return pmsVisitorMapper.deletePmsVisitorByVisitorId(visitorId);
    }
}
