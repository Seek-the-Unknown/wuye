package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsNoticeMapper;
import com.ruoyi.property.domain.PmsNotice;
import com.ruoyi.property.service.IPmsNoticeService;

/**
 * 社区公告Service业务层实现类
 * 负责处理社区公告管理的业务逻辑操作
 * 
 * @author ruoyi
 */
@Service
public class PmsNoticeServiceImpl implements IPmsNoticeService {
    
    /** 社区公告Mapper数据访问接口 */
    @Autowired
    private PmsNoticeMapper pmsNoticeMapper;

    /**
     * 根据公告ID查询社区公告详细信息
     * 
     * @param noticeId 社区公告主键ID
     * @return 社区公告实体对象
     */
    @Override
    public PmsNotice selectPmsNoticeByNoticeId(Long noticeId) {
        // 调用Mapper接口根据主键查询社区公告记录
        return pmsNoticeMapper.selectPmsNoticeByNoticeId(noticeId);
    }

    /**
     * 查询符合条件的社区公告列表数据
     * 
     * @param pmsNotice 包含查询条件的社区公告实体
     * @return 社区公告对象集合
     */
    @Override
    public List<PmsNotice> selectPmsNoticeList(PmsNotice pmsNotice) {
        // 调用Mapper接口根据条件查询社区公告列表
        return pmsNoticeMapper.selectPmsNoticeList(pmsNotice);
    }

    /**
     * 新增社区公告记录
     * 
     * @param pmsNotice 待新增的社区公告实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsNotice(PmsNotice pmsNotice) {
        // 自动设置记录的创建时间为当前时间
        pmsNotice.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入社区公告操作
        return pmsNoticeMapper.insertPmsNotice(pmsNotice);
    }

    /**
     * 修改社区公告记录
     * 
     * @param pmsNotice 包含修改信息的社区公告实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsNotice(PmsNotice pmsNotice) {
        // 自动设置记录的更新时间为当前时间
        pmsNotice.setUpdateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行更新社区公告操作
        return pmsNoticeMapper.updatePmsNotice(pmsNotice);
    }

    /**
     * 批量删除社区公告记录
     * 
     * @param noticeIds 需要批量删除的社区公告主键ID数组
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsNoticeByNoticeIds(Long[] noticeIds) {
        // 调用Mapper接口根据主键数组批量删除社区公告
        return pmsNoticeMapper.deletePmsNoticeByNoticeIds(noticeIds);
    }

    /**
     * 根据公告ID单条删除社区公告信息
     * 
     * @param noticeId 待删除的社区公告主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsNoticeByNoticeId(Long noticeId) {
        // 调用Mapper接口根据主键删除单条社区公告记录
        return pmsNoticeMapper.deletePmsNoticeByNoticeId(noticeId);
    }
}
