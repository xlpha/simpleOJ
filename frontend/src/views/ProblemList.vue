<template>
    <div>
        <el-table :data="tableData" @row-click="handleRowChange" style="width: 100%">
            <el-table-column prop="id" label="#" width="180">
            </el-table-column>
            <el-table-column prop="title" label="标题" width="180">
            </el-table-column>
            <el-table-column prop="level" label="难度">
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
    export default {
        name: 'app',
        data() {
            return {
                tableData: []
            }
        },
        methods: {
            handleRowChange(row, event, column) {
                this.$router.push({name: "ProblemDetail", params:{id: row.id}})
            }
        },
        created() {
            const _this = this
            axios.get("http://localhost:8081/api/problem").then(function (resp) {
                _this.tableData = resp.data
            })
        },

    }
</script>