package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.ArchiveRoomList;

/**
 * 房间信息Service接口
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-03-01
 */
public interface IArchiveRoomListService extends IService<ArchiveRoomList> {

    /**
     * 查询房间信息列表
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 房间信息分页列表
     */
    Page<ArchiveRoomList> pageArchiveRoomList(Page<ArchiveRoomList> page,
            QueryWrapper<ArchiveRoomList> queryWrapper);

    /**
     * 查询房间信息
     *
     * @param id 房间信息主键
     * @return 房间信息
     */
    ArchiveRoomList selectArchiveRoomListById(Long id);

    /**
     * 新增房间信息
     *
     * @param archiveRoomList 房间信息
     * @return 结果
     */
    int insertArchiveRoomList(ArchiveRoomList archiveRoomList);

    /**
     * 修改房间信息
     *
     * @param archiveRoomList 房间信息
     * @return 结果
     */
    int updateArchiveRoomList(ArchiveRoomList archiveRoomList);

    /**
     * 批量删除房间信息
     *
     * @param ids 需要删除的房间信息主键集合
     * @return 结果
     */
    int deleteArchiveRoomListByIds(Long[] ids);

    /**
     * 删除房间信息信息
     *
     * @param id 房间信息主键
     * @return 结果
     */
    int deleteArchiveRoomListById(Long id);
}