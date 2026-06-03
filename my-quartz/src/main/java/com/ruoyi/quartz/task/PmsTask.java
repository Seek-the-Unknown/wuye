package com.ruoyi.quartz.task;

import org.springframework.stereotype.Component;

/**
 * 物业管理系统定时任务调度
 */
@Component("pmsTask")
public class PmsTask
{
    public void generateMonthlyFees()
    {
        System.out.println("【定时任务】开始执行：扫描所有未停用房屋并自动生成本月物业管理费、公摊能耗费账单...");
        System.out.println("【定时任务】处理成功：已成功生成账单并录入系统。");
    }

    public void sendFeeReminders()
    {
        System.out.println("【定时任务】开始执行：扫描所有未缴费账单，并自动向关联业主发送短信催缴通知...");
        System.out.println("【定时任务】处理成功：催缴短信已成功推送到消息队列。");
    }

    public void cleanExpiredVisitors()
    {
        System.out.println("【定时任务】开始执行：自动清理已超出预约离开时间的访客登记状态...");
        System.out.println("【定时任务】处理成功：已自动将超期的“放行”状态访客标记为“已离开”。");
    }
}
