package com.ruoyi.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.biz.domain.ArchiveEntranceList;
import com.ruoyi.biz.service.IArchiveEntranceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 楼门信息Controller
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-02-27
 */
@RestController
@RequestMapping("/biz/entrance")
public class ArchiveEntranceListController extends BaseController {
    @Autowired
    private IArchiveEntranceListService archiveEntranceListService;

    /**
     * 查询楼门信息列表
     */
    @PreAuthorize("@ss.hasPermi('biz:entrance:list')")
    @GetMapping("/list")
    public TableDataInfo list(HttpServletRequest request, ArchiveEntranceList archiveEntranceList) {
        QueryWrapper<ArchiveEntranceList> queryWrapper = new QueryWrapper<>();

        // 手动构建查询条件，避免驼峰命名转换问题
        // 获取前端传递的参数（兼容 Like 后缀的参数名）
        // 注意：所有列名需加上表别名 ae. 以避免多表关联时的列名歧义
        String entranceCode = request.getParameter("entranceCodeLike");
        if (entranceCode != null && !entranceCode.isEmpty()) {
            queryWrapper.like("ae.entrance_code", entranceCode);
        }
        String entranceName = request.getParameter("entranceNameLike");
        if (entranceName != null && !entranceName.isEmpty()) {
            queryWrapper.like("ae.entrance_name", entranceName);
        }
        String villageId = request.getParameter("villageId");
        if (villageId != null && !villageId.isEmpty()) {
            queryWrapper.eq("ae.village_id", villageId);
        }
        String buildingId = request.getParameter("buildingId");
        if (buildingId != null && !buildingId.isEmpty()) {
            queryWrapper.eq("ae.building_id", buildingId);
        }
        String houseCount = request.getParameter("houseCountLike");
        if (houseCount != null && !houseCount.isEmpty()) {
            queryWrapper.like("ae.house_count", houseCount);
        }
        String buildingArea = request.getParameter("buildingAreaLike");
        if (buildingArea != null && !buildingArea.isEmpty()) {
            queryWrapper.like("ae.building_area", buildingArea);
        }
        String billingArea = request.getParameter("billingAreaLike");
        if (billingArea != null && !billingArea.isEmpty()) {
            queryWrapper.like("ae.billing_area", billingArea);
        }

        Page<ArchiveEntranceList> page = getPage();
        Page<ArchiveEntranceList> result = archiveEntranceListService
                .pageArchiveEntranceList(page, queryWrapper);
        return getDataTableByPage(result);
    }

    /**
     * 导出楼门信息列表
     */
    @PreAuthorize("@ss.hasPermi('biz:entrance:export')")
    @Log(title = "楼门信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HttpServletRequest request,
            ArchiveEntranceList archiveEntranceList) {
        QueryWrapper<ArchiveEntranceList> queryWrapper = new QueryWrapper<>();

        // 手动构建查询条件，避免驼峰命名转换问题
        // 获取前端传递的参数（兼容 Like 后缀的参数名）
        // 注意：所有列名需加上表别名 ae. 以避免多表关联时的列名歧义
        String entranceCode = request.getParameter("entranceCodeLike");
        if (entranceCode != null && !entranceCode.isEmpty()) {
            queryWrapper.like("ae.entrance_code", entranceCode);
        }
        String entranceName = request.getParameter("entranceNameLike");
        if (entranceName != null && !entranceName.isEmpty()) {
            queryWrapper.like("ae.entrance_name", entranceName);
        }
        String villageId = request.getParameter("villageId");
        if (villageId != null && !villageId.isEmpty()) {
            queryWrapper.eq("ae.village_id", villageId);
        }
        String buildingId = request.getParameter("buildingId");
        if (buildingId != null && !buildingId.isEmpty()) {
            queryWrapper.eq("ae.building_id", buildingId);
        }
        String houseCount = request.getParameter("houseCountLike");
        if (houseCount != null && !houseCount.isEmpty()) {
            queryWrapper.like("ae.house_count", houseCount);
        }
        String buildingArea = request.getParameter("buildingAreaLike");
        if (buildingArea != null && !buildingArea.isEmpty()) {
            queryWrapper.like("ae.building_area", buildingArea);
        }
        String billingArea = request.getParameter("billingAreaLike");
        if (billingArea != null && !billingArea.isEmpty()) {
            queryWrapper.like("ae.billing_area", billingArea);
        }

        Page<ArchiveEntranceList> page = new Page<>(1, 10000);
        Page<ArchiveEntranceList> result = archiveEntranceListService
                .pageArchiveEntranceList(page, queryWrapper);
        List<ArchiveEntranceList> list = result.getRecords();
        ExcelUtil<ArchiveEntranceList> util = new ExcelUtil<ArchiveEntranceList>(ArchiveEntranceList.class);
        util.exportExcel(response, list, "楼门信息");
    }

    /**
     * 获取楼门信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:entrance:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(archiveEntranceListService.selectArchiveEntranceListById(id));
    }

    /**
     * 新增楼门信息
     */
    @PreAuthorize("@ss.hasPermi('biz:entrance:add')")
    @Log(title = "楼门信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ArchiveEntranceList archiveEntranceList) {
        // 自动填充创建人和创建时间
        archiveEntranceList.setCreatePerson(getUsername());
        archiveEntranceList.setCreateTime(new java.util.Date());
        // 同时填充更新人和更新时间
        archiveEntranceList.setUpdatePerson(getUsername());
        archiveEntranceList.setUpdateTime(new java.util.Date());
        return toAjax(archiveEntranceListService.insertArchiveEntranceList(archiveEntranceList));
    }

    /**
     * 修改楼门信息
     */
    @PreAuthorize("@ss.hasPermi('biz:entrance:edit')")
    @Log(title = "楼门信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ArchiveEntranceList archiveEntranceList) {
        // 自动填充更新人和更新时间
        archiveEntranceList.setUpdatePerson(getUsername());
        archiveEntranceList.setUpdateTime(new java.util.Date());
        return toAjax(archiveEntranceListService.updateArchiveEntranceList(archiveEntranceList));
    }

    /**
     * 删除楼门信息
     */
    @PreAuthorize("@ss.hasPermi('biz:entrance:remove')")
    @Log(title = "楼门信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(archiveEntranceListService.deleteArchiveEntranceListById(id));
    }

    /**
     * 批量删除楼门信息
     */
    @PreAuthorize("@ss.hasPermi('biz:entrance:remove')")
    @Log(title = "楼门信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult removeBatch(@RequestBody Long[] ids) {
        return toAjax(archiveEntranceListService.deleteArchiveEntranceListByIds(ids));
    }
}
