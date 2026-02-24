<template>
  <div class="app-container">
    <el-form @submit.prevent :model="ArchiveVillageListQueryParams" ref="ArchiveVillageListQueryRef" :inline="true" v-show="ArchiveVillageListShowSearch" label-width="auto">
      <el-form-item label="创建时间">
        <el-date-picker
            style="width: 230px;"
            v-model="dateRange"
            value-format="YYYY-MM-DD"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="小区编码" prop="villageCodeLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.villageCodeLike"
          placeholder="请输入小区编码"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="小区名字" prop="villageNameLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.villageNameLike"
          placeholder="请输入小区名字"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="小区楼栋数量" prop="villageCountLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.villageCountLike"
          placeholder="请输入小区楼栋数量"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="小区楼门数量" prop="villageEntranceLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.villageEntranceLike"
          placeholder="请输入小区楼门数量"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="小区房屋数量" prop="villageHouseLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.villageHouseLike"
          placeholder="请输入小区房屋数量"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="建筑面积" prop="floorAreaLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.floorAreaLike"
          placeholder="请输入建筑面积"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="计费面积" prop="billingAreaLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.billingAreaLike"
          placeholder="请输入计费面积"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createDatetime">
        <el-date-picker clearable
          v-model="ArchiveVillageListQueryParams.createDatetime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="创建人" prop="createPersonLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.createPersonLike"
          placeholder="请输入创建人"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="更新时间" prop="updateDatetime">
        <el-date-picker clearable
          v-model="ArchiveVillageListQueryParams.updateDatetime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择更新时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="更新人" prop="updatePersonLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.updatePersonLike"
          placeholder="请输入更新人"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item label="所属公司" prop="villageCompanyLike">
        <el-input
          v-model="ArchiveVillageListQueryParams.villageCompanyLike"
          placeholder="请输入所属公司"
          clearable
          @keyup.enter="ArchiveVillageListHandleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="ArchiveVillageListHandleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="ArchiveVillageListResetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="ArchiveVillageListHandleAdd"
          v-hasPermi="['biz:ArchiveVillageList:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="ArchiveVillageListSingle"
          @click="ArchiveVillageListHandleUpdate"
          v-hasPermi="['biz:ArchiveVillageList:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="ArchiveVillageListMultiple"
          @click="ArchiveVillageListHandleDelete"
          v-hasPermi="['biz:ArchiveVillageList:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="ArchiveVillageListHandleExport"
          v-hasPermi="['biz:ArchiveVillageList:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="info"
            plain
            icon="Download"
            @click="ArchiveVillageListImportTemplate"
            v-hasPermi="['biz:ArchiveVillageList:import']"
        >下载模版</el-button>
      </el-col>
      <el-col :span="1.5">
      <file-upload
            v-model="importRes"
            v-hasPermi="['biz:ArchiveVillageList:import']"
            :limit="1"
            uploadBtnText="导入"
            :fileType="['xlsx']"
            accept=".xlsx"
            :action="'/biz/ArchiveVillageList/importData'"
            :isShowTip="false"
            :isShowFileList="false"
            isImport
          />
      </el-col>
      <right-toolbar v-model:showSearch="ArchiveVillageListShowSearch" @queryTable="ArchiveVillageListGetList"></right-toolbar>
    </el-row>

    <el-table v-loading="ArchiveVillageListLoading" :data="ArchiveVillageListList" @selection-change="ArchiveVillageListHandleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column width="50" label="主键ID" align="center" prop="id" />
      <el-table-column label="小区编码" align="center" prop="villageCode" />
      <el-table-column label="小区名字" align="center" prop="villageName" />
      <el-table-column label="小区楼栋数量" align="center" prop="villageCount" />
      <el-table-column label="小区楼门数量" align="center" prop="villageEntrance" />
      <el-table-column label="小区房屋数量" align="center" prop="villageHouse" />
      <el-table-column label="建筑面积" align="center" prop="floorArea" />
      <el-table-column label="计费面积" align="center" prop="billingArea" />
      <el-table-column label="创建时间" align="center" prop="createDatetime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createDatetime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createPerson" />
      <el-table-column label="更新时间" align="center" prop="updateDatetime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updateDatetime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新人" align="center" prop="updatePerson" />
      <el-table-column label="所属公司" align="center" prop="villageCompany" />
      <el-table-column width="130" label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="ArchiveVillageListHandleUpdate(scope.row)" v-hasPermi="['biz:ArchiveVillageList:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="ArchiveVillageListHandleDelete(scope.row)" v-hasPermi="['biz:ArchiveVillageList:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="ArchiveVillageListTotal>0"
      :total="ArchiveVillageListTotal"
      v-model:page="ArchiveVillageListQueryParams.pageNum"
      v-model:limit="ArchiveVillageListQueryParams.pageSize"
      @pagination="ArchiveVillageListGetList"
    />

    <!-- 添加或修改小区列对话框 -->
    <el-dialog :title="ArchiveVillageListTitle" v-model="ArchiveVillageListOpen" width="500px" append-to-body destroy-on-close>
      <el-form @submit.prevent ref="ArchiveVillageListRef" :model="ArchiveVillageListForm" :rules="ArchiveVillageListRules" label-width="auto">
        <el-form-item label="小区编码" prop="villageCode">
          <el-input v-model="ArchiveVillageListForm.villageCode" placeholder="请输入小区编码" />
        </el-form-item>
        <el-form-item label="小区名字" prop="villageName">
          <el-input v-model="ArchiveVillageListForm.villageName" placeholder="请输入小区名字" />
        </el-form-item>
        <el-form-item label="小区楼栋数量" prop="villageCount">
          <el-input v-model="ArchiveVillageListForm.villageCount" placeholder="请输入小区楼栋数量" />
        </el-form-item>
        <el-form-item label="小区楼门数量" prop="villageEntrance">
          <el-input v-model="ArchiveVillageListForm.villageEntrance" placeholder="请输入小区楼门数量" />
        </el-form-item>
        <el-form-item label="小区房屋数量" prop="villageHouse">
          <el-input v-model="ArchiveVillageListForm.villageHouse" placeholder="请输入小区房屋数量" />
        </el-form-item>
        <el-form-item label="建筑面积" prop="floorArea">
          <el-input v-model="ArchiveVillageListForm.floorArea" placeholder="请输入建筑面积" />
        </el-form-item>
        <el-form-item label="计费面积" prop="billingArea">
          <el-input v-model="ArchiveVillageListForm.billingArea" placeholder="请输入计费面积" />
        </el-form-item>
        <el-form-item label="创建时间" prop="createDatetime">
          <el-date-picker clearable style="width: 100%;"
            v-model="ArchiveVillageListForm.createDatetime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="创建人" prop="createPerson">
          <el-input v-model="ArchiveVillageListForm.createPerson" placeholder="请输入创建人" />
        </el-form-item>
        <el-form-item label="更新时间" prop="updateDatetime">
          <el-date-picker clearable style="width: 100%;"
            v-model="ArchiveVillageListForm.updateDatetime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择更新时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="更新人" prop="updatePerson">
          <el-input v-model="ArchiveVillageListForm.updatePerson" placeholder="请输入更新人" />
        </el-form-item>
        <el-form-item label="所属公司" prop="villageCompany">
          <el-input v-model="ArchiveVillageListForm.villageCompany" placeholder="请输入所属公司" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="ArchiveVillageListSubmitForm">确 定</el-button>
          <el-button @click="ArchiveVillageListCancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ArchiveVillageList">
import { listArchiveVillageList, getArchiveVillageList, delArchiveVillageList, addArchiveVillageList, updateArchiveVillageList } from "@/api/biz/ArchiveVillageList";
import dayjs from "dayjs";
import xeUtils from "xe-utils";

const dateRange = ref([]);

const route = useRoute();
const action = ref(null);

onMounted(() => {
  ArchiveVillageListHandleQuery();
});

const { proxy } = getCurrentInstance();
const { sys_common_status } = proxy.useDict('sys_common_status');

const importRes = ref(null);
watch(
  () => importRes,
  (val) => {
    if (val.value && val.value.code == 200) {
      ArchiveVillageListHandleQuery();
    }
  },
  { deep: true, immediate: true }
);

const ArchiveVillageListList = ref([]);
const ArchiveVillageListOpen = ref(false);
const ArchiveVillageListLoading = ref(true);
const ArchiveVillageListShowSearch = ref(true);
const ArchiveVillageListIds = ref([]);
const ArchiveVillageListSingle = ref(true);
const ArchiveVillageListMultiple = ref(true);
const ArchiveVillageListTotal = ref(0);
const ArchiveVillageListTitle = ref("");

const ArchiveVillageListData = reactive({
  ArchiveVillageListForm: {},
  ArchiveVillageListQueryParams: {
    pageNum: 1,
    pageSize: 10,
    villageCode: null,
    villageName: null,
    villageCount: null,
    villageEntrance: null,
    villageHouse: null,
    floorArea: null,
    billingArea: null,
    createDatetime: null,
    createPerson: null,
    updateDatetime: null,
    updatePerson: null,
    villageCompany: null
  },
  ArchiveVillageListRules: {
    villageCode: [
      { required: true, message: "小区编码不能为空", trigger: "blur" }
    ],
    villageName: [
      { required: true, message: "小区名字不能为空", trigger: "blur" }
    ],
    createDatetime: [
      { required: true, message: "创建时间不能为空", trigger: "blur" }
    ],
    createPerson: [
      { required: true, message: "创建人不能为空", trigger: "blur" }
    ],
    updateDatetime: [
      { required: true, message: "更新时间不能为空", trigger: "blur" }
    ],
    updatePerson: [
      { required: true, message: "更新人不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "是否启用不能为空", trigger: "change" }
    ],
  }
});

const { ArchiveVillageListQueryParams, ArchiveVillageListForm, ArchiveVillageListRules } = toRefs(ArchiveVillageListData);

watch(
  route,
  (newRoute) => {
    action.value = newRoute.query && newRoute.query.action;
  },
  { immediate: true }
);

/** 查询小区列列表 */
function ArchiveVillageListGetList() {
  ArchiveVillageListLoading.value = true;
  listArchiveVillageList(proxy.addDateRange(ArchiveVillageListQueryParams.value, dateRange.value, 'create_time')).then(response => {
    ArchiveVillageListList.value = response.rows;
    ArchiveVillageListTotal.value = response.total;
    ArchiveVillageListLoading.value = false;
  });
}

// 取消按钮
function ArchiveVillageListCancel() {
  ArchiveVillageListOpen.value = false;
  ArchiveVillageListReset();
}

// 表单重置
function ArchiveVillageListReset() {
  ArchiveVillageListForm.value = {
    id: null,
    villageCode: null,
    villageName: null,
    villageCount: null,
    villageEntrance: null,
    villageHouse: null,
    floorArea: null,
    billingArea: null,
    createDatetime: null,
    createPerson: null,
    updateDatetime: null,
    updatePerson: null,
    status: null,
    villageCompany: null
  };
  proxy.resetForm("ArchiveVillageListRef");
}

/** 搜索按钮操作 */
function ArchiveVillageListHandleQuery() {
  ArchiveVillageListQueryParams.value.pageNum = 1;
  ArchiveVillageListGetList();
}

/** 重置按钮操作 */
function ArchiveVillageListResetQuery() {
  dateRange.value = [];
  proxy.resetForm("ArchiveVillageListQueryRef");
  ArchiveVillageListHandleQuery();
}

// 多选框选中数据
function ArchiveVillageListHandleSelectionChange(selection) {
  ArchiveVillageListIds.value = selection.map(item => item.id);
  ArchiveVillageListSingle.value = selection.length != 1;
  ArchiveVillageListMultiple.value = !selection.length;
}

/** 新增按钮操作 */
function ArchiveVillageListHandleAdd() {
  ArchiveVillageListReset();
  ArchiveVillageListOpen.value = true;
  ArchiveVillageListTitle.value = "添加";
}

/** 修改按钮操作 */
function ArchiveVillageListHandleUpdate(row) {
  ArchiveVillageListReset();
  const _id = row.id || ArchiveVillageListIds.value
  getArchiveVillageList(_id).then(response => {
    ArchiveVillageListForm.value = response.data;
    ArchiveVillageListOpen.value = true;
    ArchiveVillageListTitle.value = "修改";
  });
}

/** 提交按钮 */
function ArchiveVillageListSubmitForm() {
  proxy.$refs["ArchiveVillageListRef"].validate(valid => {
    if (valid) {
        const submitForm = xeUtils.clone(ArchiveVillageListForm.value, true);
      if (submitForm.id != null) {
        updateArchiveVillageList(submitForm).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          ArchiveVillageListOpen.value = false;
          ArchiveVillageListGetList();
        });
      } else {
        addArchiveVillageList(submitForm).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          ArchiveVillageListOpen.value = false;
          ArchiveVillageListGetList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function ArchiveVillageListHandleDelete(row) {
  const _ids = row.id || ArchiveVillageListIds.value;
  proxy.$modal.confirm('是否确认删除编号为"' + _ids + '"的数据项？').then(function() {
    return delArchiveVillageList(_ids);
  }).then(() => {
    ArchiveVillageListGetList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function ArchiveVillageListHandleExport() {
  proxy.download('biz/ArchiveVillageList/export', {
    ...ArchiveVillageListQueryParams.value
  }, `小区列_${new Date().getTime()}.xlsx`)
}

/** 下载模板操作 */
function ArchiveVillageListImportTemplate() {
  proxy.download(
      "biz/ArchiveVillageList/importTemplate",{},
      `小区列_${new Date().getTime()}.xlsx`
  );
}
</script>
<style lang="scss" scoped>

</style>