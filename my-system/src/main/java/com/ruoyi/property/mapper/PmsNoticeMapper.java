package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsNotice;

public interface PmsNoticeMapper {
    PmsNotice selectPmsNoticeByNoticeId(Long noticeId);
    List<PmsNotice> selectPmsNoticeList(PmsNotice pmsNotice);
    int insertPmsNotice(PmsNotice pmsNotice);
    int updatePmsNotice(PmsNotice pmsNotice);
    int deletePmsNoticeByNoticeId(Long noticeId);
    int deletePmsNoticeByNoticeIds(Long[] noticeIds);
}
