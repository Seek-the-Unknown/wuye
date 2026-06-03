package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsNoticeMapper;
import com.ruoyi.property.domain.PmsNotice;
import com.ruoyi.property.service.IPmsNoticeService;

@Service
public class PmsNoticeServiceImpl implements IPmsNoticeService {
    @Autowired
    private PmsNoticeMapper pmsNoticeMapper;

    @Override
    public PmsNotice selectPmsNoticeByNoticeId(Long noticeId) {
        return pmsNoticeMapper.selectPmsNoticeByNoticeId(noticeId);
    }

    @Override
    public List<PmsNotice> selectPmsNoticeList(PmsNotice pmsNotice) {
        return pmsNoticeMapper.selectPmsNoticeList(pmsNotice);
    }

    @Override
    public int insertPmsNotice(PmsNotice pmsNotice) {
        pmsNotice.setCreateTime(DateUtils.getNowDate());
        return pmsNoticeMapper.insertPmsNotice(pmsNotice);
    }

    @Override
    public int updatePmsNotice(PmsNotice pmsNotice) {
        pmsNotice.setUpdateTime(DateUtils.getNowDate());
        return pmsNoticeMapper.updatePmsNotice(pmsNotice);
    }

    @Override
    public int deletePmsNoticeByNoticeIds(Long[] noticeIds) {
        return pmsNoticeMapper.deletePmsNoticeByNoticeIds(noticeIds);
    }

    @Override
    public int deletePmsNoticeByNoticeId(Long noticeId) {
        return pmsNoticeMapper.deletePmsNoticeByNoticeId(noticeId);
    }
}
