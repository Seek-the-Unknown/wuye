package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsNotice;

public interface IPmsNoticeService {
    PmsNotice selectPmsNoticeByNoticeId(Long noticeId);
    List<PmsNotice> selectPmsNoticeList(PmsNotice pmsNotice);
    int insertPmsNotice(PmsNotice pmsNotice);
    int updatePmsNotice(PmsNotice pmsNotice);
    int deletePmsNoticeByNoticeIds(Long[] noticeIds);
    int deletePmsNoticeByNoticeId(Long noticeId);
}
