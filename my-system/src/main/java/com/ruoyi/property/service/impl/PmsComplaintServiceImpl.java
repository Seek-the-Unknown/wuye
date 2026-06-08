package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsComplaintMapper;
import com.ruoyi.property.domain.PmsComplaint;
import com.ruoyi.property.service.IPmsComplaintService;

/**
 * 投诉建议管理Service业务层实现类
 * 负责处理与投诉和建议相关的业务逻辑
 * 
 * @author ruoyi
 */
@Service
public class PmsComplaintServiceImpl implements IPmsComplaintService {
    
    /** 投诉建议Mapper数据访问接口 */
    @Autowired
    private PmsComplaintMapper pmsComplaintMapper;

    /**
     * 根据投诉建议ID查询详细信息
     * 
     * @param complaintId 投诉建议主键ID
     * @return 投诉建议实体对象
     */
    @Override
    public PmsComplaint selectPmsComplaintByComplaintId(Long complaintId) {
        // 调用Mapper接口根据主键查询单条投诉建议记录
        return pmsComplaintMapper.selectPmsComplaintByComplaintId(complaintId);
    }

    /**
     * 查询符合条件的投诉建议列表数据
     * 
     * @param pmsComplaint 包含查询条件的投诉建议实体
     * @return 投诉建议对象集合
     */
    @Override
    public List<PmsComplaint> selectPmsComplaintList(PmsComplaint pmsComplaint) {
        // 调用Mapper接口根据条件查询投诉建议列表
        return pmsComplaintMapper.selectPmsComplaintList(pmsComplaint);
    }

    /**
     * 新增投诉建议记录
     * 
     * @param pmsComplaint 待新增的投诉建议实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsComplaint(PmsComplaint pmsComplaint) {
        // 自动设置记录的创建时间为当前时间
        pmsComplaint.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入投诉建议数据操作
        return pmsComplaintMapper.insertPmsComplaint(pmsComplaint);
    }

    /**
     * 修改投诉建议记录
     * 
     * @param pmsComplaint 包含修改信息的投诉建议实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsComplaint(PmsComplaint pmsComplaint) {
        // 自动设置记录的更新时间为当前时间
        pmsComplaint.setUpdateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行更新投诉建议数据操作
        return pmsComplaintMapper.updatePmsComplaint(pmsComplaint);
    }

    /**
     * 根据投诉建议ID单条删除信息
     * 
     * @param complaintId 待删除的投诉建议主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsComplaintByComplaintId(Long complaintId) {
        // 调用Mapper接口根据主键删除单条投诉建议记录
        return pmsComplaintMapper.deletePmsComplaintByComplaintId(complaintId);
    }

    /**
     * 批量删除投诉建议记录
     * 
     * @param complaintIds 需要批量删除的投诉建议主键ID数组
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsComplaintByComplaintIds(Long[] complaintIds) {
        // 调用Mapper接口根据主键数组批量删除投诉建议记录
        return pmsComplaintMapper.deletePmsComplaintByComplaintIds(complaintIds);
    }
}
