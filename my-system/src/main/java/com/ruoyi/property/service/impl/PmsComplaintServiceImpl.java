package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsComplaintMapper;
import com.ruoyi.property.domain.PmsComplaint;
import com.ruoyi.property.service.IPmsComplaintService;

@Service
public class PmsComplaintServiceImpl implements IPmsComplaintService {
    @Autowired
    private PmsComplaintMapper pmsComplaintMapper;

    @Override
    public PmsComplaint selectPmsComplaintByComplaintId(Long complaintId) {
        return pmsComplaintMapper.selectPmsComplaintByComplaintId(complaintId);
    }

    @Override
    public List<PmsComplaint> selectPmsComplaintList(PmsComplaint pmsComplaint) {
        return pmsComplaintMapper.selectPmsComplaintList(pmsComplaint);
    }

    @Override
    public int insertPmsComplaint(PmsComplaint pmsComplaint) {
        pmsComplaint.setCreateTime(DateUtils.getNowDate());
        return pmsComplaintMapper.insertPmsComplaint(pmsComplaint);
    }

    @Override
    public int updatePmsComplaint(PmsComplaint pmsComplaint) {
        pmsComplaint.setUpdateTime(DateUtils.getNowDate());
        return pmsComplaintMapper.updatePmsComplaint(pmsComplaint);
    }

    @Override
    public int deletePmsComplaintByComplaintId(Long complaintId) {
        return pmsComplaintMapper.deletePmsComplaintByComplaintId(complaintId);
    }

    @Override
    public int deletePmsComplaintByComplaintIds(Long[] complaintIds) {
        return pmsComplaintMapper.deletePmsComplaintByComplaintIds(complaintIds);
    }
}
