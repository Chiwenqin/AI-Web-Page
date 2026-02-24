package com.ruoyi.biz.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain .ArchiveBuildingList;

/**
 * 楼栋信息Service接口
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-01-05
 */
public interface IArchiveBuildingListService extends IService<ArchiveBuildingList> {
    IPage<ArchiveBuildingList> pageArchiveBuildingList(Page<ArchiveBuildingList> page, QueryWrapper<ArchiveBuildingList> queryWrapper);

    /**
     * 查询楼栋信息
     *
     * @param id 楼栋信息主键
     * @return 楼栋信息
     */
    public ArchiveBuildingList selectArchiveBuildingListById(Long id);

    /**
     * 新增楼栋信息
     *
     * @param archiveBuildingList 楼栋信息
     * @return 结果
     */
    public int insertArchiveBuildingList(ArchiveBuildingList archiveBuildingList);

    /**
     * 修改楼栋信息
     *
     * @param archiveBuildingList 楼栋信息
     * @return 结果
     */
    public int updateArchiveBuildingList(ArchiveBuildingList archiveBuildingList);

    /**
     * 批量删除楼栋信息
     *
     * @param ids 需要删除的楼栋信息主键集合
     * @return 结果
     */
    public int deleteArchiveBuildingListByIds(Long[] ids);

    /**
     * 删除楼栋信息信息
     *
     * @param id 楼栋信息主键
     * @return 结果
     */
    public int deleteArchiveBuildingListById(Long id);

    /**
     * 导入楼栋信息数据
     *
     * @param list            数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    public String importArchiveBuildingList(
    List<ArchiveBuildingList> list, int titleNum, Boolean
    isUpdateSupport,
    String operName);
}
