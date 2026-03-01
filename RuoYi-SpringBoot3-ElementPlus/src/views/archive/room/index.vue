<template>
    <div class="app-container">
        <el-form @submit.prevent :model="roomQueryParams" ref="roomQueryRef" :inline="true" v-show="roomShowSearch" label-width="auto">
            <el-form-item label="所属小区" prop="villageId">
                <el-select v-model="roomQueryParams.villageId" placeholder="请选择所属小区" clearable filterable @change="handleSearchVillageChange">
                    <el-option v-for="item in villageList" :key="item.id" :label="item.villageName" :value="item.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="供暖状态" prop="heatingStatus">
                <el-select v-model="roomQueryParams.heatingStatus" placeholder="请选择供暖状态" clearable>
                    <el-option label="正常" :value="1" />
                    <el-option label="停供" :value="0" />
                </el-select>
            </el-form-item>
            <el-form-item label="计量方式" prop="measurementMethod">
                <el-select v-model="roomQueryParams.measurementMethod" placeholder="请选择计量方式" clearable>
                    <el-option label="按面积计费" :value="0" />
                    <el-option label="按表计费" :value="1" />
                </el-select>
            </el-form-item>
            <el-form-item label="一卡通号" prop="cardNumberLike">
                <el-input v-model="roomQueryParams.cardNumberLike" placeholder="请输入一卡通号" clearable @keyup.enter="roomHandleQuery" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="roomHandleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="roomResetQuery">重置</el-button>
            </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="Plus" @click="roomHandleAdd" v-hasPermi="['biz:room:add']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="success" plain icon="Edit" :disabled="roomSingle" @click="roomHandleUpdate" v-hasPermi="['biz:room:edit']">修改</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="danger" plain icon="Delete" :disabled="roomMultiple" @click="roomHandleDelete" v-hasPermi="['biz:room:remove']">删除</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="warning" plain icon="Download" @click="roomHandleExport" v-hasPermi="['biz:room:export']">导出</el-button>
            </el-col>
            <right-toolbar :showSearch="roomShowSearch" @queryTable="roomGetList"></right-toolbar>
        </el-row>

        <el-table v-loading="roomLoading" :data="roomList" @selection-change="roomHandleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column width="50" label="主键ID" align="center" prop="id" />
            <el-table-column label="一卡通号" align="center" prop="cardNumber" />
            <el-table-column label="所属小区" align="center" prop="villageName" />
            <el-table-column label="所属楼栋" align="center" prop="buildingName" />
            <el-table-column label="所属楼门" align="center" prop="entranceName" />
            <el-table-column label="房间号码" align="center" prop="roomNumber" />
            <el-table-column label="计费面积" align="center" prop="billingArea">
                <template #default="scope">
                    <span>{{ scope.row.billingArea?.toFixed(2) || '0.00' }}</span>
                </template>
            </el-table-column>
            <el-table-column label="供暖状态" align="center" prop="heatingStatus">
                <template #default="scope">
                    <el-tag :type="scope.row.heatingStatus === 1 ? 'success' : 'danger'">
                        {{ scope.row.heatingStatus === 1 ? '正常' : '停供' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="计量方式" align="center" prop="measurementMethod">
                <template #default="scope">
                    <el-tag :type="scope.row.measurementMethod === 1 ? 'warning' : ''">
                        {{ scope.row.measurementMethod === 1 ? '按表计费' : '按面积计费' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="过程数据" align="center" prop="processData" show-overflow-tooltip />
            <el-table-column label="当前采暖季" align="center" prop="currentSeason" />
            <el-table-column label="入网状态" align="center" prop="networkStatus">
                <template #default="scope">
                    <el-tag :type="scope.row.networkStatus === 1 ? 'success' : 'info'">
                        {{ scope.row.networkStatus === 1 ? '已入网' : '未入网' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="入网采暖季" align="center" prop="networkSeason" />
            <el-table-column width="130" label="操作" align="center" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button link type="primary" icon="Edit" @click="roomHandleUpdate(scope.row)" v-hasPermi="['biz:room:edit']">修改</el-button>
                    <el-button link type="primary" icon="Delete" @click="roomHandleDelete(scope.row)" v-hasPermi="['biz:room:remove']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <pagination v-show="roomTotal > 0" :total="roomTotal" :page="roomQueryParams.pageNum" :limit="roomQueryParams.pageSize" @pagination="roomGetList" />

        <!-- 添加或修改房间档案对话框 -->
        <el-dialog :title="roomTitle" v-model="roomOpen" width="600px" append-to-body destroy-on-close>
            <el-form @submit.prevent ref="roomRef" :model="roomForm" :rules="roomRules" label-width="auto">
                <el-form-item label="一卡通号" prop="cardNumber">
                    <el-input v-model="roomForm.cardNumber" placeholder="请输入一卡通号" />
                </el-form-item>
                <el-form-item label="所属小区" prop="villageId">
                    <el-select v-model="roomForm.villageId" placeholder="请选择所属小区" clearable filterable @change="handleFormVillageChange">
                        <el-option v-for="item in villageList" :key="item.id" :label="item.villageName" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="所属楼栋" prop="buildingId">
                    <el-select v-model="roomForm.buildingId" placeholder="请选择所属楼栋" clearable filterable :disabled="!roomForm.villageId" @change="handleFormBuildingChange">
                        <el-option v-for="item in formBuildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="所属楼门" prop="entranceId">
                    <el-select v-model="roomForm.entranceId" placeholder="请选择所属楼门" clearable filterable :disabled="!roomForm.buildingId">
                        <el-option v-for="item in formEntranceList" :key="item.id" :label="item.entranceName" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="房间号码" prop="roomNumber">
                    <el-input v-model="roomForm.roomNumber" placeholder="请输入房间号码" />
                </el-form-item>
                <el-form-item label="计费面积" prop="billingArea">
                    <el-input v-model="roomForm.billingArea" placeholder="请输入计费面积">
                        <template #append>m²</template>
                    </el-input>
                </el-form-item>
                <el-form-item label="供暖状态" prop="heatingStatus">
                    <el-select v-model="roomForm.heatingStatus" placeholder="请选择供暖状态">
                        <el-option label="正常" :value="1" />
                        <el-option label="停供" :value="0" />
                    </el-select>
                </el-form-item>
                <el-form-item label="计量方式" prop="measurementMethod">
                    <el-select v-model="roomForm.measurementMethod" placeholder="请选择计量方式">
                        <el-option label="按面积计费" :value="0" />
                        <el-option label="按表计费" :value="1" />
                    </el-select>
                </el-form-item>
                <el-form-item label="过程数据" prop="processData">
                    <el-input v-model="roomForm.processData" type="textarea" :rows="2" placeholder="请输入过程数据（如温度、压力、表显等）" />
                </el-form-item>

                <el-form-item label="当前采暖季" prop="currentSeason">
                    <el-input v-model="roomForm.currentSeason" placeholder="请输入当前采暖季，如2023-2024" />
                </el-form-item>
                <el-form-item label="入网状态" prop="networkStatus">
                    <el-select v-model="roomForm.networkStatus" placeholder="请选择入网状态">
                        <el-option label="未入网" :value="0" />
                        <el-option label="已入网" :value="1" />
                    </el-select>
                </el-form-item>
                <el-form-item label="入网采暖季" prop="networkSeason">
                    <el-input v-model="roomForm.networkSeason" placeholder="自动回填小区的入网时间" disabled />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="roomSubmitForm">确 定</el-button>
                    <el-button @click="roomCancel">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup name="Room">
import { ref, reactive, toRefs, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { listRoom, getRoom, delRoom, addRoom, updateRoom, batchDelRoom } from '@/api/biz/room';
import { listArchiveVillageList } from '@/api/biz/ArchiveVillageList';
import { listArchiveBuildingList } from '@/api/biz/ArchiveBuildingList';
import { listEntrance } from '@/api/biz/entrance';
import { getCurrentInstance } from 'vue';
import xeUtils from 'xe-utils';

const route = useRoute();
const action = ref(null);

const villageList = ref([]);
const buildingList = ref([]);
const formBuildingList = ref([]);
const entranceList = ref([]);
const formEntranceList = ref([]);

onMounted(() => {
    roomHandleQuery();
    getVillageList();
    // 初始化获取机组列表（如果有的话）
    // getEquipmentList();
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
        return Promise.resolve();
    }
    return listArchiveBuildingList({ pageNum: 1, pageSize: 9999, villageId }).then((response) => {
        buildingList.value = response.rows;
        formBuildingList.value = response.rows;
    });
}

/** 根据楼栋ID获取楼门列表 */
function getEntranceList(buildingId) {
    if (!buildingId) {
        entranceList.value = [];
        formEntranceList.value = [];
        return Promise.resolve();
    }
    return listEntrance({ pageNum: 1, pageSize: 9999, buildingId }).then((response) => {
        entranceList.value = response.rows;
        formEntranceList.value = response.rows;
    });
}

/** 搜索表单小区选择变化处理 */
function handleSearchVillageChange(villageId) {
    getBuildingList(villageId);
    roomQueryParams.value.buildingId = null;
    roomQueryParams.value.entranceId = null;
}

/** 表单小区选择变化处理 */
function handleFormVillageChange(villageId) {
    getBuildingList(villageId);
    roomForm.value.buildingId = null;
    roomForm.value.entranceId = null;
    // 清空楼门列表
    formEntranceList.value = [];
    // 这里应该查询小区的入网时间并自动回填到networkSeason字段
    // 假设小区有networkSeason字段，这里需要调用API获取小区详情
    if (villageId) {
        // 从小区列表中查找对应的小区信息
        const village = villageList.value.find((item) => item.id === villageId);
        if (village && village.networkSeason) {
            roomForm.value.networkSeason = village.networkSeason;
        } else {
            roomForm.value.networkSeason = '';
        }
    } else {
        roomForm.value.networkSeason = '';
    }
}

/** 表单楼栋选择变化处理 */
function handleFormBuildingChange(buildingId) {
    getEntranceList(buildingId);
    roomForm.value.entranceId = null;
}

const importRes = ref(null);
watch(
    () => importRes,
    (val) => {
        if (val.value && val.value.code == 200) {
            roomHandleQuery();
        }
    },
    { deep: true, immediate: true }
);

const roomList = ref([]);
const roomOpen = ref(false);
const roomLoading = ref(true);
const roomShowSearch = ref(true);
const roomIds = ref([]);
const roomSingle = ref(true);
const roomMultiple = ref(true);
const roomTotal = ref(0);
const roomTitle = ref('');

const roomData = reactive({
    roomForm: {},
    roomQueryParams: {
        pageNum: 1,
        pageSize: 10,
        cardNumberLike: null,
        villageId: null,
        buildingId: null,
        entranceId: null,
        roomNumberLike: null,
        heatingStatus: null,
        measurementMethod: null,
        networkStatus: null
    },
    roomRules: {
        cardNumber: [{ required: true, message: '一卡通号不能为空', trigger: 'blur' }],
        villageId: [{ required: true, message: '所属小区不能为空', trigger: 'change' }],
        buildingId: [{ required: true, message: '所属楼栋不能为空', trigger: 'change' }],
        entranceId: [{ required: true, message: '所属楼门不能为空', trigger: 'change' }],
        roomNumber: [{ required: true, message: '房间号码不能为空', trigger: 'blur' }],
        billingArea: [
            { required: true, message: '计费面积不能为空', trigger: 'blur' },
            { pattern: /^\d+(\.\d{1,2})?$/, message: '计费面积格式不正确，最多保留两位小数', trigger: 'blur' }
        ],
        heatingStatus: [{ required: true, message: '供暖状态不能为空', trigger: 'change' }],
        measurementMethod: [{ required: true, message: '计量方式不能为空', trigger: 'change' }],
        networkStatus: [{ required: true, message: '入网状态不能为空', trigger: 'change' }]
    }
});

const { roomQueryParams, roomForm, roomRules } = toRefs(roomData);

watch(
    route,
    (newRoute) => {
        action.value = newRoute.query && newRoute.query.action;
    },
    { immediate: true }
);

/** 查询房间档案列表 */
function roomGetList() {
    roomLoading.value = true;
    listRoom(roomQueryParams.value).then((response) => {
        roomList.value = response.rows;
        roomTotal.value = response.total;
        roomLoading.value = false;
    });
}

// 取消按钮
function roomCancel() {
    roomOpen.value = false;
    roomReset();
}

// 表单重置
function roomReset() {
    roomForm.value = {
        id: null,
        cardNumber: null,
        villageId: null,
        buildingId: null,
        entranceId: null,
        roomNumber: null,
        billingArea: null,
        heatingStatus: 1,
        measurementMethod: 0,
        processData: null,
        equipmentId: null,
        currentSeason: null,
        networkStatus: 0,
        networkSeason: null
    };
    proxy.resetForm('roomRef');
    // 重置级联选择器的数据
    formBuildingList.value = [];
    formEntranceList.value = [];
}

/** 搜索按钮操作 */
function roomHandleQuery() {
    roomQueryParams.value.pageNum = 1;
    roomGetList();
}

/** 重置按钮操作 */
function roomResetQuery() {
    proxy.resetForm('roomQueryRef');
    roomHandleQuery();
}

// 多选框选中数据
function roomHandleSelectionChange(selection) {
    roomIds.value = selection.map((item) => item.id);
    roomSingle.value = selection.length != 1;
    roomMultiple.value = !selection.length;
}

/** 新增按钮操作 */
function roomHandleAdd() {
    roomReset();
    roomOpen.value = true;
    roomTitle.value = '添加房间档案';
}

/** 修改按钮操作 */
function roomHandleUpdate(row) {
    roomReset();
    const _id = row.id || roomIds.value;
    getRoom(_id).then((response) => {
        roomForm.value = response.data;
        // 加载对应小区的楼栋列表
        if (roomForm.value.villageId) {
            getBuildingList(roomForm.value.villageId).then(() => {
                // 加载对应楼栋的楼门列表
                if (roomForm.value.buildingId) {
                    getEntranceList(roomForm.value.buildingId);
                }
            });
        }
        roomOpen.value = true;
        roomTitle.value = '修改房间档案';
    });
}

/** 提交按钮 */
function roomSubmitForm() {
    proxy.$refs['roomRef'].validate((valid) => {
        if (valid) {
            const submitForm = xeUtils.clone(roomForm.value, true);
            if (submitForm.id != null) {
                updateRoom(submitForm).then((response) => {
                    proxy.$modal.msgSuccess('修改成功');
                    roomOpen.value = false;
                    roomGetList();
                });
            } else {
                addRoom(submitForm).then((response) => {
                    proxy.$modal.msgSuccess('新增成功');
                    roomOpen.value = false;
                    roomGetList();
                });
            }
        }
    });
}

/** 删除按钮操作 */
function roomHandleDelete(row) {
    const _ids = row.id || roomIds.value;
    proxy.$modal
        .confirm('是否确认删除编号为"' + _ids + '"的数据项？')
        .then(function () {
            // 区分单条删除和批量删除
            if (Array.isArray(_ids)) {
                return batchDelRoom(_ids);
            } else {
                return delRoom(_ids);
            }
        })
        .then(() => {
            roomGetList();
            proxy.$modal.msgSuccess('删除成功');
        })
        .catch(() => {});
}

/** 批量删除按钮操作 */
function roomHandleBatchDelete() {
    if (roomIds.value.length === 0) {
        proxy.$modal.msgError('请选择要删除的数据');
        return;
    }
    proxy.$modal
        .confirm('是否确认批量删除选中的数据项？')
        .then(function () {
            return batchDelRoom(roomIds.value);
        })
        .then(() => {
            roomGetList();
            proxy.$modal.msgSuccess('删除成功');
        })
        .catch(() => {});
}

/** 导出按钮操作 */
function roomHandleExport() {
    proxy.download(
        'biz/room/export',
        {
            ...roomQueryParams.value
        },
        `房间档案_${new Date().getTime()}.xlsx`
    );
}
</script>
<style lang="scss" scoped></style>
