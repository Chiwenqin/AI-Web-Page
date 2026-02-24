package com.ruoyi.biz.service.impl;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.ruoyi.biz.mapper.ArchiveVillageListMapper;
import com.ruoyi.biz.domain.ArchiveVillageList;
import com.ruoyi.biz.service.IArchiveVillageListService;

import jakarta.validation.Validator;

/**
 * 小区列Service业务层处理
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2025-12-31
 */
@Service
@RequiredArgsConstructor
public class ArchiveVillageListServiceImpl extends ServiceImpl<ArchiveVillageListMapper,ArchiveVillageList> implements IArchiveVillageListService {
    private final ArchiveVillageListMapper archiveVillageListMapper;
    protected final Validator validator;

    @Override
    public String importArchiveVillageList(
    List<ArchiveVillageList> list, int titleNum, Boolean
    isUpdateSupport,
    String operName)

    {
        if (StringUtils.isNull(list) || list.size() == 0) {
            throw new ServiceException("导入数据不能为空！" );
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            ArchiveVillageList archiveVillageList =list.get(i);
            try {
                QueryWrapper<ArchiveVillageList> queryWrapper = new QueryWrapper<>();
                List<ArchiveVillageList> checkList = new ArrayList<>(); //archiveVillageListMapper.selectList(queryWrapper);
                if (checkList.size() == 0) {
                    BeanValidators.validateWithException(validator, archiveVillageList);
                    insertArchiveVillageList(archiveVillageList);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、记录" + (i + titleNum + 2) + "：" + archiveVillageList.getId() + " 导入成功")
                    ;
                } else if (isUpdateSupport) {
                    BeanValidators.validateWithException(validator, archiveVillageList);
                    archiveVillageList.setId(checkList.get(0).getId());
                    updateArchiveVillageList(archiveVillageList);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、记录" + (i + titleNum + 2) + " 更新成功")
                    ;
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、记录" + (i + titleNum + 2) + " 已存在")
                    ;
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、记录" + (i + titleNum + 2) + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：" );
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：" );
        }
        return successMsg.toString();
    }

    @Override
    public IPage<ArchiveVillageList> pageArchiveVillageList(Page<ArchiveVillageList> page, QueryWrapper<ArchiveVillageList> queryWrapper) {
        return archiveVillageListMapper.selectPage(page, queryWrapper);
    }

    /**
     * 查询小区列
     *
     * @param id 小区列主键
     * @return 小区列
     */
    @Override
    public ArchiveVillageList selectArchiveVillageListById(Long id) {
        return archiveVillageListMapper.selectById(id);
    }

    /**
     * 新增小区列
     *
     * @param archiveVillageList 小区列
     * @return 结果
     */
    @Override
    public int insertArchiveVillageList(ArchiveVillageList archiveVillageList) {
            return archiveVillageListMapper.insert(archiveVillageList);
    }

    /**
     * 修改小区列
     *
     * @param archiveVillageList 小区列
     * @return 结果
     */
    @Override
    public int updateArchiveVillageList(ArchiveVillageList archiveVillageList) {
        return archiveVillageListMapper.updateById(archiveVillageList);
    }

    /**
     * 批量删除小区列
     *
     * @param ids 需要删除的小区列主键
     * @return 结果
     */
    @Override
    public int deleteArchiveVillageListByIds(Long[] ids) {
        return archiveVillageListMapper.deleteByIds(Arrays.asList(ids));
    }

    /**
     * 删除小区列信息
     *
     * @param id 小区列主键
     * @return 结果
     */
    @Override
    public int deleteArchiveVillageListById(Long id) {
        return archiveVillageListMapper.deleteById(id);
    }
}
