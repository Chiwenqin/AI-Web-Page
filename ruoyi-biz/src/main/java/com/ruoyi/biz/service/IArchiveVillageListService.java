package com.ruoyi.biz.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain .ArchiveVillageList;

/**
 * 小区列Service接口
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2025-12-31
 */
public interface IArchiveVillageListService extends IService<ArchiveVillageList> {
    public IPage<ArchiveVillageList> pageArchiveVillageList(Page<ArchiveVillageList> page, QueryWrapper<ArchiveVillageList> queryWrapper);

    /**
     * 查询小区列
     *
     * @param id 小区列主键
     * @return 小区列
     */
    public ArchiveVillageList selectArchiveVillageListById(Long id);

    /**
     * 新增小区列
     *
     * @param archiveVillageList 小区列
     * @return 结果
     */
    public int insertArchiveVillageList(ArchiveVillageList archiveVillageList);

    /**
     * 修改小区列
     *
     * @param archiveVillageList 小区列
     * @return 结果
     */
    public int updateArchiveVillageList(ArchiveVillageList archiveVillageList);

    /**
     * 批量删除小区列
     *
     * @param ids 需要删除的小区列主键集合
     * @return 结果
     */
    public int deleteArchiveVillageListByIds(Long[] ids);

    /**
     * 删除小区列信息
     *
     * @param id 小区列主键
     * @return 结果
     */
    public int deleteArchiveVillageListById(Long id);

    /**
     * 导入小区列数据
     *
     * @param list            数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    public String importArchiveVillageList(
    List<ArchiveVillageList> list, int titleNum, Boolean
    isUpdateSupport,
    String operName);
}
