package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsNotice;

/**
 * 社区公告管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsNoticeMapper {
    /**
     * 查询社区公告管理
     * 
     * @param noticeId 社区公告管理主键
     * @return 社区公告管理
     */
    public PmsNotice selectPmsNoticeByNoticeId(Long noticeId);

    /**
     * 查询社区公告管理列表
     * 
     * @param pmsNotice 社区公告管理
     * @return 社区公告管理集合
     */
    public List<PmsNotice> selectPmsNoticeList(PmsNotice pmsNotice);

    /**
     * 新增社区公告管理
     * 
     * @param pmsNotice 社区公告管理
     * @return 结果
     */
    public int insertPmsNotice(PmsNotice pmsNotice);

    /**
     * 修改社区公告管理
     * 
     * @param pmsNotice 社区公告管理
     * @return 结果
     */
    public int updatePmsNotice(PmsNotice pmsNotice);

    /**
     * 删除社区公告管理
     * 
     * @param noticeId 社区公告管理主键
     * @return 结果
     */
    public int deletePmsNoticeByNoticeId(Long noticeId);

    /**
     * 批量删除社区公告管理
     * 
     * @param noticeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsNoticeByNoticeIds(Long[] noticeIds);
}
