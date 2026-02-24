<template>
  <div class="app-container">
    <el-form @submit.prevent :model="ArchiveBuildingListQueryParams" ref="ArchiveBuildingListQueryRef" :inline="true" v-show="ArchiveBuildingListShowSearch" label-width="auto">
      <el-form-item label="楼栋编码" prop="buildingCodeLike">
        <el-input
          v-model="ArchiveBuildingListQueryParams.buildingCodeLike"
          placeholder="请输入楼栋编码"
          clearable
          @keyup.enter="ArchiveBuildingListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="楼栋名称" prop="buildingNameLike">
        <el-input
          v-model="ArchiveBuildingListQueryParams.buildingNameLike"
          placeholder="请输入楼栋名称"
          clearable
          @keyup.enter="ArchiveBuildingListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="楼门数量" prop="entranceCountLike">
        <el-input
          v-model="ArchiveBuildingListQueryParams.entranceCountLike"
          placeholder="请输入楼门数量"
          clearable
          @keyup.enter="ArchiveBuildingListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="房屋数量" prop="houseCountLike">
        <el-input
          v-model="ArchiveBuildingListQueryParams.houseCountLike"
          placeholder="请输入房屋数量"
          clearable
          @keyup.enter="ArchiveBuildingListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="建筑面积" prop="buildingAreaLike">
        <el-input
          v-model="ArchiveBuildingListQueryParams.buildingAreaLike"
          placeholder="请输入建筑面积"
          clearable
          @keyup.enter="ArchiveBuildingListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="计费面积" prop="billingAreaLike">
        <el-input
          v-model="ArchiveBuildingListQueryParams.billingAreaLike"
          placeholder="请输入计费面积"
          clearable
          @keyup.enter="ArchiveBuildingListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="所属小区" prop="villageId">
        <el-select
          v-model="ArchiveBuildingListQueryParams.villageId"
          placeholder="请选择所属小区"
          clearable
          filterable
        >
          <el-option
            v-for="item in villageList"
            :key="item.id"
            :label="item.villageName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="ArchiveBuildingListHandleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="ArchiveBuildingListResetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="ArchiveBuildingListHandleAdd"
          v-hasPermi="['biz:ArchiveBuildingList:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="ArchiveBuildingListSingle"
          @click="ArchiveBuildingListHandleUpdate"
          v-hasPermi="['biz:ArchiveBuildingList:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="ArchiveBuildingListMultiple"
          @click="ArchiveBuildingListHandleDelete"
          v-hasPermi="['biz:ArchiveBuildingList:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="ArchiveBuildingListHandleExport"
          v-hasPermi="['biz:ArchiveBuildingList:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="info"
            plain
            icon="Download"
            @click="ArchiveBuildingListImportTemplate"
            v-hasPermi="['biz:ArchiveBuildingList:import']"
        >下载模版</el-button>
      </el-col>
      <el-col :span="1.5">
      <file-upload
            v-model="importRes"
            v-hasPermi="['biz:ArchiveBuildingList:import']"
            :limit="1"
            uploadBtnText="导入"
            :fileType="['xlsx']"
            accept=".xlsx"
            :action="'/biz/ArchiveBuildingList/importData'"
            :isShowTip="false"
            :isShowFileList="false"
            isImport
          />
      </el-col>
      <right-toolbar v-model:showSearch="ArchiveBuildingListShowSearch" @queryTable="ArchiveBuildingListGetList"></right-toolbar>
    </el-row>

    <el-table v-loading="ArchiveBuildingListLoading" :data="ArchiveBuildingListList" @selection-change="ArchiveBuildingListHandleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column width="50" label="主键ID" align="center" prop="id" />
      <el-table-column label="楼栋编码" align="center" prop="buildingCode" />
      <el-table-column label="楼栋名称" align="center" prop="buildingName" />
      <el-table-column label="楼门数量" align="center" prop="entranceCount" />
      <el-table-column label="房屋数量" align="center" prop="houseCount" />
      <el-table-column label="建筑面积" align="center" prop="buildingArea" />
      <el-table-column label="计费面积" align="center" prop="billingArea" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="所属小区" align="center" prop="villageName" />
      <el-table-column label="创建人" align="center" prop="createPerson" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新人" align="center" prop="updatePerson" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column width="130" label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="ArchiveBuildingListHandleUpdate(scope.row)" v-hasPermi="['biz:ArchiveBuildingList:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="ArchiveBuildingListHandleDelete(scope.row)" v-hasPermi="['biz:ArchiveBuildingList:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="ArchiveBuildingListTotal>0"
      :total="ArchiveBuildingListTotal"
      v-model:page="ArchiveBuildingListQueryParams.pageNum"
      v-model:limit="ArchiveBuildingListQueryParams.pageSize"
      @pagination="ArchiveBuildingListGetList"
    />

    <!-- 添加或修改楼栋信息对话框 -->
    <el-dialog :title="ArchiveBuildingListTitle" v-model="ArchiveBuildingListOpen" width="500px" append-to-body destroy-on-close>
      <el-form @submit.prevent ref="ArchiveBuildingListRef" :model="ArchiveBuildingListForm" :rules="ArchiveBuildingListRules" label-width="auto">
        <el-form-item label="楼栋编码" prop="buildingCode">
          <el-input v-model="ArchiveBuildingListForm.buildingCode" placeholder="请输入楼栋编码" />
        </el-form-item>
        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input v-model="ArchiveBuildingListForm.buildingName" placeholder="请输入楼栋名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="ArchiveBuildingListForm.sort" placeholder="请输入排序" />
        </el-form-item>
        <el-form-item label="楼门数量" prop="entranceCount">
          <el-input v-model="ArchiveBuildingListForm.entranceCount" placeholder="请输入楼门数量" />
        </el-form-item>
        <el-form-item label="房屋数量" prop="houseCount">
          <el-input v-model="ArchiveBuildingListForm.houseCount" placeholder="请输入房屋数量" />
        </el-form-item>
        <el-form-item label="建筑面积" prop="buildingArea">
          <el-input v-model="ArchiveBuildingListForm.buildingArea" placeholder="请输入建筑面积" />
        </el-form-item>
        <el-form-item label="计费面积" prop="billingArea">
          <el-input v-model="ArchiveBuildingListForm.billingArea" placeholder="请输入计费面积" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="ArchiveBuildingListForm.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属小区" prop="villageId">
          <el-select
            v-model="ArchiveBuildingListForm.villageId"
            placeholder="请选择所属小区"
            clearable
            filterable
          >
            <el-option
              v-for="item in villageList"
              :key="item.id"
              :label="item.villageName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="ArchiveBuildingListSubmitForm">确 定</el-button>
          <el-button @click="ArchiveBuildingListCancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ArchiveBuildingList">
import { listArchiveBuildingList, getArchiveBuildingList, delArchiveBuildingList, addArchiveBuildingList, updateArchiveBuildingList } from "@/api/biz/ArchiveBuildingList";
import { listArchiveVillageList } from "@/api/biz/ArchiveVillageList";
import xeUtils from "xe-utils";

const route = useRoute();
const action = ref(null);

const villageList = ref([]);

onMounted(() => {
  ArchiveBuildingListHandleQuery();
  getVillageList();
});

const { proxy } = getCurrentInstance();
const { sys_common_status } = proxy.useDict('sys_common_status');

/** 获取小区列表 */
function getVillageList() {
  listArchiveVillageList({ pageNum: 1, pageSize: 9999 }).then(response => {
    villageList.value = response.rows;
  });
}

const importRes = ref(null);
watch(
  () => importRes,
  (val) => {
    if (val.value && val.value.code == 200) {
      ArchiveBuildingListHandleQuery();
    }
  },
  { deep: true, immediate: true }
);

const ArchiveBuildingListList = ref([]);
const ArchiveBuildingListOpen = ref(false);
const ArchiveBuildingListLoading = ref(true);
const ArchiveBuildingListShowSearch = ref(true);
const ArchiveBuildingListIds = ref([]);
const ArchiveBuildingListSingle = ref(true);
const ArchiveBuildingListMultiple = ref(true);
const ArchiveBuildingListTotal = ref(0);
const ArchiveBuildingListTitle = ref("");

const ArchiveBuildingListData = reactive({
  ArchiveBuildingListForm: {},
  ArchiveBuildingListQueryParams: {
    pageNum: 1,
    pageSize: 10,
    buildingCode: null,
    buildingName: null,
    entranceCount: null,
    houseCount: null,
    buildingArea: null,
    billingArea: null,
    villageId: null,
    createPerson: null,
    updatePerson: null,
  },
  ArchiveBuildingListRules: {
    buildingCode: [
      { required: true, message: "楼栋编码不能为空", trigger: "blur" }
    ],
    buildingName: [
      { required: true, message: "楼栋名称不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ]
  }
});

const { ArchiveBuildingListQueryParams, ArchiveBuildingListForm, ArchiveBuildingListRules } = toRefs(ArchiveBuildingListData);

watch(
  route,
  (newRoute) => {
    action.value = newRoute.query && newRoute.query.action;
  },
  { immediate: true }
);

/** 查询楼栋信息列表 */
function ArchiveBuildingListGetList() {
  ArchiveBuildingListLoading.value = true;
  listArchiveBuildingList(ArchiveBuildingListQueryParams.value).then(response => {
    ArchiveBuildingListList.value = response.rows;
    ArchiveBuildingListTotal.value = response.total;
    ArchiveBuildingListLoading.value = false;
  });
}

// 取消按钮
function ArchiveBuildingListCancel() {
  ArchiveBuildingListOpen.value = false;
  ArchiveBuildingListReset();
}

// 表单重置
function ArchiveBuildingListReset() {
  ArchiveBuildingListForm.value = {
    id: null,
    buildingCode: null,
    buildingName: null,
    sort: null,
    entranceCount: null,
    houseCount: null,
    buildingArea: null,
    billingArea: null,
    status: null,
    villageId: null,
    createPerson: null,
    createTime: null,
    updatePerson: null,
    updateTime: null
  };
  proxy.resetForm("ArchiveBuildingListRef");
}

/** 搜索按钮操作 */
function ArchiveBuildingListHandleQuery() {
  ArchiveBuildingListQueryParams.value.pageNum = 1;
  ArchiveBuildingListGetList();
}

/** 重置按钮操作 */
function ArchiveBuildingListResetQuery() {
  proxy.resetForm("ArchiveBuildingListQueryRef");
  ArchiveBuildingListHandleQuery();
}

// 多选框选中数据
function ArchiveBuildingListHandleSelectionChange(selection) {
  ArchiveBuildingListIds.value = selection.map(item => item.id);
  ArchiveBuildingListSingle.value = selection.length != 1;
  ArchiveBuildingListMultiple.value = !selection.length;
}

/** 新增按钮操作 */
function ArchiveBuildingListHandleAdd() {
  ArchiveBuildingListReset();
  ArchiveBuildingListOpen.value = true;
  ArchiveBuildingListTitle.value = "添加";
}

/** 修改按钮操作 */
function ArchiveBuildingListHandleUpdate(row) {
  ArchiveBuildingListReset();
  const _id = row.id || ArchiveBuildingListIds.value
  getArchiveBuildingList(_id).then(response => {
    ArchiveBuildingListForm.value = response.data;
    ArchiveBuildingListOpen.value = true;
    ArchiveBuildingListTitle.value = "修改";
  });
}

/** 提交按钮 */
function ArchiveBuildingListSubmitForm() {
  proxy.$refs["ArchiveBuildingListRef"].validate(valid => {
    if (valid) {
        const submitForm = xeUtils.clone(ArchiveBuildingListForm.value, true);
      if (submitForm.id != null) {
        updateArchiveBuildingList(submitForm).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          ArchiveBuildingListOpen.value = false;
          ArchiveBuildingListGetList();
        });
      } else {
        addArchiveBuildingList(submitForm).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          ArchiveBuildingListOpen.value = false;
          ArchiveBuildingListGetList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function ArchiveBuildingListHandleDelete(row) {
  const _ids = row.id || ArchiveBuildingListIds.value;
  proxy.$modal.confirm('是否确认删除编号为"' + _ids + '"的数据项？').then(function() {
    return delArchiveBuildingList(_ids);
  }).then(() => {
    ArchiveBuildingListGetList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function ArchiveBuildingListHandleExport() {
  proxy.download('biz/ArchiveBuildingList/export', {
    ...ArchiveBuildingListQueryParams.value
  }, `楼栋信息_${new Date().getTime()}.xlsx`)
}

/** 下载模板操作 */
function ArchiveBuildingListImportTemplate() {
  proxy.download(
      "biz/ArchiveBuildingList/importTemplate",{},
      `楼栋信息_${new Date().getTime()}.xlsx`
  );
}
</script>
<style lang="scss" scoped>

</style>