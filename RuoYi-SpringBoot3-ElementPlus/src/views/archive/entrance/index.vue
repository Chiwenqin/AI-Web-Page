<template>
    <div class="app-container">
        <el-form @submit.prevent :model="entranceQueryParams" ref="entranceQueryRef" :inline="true" v-show="entranceShowSearch" label-width="auto">
            <el-form-item label="所属公司" prop="companyLike">
                <el-input v-model="entranceQueryParams.companyLike" placeholder="请输入所属公司" clearable @keyup.enter="entranceHandleQuery" />
            </el-form-item>
            <el-form-item label="所属小区" prop="villageId">
                <el-select v-model="entranceQueryParams.villageId" placeholder="请选择所属小区" clearable filterable @change="handleVillageChange">
                    <el-option v-for="item in villageList" :key="item.id" :label="item.villageName" :value="item.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="所属楼栋" prop="buildingId">
                <el-select v-model="entranceQueryParams.buildingId" placeholder="请选择所属楼栋" clearable filterable :disabled="!entranceQueryParams.villageId">
                    <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="楼门名称" prop="entranceNameLike">
                <el-input v-model="entranceQueryParams.entranceNameLike" placeholder="请输入楼门名称" clearable @keyup.enter="entranceHandleQuery" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="entranceHandleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="entranceResetQuery">重置</el-button>
            </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="Plus" @click="entranceHandleAdd" v-hasPermi="['biz:entrance:add']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="success" plain icon="Edit" :disabled="entranceSingle" @click="entranceHandleUpdate" v-hasPermi="['biz:entrance:edit']">修改</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="danger" plain icon="Delete" :disabled="entranceMultiple" @click="entranceHandleDelete" v-hasPermi="['biz:entrance:remove']">删除</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="warning" plain icon="Download" @click="entranceHandleExport" v-hasPermi="['biz:entrance:export']">导出</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="entranceShowSearch" @queryTable="entranceGetList"></right-toolbar>
        </el-row>

        <el-table v-loading="entranceLoading" :data="entranceList" @selection-change="entranceHandleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column width="50" label="主键ID" align="center" prop="id" />
            <el-table-column label="楼门编码" align="center" prop="entranceCode" />
            <el-table-column label="楼门名称" align="center" prop="entranceName" />
            <el-table-column label="所属小区" align="center" prop="villageName" />
            <el-table-column label="所属楼栋" align="center" prop="buildingName" />
            <el-table-column label="房屋数量" align="center" prop="houseCount" />
            <el-table-column label="建筑面积" align="center" prop="buildingArea">
                <template #default="scope">
                    <span>{{ scope.row.buildingArea.toFixed(2) }}</span>
                </template>
            </el-table-column>
            <el-table-column label="计费面积" align="center" prop="billingArea">
                <template #default="scope">
                    <span>{{ scope.row.billingArea.toFixed(2) }}</span>
                </template>
            </el-table-column>
            <el-table-column label="状态" align="center" prop="status">
                <template #default="scope">
                    <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                        {{ scope.row.status === 1 ? '正常' : '停用' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column width="130" label="操作" align="center" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button link type="primary" icon="Edit" @click="entranceHandleUpdate(scope.row)" v-hasPermi="['biz:entrance:edit']">修改</el-button>
                    <el-button link type="primary" icon="Delete" @click="entranceHandleDelete(scope.row)" v-hasPermi="['biz:entrance:remove']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <pagination
            v-show="entranceTotal > 0"
            :total="entranceTotal"
            v-model:page="entranceQueryParams.pageNum"
            v-model:limit="entranceQueryParams.pageSize"
            @pagination="entranceGetList"
        />

        <!-- 添加或修改楼门档案对话框 -->
        <el-dialog :title="entranceTitle" v-model="entranceOpen" width="500px" append-to-body destroy-on-close>
            <el-form @submit.prevent ref="entranceRef" :model="entranceForm" :rules="entranceRules" label-width="auto">
                <el-form-item label="楼门编码" prop="entranceCode">
                    <el-input v-model="entranceForm.entranceCode" placeholder="请输入楼门编码" />
                </el-form-item>
                <el-form-item label="楼门名称" prop="entranceName">
                    <el-input v-model="entranceForm.entranceName" placeholder="请输入楼门名称" />
                </el-form-item>
                <el-form-item label="所属小区" prop="villageId">
                    <el-select v-model="entranceForm.villageId" placeholder="请选择所属小区" clearable filterable @change="handleFormVillageChange">
                        <el-option v-for="item in villageList" :key="item.id" :label="item.villageName" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="所属楼栋" prop="buildingId">
                    <el-select v-model="entranceForm.buildingId" placeholder="请选择所属楼栋" clearable filterable :disabled="!entranceForm.villageId">
                        <el-option v-for="item in formBuildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="房屋数量" prop="houseCount">
                    <el-input v-model="entranceForm.houseCount" placeholder="请输入房屋数量" />
                </el-form-item>
                <el-form-item label="建筑面积" prop="buildingArea">
                    <el-input v-model="entranceForm.buildingArea" placeholder="请输入建筑面积" />
                </el-form-item>
                <el-form-item label="计费面积" prop="billingArea">
                    <el-input v-model="entranceForm.billingArea" placeholder="请输入计费面积" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="entranceForm.status" placeholder="请选择状态">
                        <el-option label="正常" :value="1" />
                        <el-option label="停用" :value="0" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="entranceSubmitForm">确 定</el-button>
                    <el-button @click="entranceCancel">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup name="Entrance">
import { ref, reactive, toRefs, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { listEntrance, getEntrance, delEntrance, addEntrance, updateEntrance } from '@/api/biz/entrance';
import { listArchiveVillageList } from '@/api/biz/ArchiveVillageList';
import { listArchiveBuildingList } from '@/api/biz/ArchiveBuildingList';
import { getCurrentInstance } from 'vue';
import xeUtils from 'xe-utils';

const route = useRoute();
const action = ref(null);

const villageList = ref([]);
const buildingList = ref([]);
const formBuildingList = ref([]);

onMounted(() => {
    entranceHandleQuery();
    getVillageList();
});

const { proxy } = getCurrentInstance();

/** 获取小区列表 */
function getVillageList() {
    listArchiveVillageList({ pageNum: 1, pageSize: 9999 }).then((response) => {
        villageList.value = response.rows;
    });
}

/** 根据小区ID获取楼栋列表 */
function getBuildingList(villageId) {
    if (!villageId) {
        buildingList.value = [];
        formBuildingList.value = [];
        return;
    }
    listArchiveBuildingList({ pageNum: 1, pageSize: 9999, villageId }).then((response) => {
        buildingList.value = response.rows;
        formBuildingList.value = response.rows;
    });
}

/** 小区选择变化处理 */
function handleVillageChange(villageId) {
    getBuildingList(villageId);
    entranceQueryParams.value.buildingId = null;
}

/** 表单小区选择变化处理 */
function handleFormVillageChange(villageId) {
    getBuildingList(villageId);
    entranceForm.value.buildingId = null;
}

const importRes = ref(null);
watch(
    () => importRes,
    (val) => {
        if (val.value && val.value.code == 200) {
            entranceHandleQuery();
        }
    },
    { deep: true, immediate: true }
);

const entranceList = ref([]);
const entranceOpen = ref(false);
const entranceLoading = ref(true);
const entranceShowSearch = ref(true);
const entranceIds = ref([]);
const entranceSingle = ref(true);
const entranceMultiple = ref(true);
const entranceTotal = ref(0);
const entranceTitle = ref('');

const entranceData = reactive({
    entranceForm: {},
    entranceQueryParams: {
        pageNum: 1,
        pageSize: 10,
        entranceCode: null,
        entranceName: null,
        villageId: null,
        buildingId: null,
        houseCount: null,
        buildingArea: null,
        billingArea: null,
        status: null,
        companyLike: null,
        entranceNameLike: null
    },
    entranceRules: {
        entranceCode: [{ required: true, message: '楼门编码不能为空', trigger: 'blur' }],
        entranceName: [{ required: true, message: '楼门名称不能为空', trigger: 'blur' }],
        villageId: [{ required: true, message: '所属小区不能为空', trigger: 'change' }],
        buildingId: [{ required: true, message: '所属楼栋不能为空', trigger: 'change' }],
        status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
    }
});

const { entranceQueryParams, entranceForm, entranceRules } = toRefs(entranceData);

watch(
    route,
    (newRoute) => {
        action.value = newRoute.query && newRoute.query.action;
    },
    { immediate: true }
);

/** 查询楼门档案列表 */
function entranceGetList() {
    entranceLoading.value = true;
    listEntrance(entranceQueryParams.value).then((response) => {
        entranceList.value = response.rows;
        entranceTotal.value = response.total;
        entranceLoading.value = false;
    });
}

// 取消按钮
function entranceCancel() {
    entranceOpen.value = false;
    entranceReset();
}

// 表单重置
function entranceReset() {
    entranceForm.value = {
        id: null,
        entranceCode: null,
        entranceName: null,
        villageId: null,
        buildingId: null,
        houseCount: null,
        buildingArea: null,
        billingArea: null,
        status: null
    };
    proxy.resetForm('entranceRef');
}

/** 搜索按钮操作 */
function entranceHandleQuery() {
    entranceQueryParams.value.pageNum = 1;
    entranceGetList();
}

/** 重置按钮操作 */
function entranceResetQuery() {
    proxy.resetForm('entranceQueryRef');
    entranceHandleQuery();
}

// 多选框选中数据
function entranceHandleSelectionChange(selection) {
    entranceIds.value = selection.map((item) => item.id);
    entranceSingle.value = selection.length != 1;
    entranceMultiple.value = !selection.length;
}

/** 新增按钮操作 */
function entranceHandleAdd() {
    entranceReset();
    entranceOpen.value = true;
    entranceTitle.value = '添加';
}

/** 修改按钮操作 */
function entranceHandleUpdate(row) {
    entranceReset();
    const _id = row.id || entranceIds.value;
    getEntrance(_id).then((response) => {
        entranceForm.value = response.data;
        // 加载对应小区的楼栋列表
        if (entranceForm.value.villageId) {
            getBuildingList(entranceForm.value.villageId);
        }
        entranceOpen.value = true;
        entranceTitle.value = '修改';
    });
}

/** 提交按钮 */
function entranceSubmitForm() {
    proxy.$refs['entranceRef'].validate((valid) => {
        if (valid) {
            const submitForm = xeUtils.clone(entranceForm.value, true);
            if (submitForm.id != null) {
                updateEntrance(submitForm).then((response) => {
                    proxy.$modal.msgSuccess('修改成功');
                    entranceOpen.value = false;
                    entranceGetList();
                });
            } else {
                addEntrance(submitForm).then((response) => {
                    proxy.$modal.msgSuccess('新增成功');
                    entranceOpen.value = false;
                    entranceGetList();
                });
            }
        }
    });
}

/** 删除按钮操作 */
function entranceHandleDelete(row) {
    const _ids = row.id || entranceIds.value;
    proxy.$modal
        .confirm('是否确认删除编号为"' + _ids + '"的数据项？')
        .then(function () {
            return delEntrance(_ids);
        })
        .then(() => {
            entranceGetList();
            proxy.$modal.msgSuccess('删除成功');
        })
        .catch(() => {});
}

/** 导出按钮操作 */
function entranceHandleExport() {
    proxy.download(
        'biz/entrance/export',
        {
            ...entranceQueryParams.value
        },
        `楼门档案_${new Date().getTime()}.xlsx`
    );
}
</script>
<style lang="scss" scoped></style>
