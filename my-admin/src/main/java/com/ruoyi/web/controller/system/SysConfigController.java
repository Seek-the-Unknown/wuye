package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 参数配置 信息操作处理控制器
 * 负责系统参数配置（如系统名称、默认密码等）的增删改查、导出、缓存刷新等请求处理
 * 
 * @author ruoyi
 */
@RestController // 标识这是一个RESTful风格的控制器，所有方法的返回值会自动转为JSON格式并写入HTTP响应体中
@RequestMapping("/system/config") // 映射基础请求路径，该控制器下所有处理的方法都映射在 "/system/config" 下
public class SysConfigController extends BaseController
{
    /** 参数配置服务接口，处理参数配置相关的业务逻辑 */
    @Autowired // 自动装配，由Spring框架注入对应的服务层实现类
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     * 支持分页查询
     * 
     * @param config 包含前端传递的查询条件（如参数名称、参数键名、状态等）
     * @return 包含参数列表和分页信息的表格数据对象
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')") // 权限拦截：判断当前登录用户是否具备 'system:config:list' 权限
    @GetMapping("/list") // 处理GET请求，路径为 "/system/config/list"
    public TableDataInfo list(SysConfig config)
    {
        // 开启MyBatis分页功能，底层拦截器会自动获取请求中的pageNum和pageSize参数并拼装到SQL中
        startPage();
        // 调用服务层，根据条件查询对应的参数配置列表数据
        List<SysConfig> list = configService.selectConfigList(config);
        // 将查询出的列表封装为前端组件(如Element UI Table)所需的分页数据结构并返回
        return getDataTable(list);
    }

    /**
     * 导出参数配置列表到Excel
     * 
     * @param response HTTP响应对象，用于向客户端回写生成的Excel文件流
     * @param config 查询条件，用于筛选需要导出的参数配置数据
     */
    @Log(title = "参数管理", businessType = BusinessType.EXPORT) // 记录操作日志，模块名为"参数管理"，业务类型为"导出"
    @PreAuthorize("@ss.hasPermi('system:config:export')") // 权限校验，要求具有导出权限
    @PostMapping("/export") // 处理POST请求，路径为 "/system/config/export"
    public void export(HttpServletResponse response, SysConfig config)
    {
        // 1. 根据前端传来的查询条件，获取所有符合条件的参数配置数据列表
        List<SysConfig> list = configService.selectConfigList(config);
        // 2. 实例化自定义的Excel工具类，泛型指定为SysConfig实体类，以便解析该类上的@Excel注解
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        // 3. 调用工具类的导出方法，将获取到的数据(list)生成Excel工作簿，并写入到response响应流中供用户下载。
        //    "参数数据"为生成的Excel内部工作表(Sheet)的名字
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * 根据参数编号获取详细信息
     * 用于前端修改页面回显单条参数配置的数据
     * 
     * @param configId URL路径参数中的配置ID
     * @return 包含系统配置实体的AjaxResult对象
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')") // 权限校验，要求具有查询权限
    @GetMapping(value = "/{configId}") // 处理GET请求，RESTful风格，路径中带有动态参数 {configId}
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        // 调用服务层通过ID查询配置的详情，并将结果使用AjaxResult的success方法包装成标准返回格式
        return success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     * 通常用于前端或后台其他模块，通过唯一的键名(key)来获取系统的某一项具体配置值
     * 
     * @param configKey 路径中的参数键名
     * @return 包含查询到的参数配置值的AjaxResult对象
     */
    @GetMapping(value = "/configKey/{configKey}") // 处理GET请求，路径为 "/system/config/configKey/{configKey}"
    public AjaxResult getConfigKey(@PathVariable String configKey)
    {
        // 调用服务层，根据键名获取指定的参数配置实体或直接返回值，封装后返回给前端
        return success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     * 
     * @param config 前端提交的JSON格式数据，自动映射为SysConfig对象，并使用@Validated进行基础字段校验
     * @return 返回操作结果状态
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')") // 权限校验：新增权限
    @Log(title = "参数管理", businessType = BusinessType.INSERT) // 记录操作日志：新增
    @PostMapping // 处理POST请求，路径为 "/system/config"
    public AjaxResult add(@Validated @RequestBody SysConfig config)
    {
        // 1. 调用服务层校验配置的"键名"是否唯一，同一系统下不允许存在相同键名的配置
        if (!configService.checkConfigKeyUnique(config))
        {
            // 如果校验不通过，返回带有错误信息的Ajax结果
            return error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        // 2. 从当前上下文中获取登录账号名，设置为该条记录的创建人
        config.setCreateBy(getUsername());
        // 3. 执行新增操作，根据影响的行数(通常大于0表示成功)通过toAjax转为对应的响应结果
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     * 
     * @param config 前端传递的带有configId等修改信息的JSON数据对象
     * @return 返回修改操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')") // 权限校验：修改权限
    @Log(title = "参数管理", businessType = BusinessType.UPDATE) // 记录操作日志：修改
    @PutMapping // 处理PUT请求，路径为 "/system/config"
    public AjaxResult edit(@Validated @RequestBody SysConfig config)
    {
        // 1. 校验修改后的键名是否与系统中其他记录发生冲突
        if (!configService.checkConfigKeyUnique(config))
        {
            // 不唯一则返回错误提示
            return error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        // 2. 将当前登录用户设置为该记录的更新人
        config.setUpdateBy(getUsername());
        // 3. 执行更新操作，并返回结果
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     * 支持批量删除，即通过URL中逗号分隔的参数ID数组进行删除
     * 
     * @param configIds URL路径中包含的待删除的配置主键数组
     * @return 返回删除成功的结果
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')") // 权限校验：删除权限
    @Log(title = "参数管理", businessType = BusinessType.DELETE) // 记录操作日志：删除
    @DeleteMapping("/{configIds}") // 处理DELETE请求，路径为 "/system/config/{configIds}"
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        // 1. 调用服务层的批量删除方法，依据传入的ID数组从数据库中移除相应配置
        configService.deleteConfigByIds(configIds);
        // 2. 删除成功后返回通用成功结果
        return success();
    }

    /**
     * 刷新参数缓存
     * 系统参数常使用Redis进行缓存，如果直接修改数据库或出于某种原因需要同步，调用此方法重新加载缓存
     * 
     * @return 刷新成功状态
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')") // 权限校验：要求具有类似管理员的删除级别权限（依框架设计配置）
    @Log(title = "参数管理", businessType = BusinessType.CLEAN) // 记录操作日志：清空或清理业务类型
    @DeleteMapping("/refreshCache") // 处理DELETE请求，路径为 "/system/config/refreshCache"
    public AjaxResult refreshCache()
    {
        // 1. 调用服务层的重置缓存方法。该方法一般会先清空Redis中的旧参数缓存，然后重新从数据库加载所有参数至缓存
        configService.resetConfigCache();
        // 2. 刷新完毕后返回成功结果
        return success();
    }
}
