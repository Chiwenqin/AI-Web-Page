package com.ruoyi.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.ArrayList;

import org.springframework.validation.annotation.Validated;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.RepeatSubmit;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.biz.domain.ArchiveVillageList;
import com.ruoyi.biz.service.IArchiveVillageListService;
import com.ruoyi.biz.mapper.ArchiveVillageListMapper;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 小区列Controller
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2025-12-31
 */
@RestController
@RequestMapping("/biz/ArchiveVillageList")
@RequiredArgsConstructor
public class ArchiveVillageListController extends BaseController {
    private final IArchiveVillageListService archiveVillageListService;
    // private final ArchiveVillageListMapper archiveVillageListMapper;

/**
 * 查询小区列列表
 */
@PreAuthorize("@ss.hasPermi('biz:ArchiveVillageList:list')")
@GetMapping("/list")
    public TableDataInfo list(ArchiveVillageList archiveVillageList) {
        QueryWrapper<ArchiveVillageList> queryWrapper = getQueryWrapper(ArchiveVillageList.class);
        IPage<ArchiveVillageList> page = archiveVillageListService.pageArchiveVillageList(getPage(), queryWrapper);
        return getDataTableByPage(page);
    }

    /**
     * 导出小区列列表
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveVillageList:export')")
    @Log(title = "小区列", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ArchiveVillageList archiveVillageList) {
        QueryWrapper<ArchiveVillageList> queryWrapper = getQueryWrapper(ArchiveVillageList.class);
        IPage<ArchiveVillageList> page = archiveVillageListService.pageArchiveVillageList(getPage(), queryWrapper);
        List<ArchiveVillageList> list = page.getRecords();
        ExcelUtil<ArchiveVillageList> util = new ExcelUtil<ArchiveVillageList>(ArchiveVillageList. class);
        util.exportExcel(response, list, "小区列");
    }

    /**
     * 导入小区列数据
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveVillageList:import')")
    @Log(title = "小区列", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        int titleNum = 0;
        ExcelUtil<ArchiveVillageList> util = new ExcelUtil<ArchiveVillageList>(ArchiveVillageList. class);
        List<ArchiveVillageList> list = util.importExcel(file.getInputStream(), titleNum);
        String operName = getUsername();
        String message = archiveVillageListService.importArchiveVillageList(list, titleNum, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, ArchiveVillageList archiveVillageList) {
        List<ArchiveVillageList> list = new ArrayList<>();
        ExcelUtil<ArchiveVillageList> util = new ExcelUtil<ArchiveVillageList>(ArchiveVillageList. class);
        util.exportExcel(response, list, "小区列");
    }

    /**
     * 获取小区列详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveVillageList:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(archiveVillageListService.selectArchiveVillageListById(id));
    }

    /**
     * 新增小区列
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveVillageList:add')")
    @Log(title = "小区列", businessType = BusinessType.INSERT)
    @PostMapping
    @RepeatSubmit
    public AjaxResult add(@Validated @RequestBody ArchiveVillageList archiveVillageList) {
            archiveVillageListService.insertArchiveVillageList(archiveVillageList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("id", archiveVillageList.getId());
        return success(jsonObject);
    }

    /**
     * 修改小区列
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveVillageList:edit')")
    @Log(title = "小区列", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ArchiveVillageList archiveVillageList) {
        return toAjax(archiveVillageListService.updateArchiveVillageList(archiveVillageList));
    }

    /**
     * 删除小区列
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveVillageList:remove')")
    @Log(title = "小区列", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(archiveVillageListService.deleteArchiveVillageListByIds(ids));
    }
}
