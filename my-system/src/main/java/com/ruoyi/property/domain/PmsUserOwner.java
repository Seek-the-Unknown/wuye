package com.ruoyi.property.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户-业主绑定关联对象 PmsUserOwner
 * 
 * 用于表示物业管理系统中系统用户（微信用户/App用户）与实际业主信息的绑定关系
 */
public class PmsUserOwner extends BaseEntity {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 关联主键ID */
    private Long id;

    /** 系统用户ID */
    private Long userId;

    /** 绑定的业主ID */
    private Long ownerId;

    /** 关联字段（展示用）：系统账号/用户名 */
    // 关联字段（展示用）
    private String userName;

    /** 关联字段（展示用）：业主姓名 */
    private String ownerName;

    /** 关联字段（展示用）：联系电话 */
    private String phone;

    /** 关联字段（展示用）：用户昵称 */
    private String nickName;

    /** 获取主键ID @return 主键ID */
    public Long getId() { return id; }
    
    /** 设置主键ID @param id 主键ID */
    public void setId(Long id) { this.id = id; }
    
    /** 获取用户ID @return 用户ID */
    public Long getUserId() { return userId; }
    
    /** 设置用户ID @param userId 用户ID */
    public void setUserId(Long userId) { this.userId = userId; }
    
    /** 获取业主ID @return 业主ID */
    public Long getOwnerId() { return ownerId; }
    
    /** 设置业主ID @param ownerId 业主ID */
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    /** 获取用户名 @return 用户名 */
    public String getUserName() { return userName; }
    
    /** 设置用户名 @param userName 用户名 */
    public void setUserName(String userName) { this.userName = userName; }
    
    /** 获取业主姓名 @return 业主姓名 */
    public String getOwnerName() { return ownerName; }
    
    /** 设置业主姓名 @param ownerName 业主姓名 */
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    /** 获取联系电话 @return 联系电话 */
    public String getPhone() { return phone; }
    
    /** 设置联系电话 @param phone 联系电话 */
    public void setPhone(String phone) { this.phone = phone; }
    
    /** 获取用户昵称 @return 用户昵称 */
    public String getNickName() { return nickName; }
    
    /** 设置用户昵称 @param nickName 用户昵称 */
    public void setNickName(String nickName) { this.nickName = nickName; }
}
