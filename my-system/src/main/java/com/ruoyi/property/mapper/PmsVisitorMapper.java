package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsVisitor;

/**
 * 访客管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsVisitorMapper {
    /**
     * 查询访客管理
     * 
     * @param visitorId 访客管理主键
     * @return 访客管理
     */
    public PmsVisitor selectPmsVisitorByVisitorId(Long visitorId);

    /**
     * 查询访客管理列表
     * 
     * @param pmsVisitor 访客管理
     * @return 访客管理集合
     */
    public List<PmsVisitor> selectPmsVisitorList(PmsVisitor pmsVisitor);

    /**
     * 新增访客管理
     * 
     * @param pmsVisitor 访客管理
     * @return 结果
     */
    public int insertPmsVisitor(PmsVisitor pmsVisitor);

    /**
     * 修改访客管理
     * 
     * @param pmsVisitor 访客管理
     * @return 结果
     */
    public int updatePmsVisitor(PmsVisitor pmsVisitor);

    /**
     * 删除访客管理
     * 
     * @param visitorId 访客管理主键
     * @return 结果
     */
    public int deletePmsVisitorByVisitorId(Long visitorId);

    /**
     * 批量删除访客管理
     * 
     * @param visitorIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsVisitorByVisitorIds(Long[] visitorIds);
}
