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
        import com.ruoyi.common.utils.DateUtils;
import org.springframework.stereotype.Service;
import com.ruoyi.biz.mapper.ArchiveBuildingListMapper;
import com.ruoyi.biz.domain.ArchiveBuildingList;
import com.ruoyi.biz.service.IArchiveBuildingListService;

import jakarta.validation.Validator;

/**
 * 楼栋信息Service业务层处理
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-01-05
 */
@Service
@RequiredArgsConstructor
public class ArchiveBuildingListServiceImpl extends ServiceImpl<ArchiveBuildingListMapper,ArchiveBuildingList> implements IArchiveBuildingListService {
    private final ArchiveBuildingListMapper archiveBuildingListMapper;
    protected final Validator validator;

    @Override
    public String importArchiveBuildingList(
    List<ArchiveBuildingList> list, int titleNum, Boolean
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
            ArchiveBuildingList archiveBuildingList =list.get(i);
            try {
                QueryWrapper<ArchiveBuildingList> queryWrapper = new QueryWrapper<>();
                List<ArchiveBuildingList> checkList = new ArrayList<>(); //archiveBuildingListMapper.selectList(queryWrapper);
                if (checkList.size() == 0) {
                    BeanValidators.validateWithException(validator, archiveBuildingList);
                    insertArchiveBuildingList(archiveBuildingList);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、记录" + (i + titleNum + 2) + "：" + archiveBuildingList.getId() + " 导入成功")
                    ;
                } else if (isUpdateSupport) {
                    BeanValidators.validateWithException(validator, archiveBuildingList);
                    archiveBuildingList.setId(checkList.get(0).getId());
                    updateArchiveBuildingList(archiveBuildingList);
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
    public IPage<ArchiveBuildingList> pageArchiveBuildingList(Page<ArchiveBuildingList> page, QueryWrapper<ArchiveBuildingList> queryWrapper) {
        return archiveBuildingListMapper.selectListPage(page, queryWrapper);
    }

    /**
     * 查询楼栋信息
     *
     * @param id 楼栋信息主键
     * @return 楼栋信息
     */
    @Override
    public ArchiveBuildingList selectArchiveBuildingListById(Long id) {
        return archiveBuildingListMapper.selectById(id);
    }

    /**
     * 新增楼栋信息
     *
     * @param archiveBuildingList 楼栋信息
     * @return 结果
     */
    @Override
    public int insertArchiveBuildingList(ArchiveBuildingList archiveBuildingList) {
            return archiveBuildingListMapper.insert(archiveBuildingList);
    }

    /**
     * 修改楼栋信息
     *
     * @param archiveBuildingList 楼栋信息
     * @return 结果
     */
    @Override
    public int updateArchiveBuildingList(ArchiveBuildingList archiveBuildingList) {
        return archiveBuildingListMapper.updateById(archiveBuildingList);
    }

    /**
     * 批量删除楼栋信息
     *
     * @param ids 需要删除的楼栋信息主键
     * @return 结果
     */
    @Override
    public int deleteArchiveBuildingListByIds(Long[] ids) {
        return archiveBuildingListMapper.deleteByIds(Arrays.asList(ids));
    }

    /**
     * 删除楼栋信息信息
     *
     * @param id 楼栋信息主键
     * @return 结果
     */
    @Override
    public int deleteArchiveBuildingListById(Long id) {
        return archiveBuildingListMapper.deleteById(id);
    }
}
