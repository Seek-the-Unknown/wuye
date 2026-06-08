package com.ruoyi.property.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.property.mapper.PmsVisitorMapper;
import com.ruoyi.property.domain.PmsVisitor;
import com.ruoyi.property.service.IPmsVisitorService;

/**
 * 访客管理Service业务层实现类
 * 负责处理社区访客信息管理的业务逻辑操作
 * 
 * @author ruoyi
 */
@Service
public class PmsVisitorServiceImpl implements IPmsVisitorService {
    
    /** 访客管理Mapper数据访问接口 */
    @Autowired
    private PmsVisitorMapper pmsVisitorMapper;

    /**
     * 根据访客ID查询访客详细信息
     * 
     * @param visitorId 访客主键ID
     * @return 访客管理信息实体对象
     */
    @Override
    public PmsVisitor selectPmsVisitorByVisitorId(Long visitorId) {
        // 调用Mapper接口根据主键查询单条访客记录
        return pmsVisitorMapper.selectPmsVisitorByVisitorId(visitorId);
    }

    /**
     * 查询符合条件的访客管理列表数据
     * 
     * @param pmsVisitor 包含查询条件的访客管理实体
     * @return 访客管理对象集合
     */
    @Override
    public List<PmsVisitor> selectPmsVisitorList(PmsVisitor pmsVisitor) {
        // 调用Mapper接口根据条件查询访客列表
        return pmsVisitorMapper.selectPmsVisitorList(pmsVisitor);
    }

    /**
     * 新增访客管理记录
     * 
     * @param pmsVisitor 待新增的访客管理实体对象
     * @return 受影响的行数（新增成功的记录数）
     */
    @Override
    public int insertPmsVisitor(PmsVisitor pmsVisitor) {
        // 自动设置记录的创建时间为当前时间
        pmsVisitor.setCreateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行插入访客数据操作
        return pmsVisitorMapper.insertPmsVisitor(pmsVisitor);
    }

    /**
     * 修改访客管理记录
     * 
     * @param pmsVisitor 包含修改信息的访客管理实体对象
     * @return 受影响的行数（更新成功的记录数）
     */
    @Override
    public int updatePmsVisitor(PmsVisitor pmsVisitor) {
        // 自动设置记录的更新时间为当前时间
        pmsVisitor.setUpdateTime(DateUtils.getNowDate());
        // 调用Mapper接口执行更新访客数据操作
        return pmsVisitorMapper.updatePmsVisitor(pmsVisitor);
    }

    /**
     * 批量删除访客管理记录
     * 
     * @param visitorIds 需要批量删除的访客主键ID数组
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsVisitorByVisitorIds(Long[] visitorIds) {
        // 调用Mapper接口根据主键数组批量删除访客记录
        return pmsVisitorMapper.deletePmsVisitorByVisitorIds(visitorIds);
    }

    /**
     * 根据访客ID单条删除访客管理信息
     * 
     * @param visitorId 待删除的访客主键ID
     * @return 受影响的行数（删除成功的记录数）
     */
    @Override
    public int deletePmsVisitorByVisitorId(Long visitorId) {
        // 调用Mapper接口根据主键删除单条访客记录
        return pmsVisitorMapper.deletePmsVisitorByVisitorId(visitorId);
    }
}
