package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsNotice;

/**
 * 社区公告Service接口
 * 
 * @author ruoyi
 */
public interface IPmsNoticeService {
    /**
     * 查询社区公告
     * 
     * @param noticeId 社区公告主键
     * @return 社区公告
     */
    PmsNotice selectPmsNoticeByNoticeId(Long noticeId);

    /**
     * 查询社区公告列表
     * 
     * @param pmsNotice 社区公告
     * @return 社区公告集合
     */
    List<PmsNotice> selectPmsNoticeList(PmsNotice pmsNotice);

    /**
     * 新增社区公告
     * 
     * @param pmsNotice 社区公告
     * @return 结果
     */
    int insertPmsNotice(PmsNotice pmsNotice);

    /**
     * 修改社区公告
     * 
     * @param pmsNotice 社区公告
     * @return 结果
     */
    int updatePmsNotice(PmsNotice pmsNotice);

    /**
     * 批量删除社区公告
     * 
     * @param noticeIds 需要删除的社区公告主键集合
     * @return 结果
     */
    int deletePmsNoticeByNoticeIds(Long[] noticeIds);

    /**
     * 删除社区公告信息
     * 
     * @param noticeId 社区公告主键
     * @return 结果
     */
    int deletePmsNoticeByNoticeId(Long noticeId);
}
