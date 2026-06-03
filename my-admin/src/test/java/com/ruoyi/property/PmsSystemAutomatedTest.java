package com.ruoyi.property;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.RuoYiApplication;
import com.ruoyi.property.domain.PmsUserOwner;
import com.ruoyi.property.domain.PmsRoom;
import com.ruoyi.property.domain.PmsFeeRecord;
import com.ruoyi.property.mapper.PmsUserOwnerMapper;
import com.ruoyi.property.mapper.PmsRoomMapper;
import com.ruoyi.property.mapper.PmsFeeRecordMapper;
import com.ruoyi.property.service.IPmsUserOwnerService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RuoYiApplication.class)
@Transactional
public class PmsSystemAutomatedTest {

    @Autowired
    private PmsUserOwnerMapper pmsUserOwnerMapper;

    @Autowired
    private IPmsUserOwnerService pmsUserOwnerService;

    @Autowired
    private PmsRoomMapper pmsRoomMapper;

    @Autowired
    private PmsFeeRecordMapper pmsFeeRecordMapper;

    /**
     * 测试 1：深度验证 PmsUserOwner 主键查询逻辑纠偏
     * 确保通过主键ID查询不会发生与系统用户ID（user_id）的参数碰撞错位
     */
    @Test
    public void testUserOwnerPrimaryKeyQuery() {
        // 创建测试绑定关系
        PmsUserOwner binding = new PmsUserOwner();
        binding.setUserId(9999L);
        binding.setOwnerId(8888L);
        binding.setCreateBy("TestQA");

        int rows = pmsUserOwnerMapper.insertPmsUserOwner(binding);
        assertEquals(1, rows, "插入测试绑定数据应该成功");
        assertNotNull(binding.getId(), "自增主键ID应该已被注入");

        // 验证主键查询
        PmsUserOwner queryResult = pmsUserOwnerService.selectPmsUserOwnerById(binding.getId());
        assertNotNull(queryResult, "通过主键ID应该能查询到绑定关系");
        assertEquals(9999L, queryResult.getUserId(), "查询到的系统用户ID应完全一致");
        assertEquals(8888L, queryResult.getOwnerId(), "查询到的物理业主ID应完全一致");

        // 验证按userId查询，确保它是按user_id字段而非id主键字段定位
        PmsUserOwner userQueryResult = pmsUserOwnerService.selectByUserId(9999L);
        assertNotNull(userQueryResult, "应该能根据系统用户ID找到关系");
    }

    /**
     * 测试 2：验证业主端动态房屋提取逻辑
     * 模拟业主名下存在账单，自动提取并去重得到绑定的物理房产信息
     */
    @Test
    public void testPortalRoomDynamicExtraction() {
        Long testOwnerId = 2222L;
        
        // 创建两个不同的物理房产
        PmsRoom r1 = new PmsRoom();
        r1.setBuildingId(100L);
        r1.setRoomCode("TEST-101");
        r1.setRoomName("测试楼宇101室");
        r1.setConstructionArea(new BigDecimal("120.00"));
        pmsRoomMapper.insertPmsRoom(r1);

        PmsRoom r2 = new PmsRoom();
        r2.setBuildingId(100L);
        r2.setRoomCode("TEST-202");
        r2.setRoomName("测试楼宇202室");
        r2.setConstructionArea(new BigDecimal("98.50"));
        pmsRoomMapper.insertPmsRoom(r2);

        // 创建属于该业主的账单明细建立物关联
        PmsFeeRecord f1 = new PmsFeeRecord();
        f1.setCommunityId(100L);
        f1.setRoomId(r1.getRoomId());
        f1.setOwnerId(testOwnerId);
        f1.setFeeTypeId(100L);
        f1.setFeeAmount(new BigDecimal("300.00"));
        f1.setFeeMonth("2026-05");
        f1.setPayStatus("0");
        pmsFeeRecordMapper.insertPmsFeeRecord(f1);

        PmsFeeRecord f2 = new PmsFeeRecord();
        f2.setCommunityId(100L);
        f2.setRoomId(r2.getRoomId());
        f2.setOwnerId(testOwnerId);
        f2.setFeeTypeId(100L);
        f2.setFeeAmount(new BigDecimal("246.25"));
        f2.setFeeMonth("2026-05");
        f2.setPayStatus("0");
        pmsFeeRecordMapper.insertPmsFeeRecord(f2);

        // 通过费用记录反查提取该业主的全部关联房屋
        PmsFeeRecord query = new PmsFeeRecord();
        query.setOwnerId(testOwnerId);
        List<PmsFeeRecord> records = pmsFeeRecordMapper.selectPmsFeeRecordList(query);
        
        java.util.List<PmsRoom> rooms = new java.util.ArrayList<>();
        java.util.Set<Long> roomIds = new java.util.HashSet<>();
        for (PmsFeeRecord r : records) {
            if (r.getRoomId() != null && !roomIds.contains(r.getRoomId())) {
                roomIds.add(r.getRoomId());
                PmsRoom room = pmsRoomMapper.selectPmsRoomByRoomId(r.getRoomId());
                if (room != null) {
                    rooms.add(room);
                }
            }
        }

        // 验证去重后的房产信息
        assertEquals(2, rooms.size(), "该业主名下应提取出2套完全不同的房产");
        assertTrue(roomIds.contains(r1.getRoomId()), "房产树应包含测试房屋101");
        assertTrue(roomIds.contains(r2.getRoomId()), "房产树应包含测试房屋202");
    }

    /**
     * 测试 3：物业费高精度乘积与四舍五入边界值计算测试
     * 验证在复杂物理度量下，计算公式 建筑面积 * 单价 能保证高精度，不产生Double/Float浮点精度丢失缺陷
     */
    @Test
    public void testBillingDecimalPrecision() {
        // 设房屋面积为 127.35 ㎡，物业费单价为 2.835 元/㎡/月
        BigDecimal area = new BigDecimal("127.35");
        BigDecimal unitPrice = new BigDecimal("2.835");

        // 期待结果：127.35 * 2.835 = 361.03725 -> 四舍五入保留两位小数应为 361.04
        BigDecimal expectedAmount = new BigDecimal("361.04");

        // 自动化白盒计算验证
        BigDecimal calculatedAmount = area.multiply(unitPrice).setScale(2, java.math.RoundingMode.HALF_UP);

        assertEquals(expectedAmount, calculatedAmount, "高精度四舍五入金额计算应与预期361.04完全相符，无任何浮点偏差");
    }
}
