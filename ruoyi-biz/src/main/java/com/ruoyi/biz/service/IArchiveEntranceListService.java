package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.ArchiveEntranceList;

/**
 * 楼门信息Service接口
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-02-27
 */
public interface IArchiveEntranceListService extends IService<ArchiveEntranceList> {

    /**
     * 查询楼门信息列表
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 楼门信息分页列表
     */
    Page<ArchiveEntranceList> pageArchiveEntranceList(Page<ArchiveEntranceList> page,
            QueryWrapper<ArchiveEntranceList> queryWrapper);

    /**
     * 查询楼门信息
     *
     * @param id 楼门信息主键
     * @return 楼门信息
     */
    ArchiveEntranceList selectArchiveEntranceListById(Long id);

    /**
     * 新增楼门信息
     *
     * @param archiveEntranceList 楼门信息
     * @return 结果
     */
    int insertArchiveEntranceList(ArchiveEntranceList archiveEntranceList);

    /**
     * 修改楼门信息
     *
     * @param archiveEntranceList 楼门信息
     * @return 结果
     */
    int updateArchiveEntranceList(ArchiveEntranceList archiveEntranceList);

    /**
     * 批量删除楼门信息
     *
     * @param ids 需要删除的楼门信息主键集合
     * @return 结果
     */
    int deleteArchiveEntranceListByIds(Long[] ids);

    /**
     * 删除楼门信息信息
     *
     * @param id 楼门信息主键
     * @return 结果
     */
    int deleteArchiveEntranceListById(Long id);
}
