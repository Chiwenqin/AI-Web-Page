package com.ruoyi.biz.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 小区列对象 archive_village_list
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2025-12-31
 */
@Data
@TableName("archive_village_list" )
public class ArchiveVillageList {
private static final long serialVersionUID = 1L;

            /** 主键ID */
            @TableId(type = IdType.AUTO)
            @OrderBy(asc = false, sort = 1)
        private Long id;
        public static final String ID = "id";

            /** 小区编码 */
                @Excel(name = "小区编码" )
            @TableField("village_code" )
            @NotBlank(message = "小区编码不能为空")
            @Size(min = 0, max = 255, message = "小区编码长度不能超过255个字符")
        private String villageCode;
        public static final String VILLAGECODE = "village_code";

            /** 小区名字 */
                @Excel(name = "小区名字" )
            @TableField("village_name" )
            @NotBlank(message = "小区名字不能为空")
            @Size(min = 0, max = 255, message = "小区名字长度不能超过255个字符")
        private String villageName;
        public static final String VILLAGENAME = "village_name";

            /** 小区楼栋数量 */
                @Excel(name = "小区楼栋数量" )
            @TableField("village_count" )
            @Size(min = 0, max = 255, message = "小区楼栋数量长度不能超过255个字符")
        private String villageCount;
        public static final String VILLAGECOUNT = "village_count";

            /** 小区楼门数量 */
                @Excel(name = "小区楼门数量" )
            @TableField("village_entrance" )
            @Size(min = 0, max = 255, message = "小区楼门数量长度不能超过255个字符")
        private String villageEntrance;
        public static final String VILLAGEENTRANCE = "village_entrance";

            /** 小区房屋数量 */
                @Excel(name = "小区房屋数量" )
            @TableField("village_house" )
            @Size(min = 0, max = 255, message = "小区房屋数量长度不能超过255个字符")
        private String villageHouse;
        public static final String VILLAGEHOUSE = "village_house";

            /** 建筑面积 */
                @Excel(name = "建筑面积" )
            @TableField("floor_area" )
            @Size(min = 0, max = 255, message = "建筑面积长度不能超过255个字符")
        private String floorArea;
        public static final String FLOORAREA = "floor_area";

            /** 计费面积 */
                @Excel(name = "计费面积" )
            @TableField("billing_area" )
            @Size(min = 0, max = 255, message = "计费面积长度不能超过255个字符")
        private String billingArea;
        public static final String BILLINGAREA = "billing_area";

            /** 创建时间 */
                @JsonFormat(pattern = "yyyy-MM-dd" )
                @Excel(name = "创建时间" , width = 30, dateFormat = "yyyy-MM-dd" )
            @TableField("create_datetime" )
        private Date createDatetime;
        public static final String CREATEDATETIME = "create_datetime";

            /** 创建人 */
                @Excel(name = "创建人" )
            @TableField("create_person" )
        private String createPerson;
        public static final String CREATEPERSON = "create_person";

            /** 更新时间 */
                @JsonFormat(pattern = "yyyy-MM-dd" )
                @Excel(name = "更新时间" , width = 30, dateFormat = "yyyy-MM-dd" )
            @TableField("update_datetime" )
        private Date updateDatetime;
        public static final String UPDATEDATETIME = "update_datetime";

            /** 更新人 */
                @Excel(name = "更新人" )
            @TableField("update_person" )
        private String updatePerson;
        public static final String UPDATEPERSON = "update_person";

            /** 是否启用 */
            // 逻辑删除替代物理删除，'0'：正常；'1'：删除
            // @TableLogic(value = "'0'" , delval = "'1'" )
            @NotNull(message = "是否启用不能为空")
            @Size(min = 0, max = 255, message = "是否启用长度不能超过255个字符")
        private String status;
        public static final String STATUS = "status";

            /** 所属公司 */
                @Excel(name = "所属公司" )
            @TableField("village_company" )
            @Size(min = 0, max = 255, message = "所属公司长度不能超过255个字符")
        private String villageCompany;
        public static final String VILLAGECOMPANY = "village_company";

}
