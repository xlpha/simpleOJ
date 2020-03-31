<template>
    <div>
        <el-container>
            <el-main>
                <table>
                    <tr>
                        <th>状态</th>
                        <th>
                            <div style="color: dimgray" v-if="result.status===-1 || result.status===-2">
                                <i class="el-icon-loading"/> {{statusNameTable[result.status]}}
                            </div>
                            <div style="color: seagreen" v-else-if="result.status===0">
                                <i class="el-icon-success"/> {{statusNameTable[result.status]}}
                            </div>
                            <div style="color: red" v-else>
                                <i class="el-icon-error"/> {{statusNameTable[result.status]}}
                            </div>
                        </th>
                    </tr>
                    <tr>
                        <th>使用语言</th>
                        <th>{{languageNameTable[result.language]}}</th>
                    </tr>
                    <tr>
                        <th>最长时间消耗</th>
                        <th>{{result.runtime ? result.runtime : "-"}} ms</th>
                    </tr>
                    <tr>
                        <th>最大内存消耗</th>
                        <th>{{result.memory ? result.memory : "-"}} KB</th>
                    </tr>
                </table>

                <div v-if="result.error">
                    <p>错误信息</p>
                    <el-input type="textarea" readonly autosize resize="none" v-model="result.error"></el-input>
                </div>

                <div>
                    <p>提交的代码</p>
                    <el-input type="textarea" readonly autosize resize="none" v-model="result.code"></el-input>
                </div>
            </el-main>
        </el-container>


    </div>
</template>

<script>
    export default {
        name: 'app',
        data() {
            return {
                result: {},
                statusNameTable: {
                    "-1": "等待评测中", "-2": "正在评测中", "-3": "内部错误",
                    "0": "提交通过", "1": "编译错误", "2": "运行时出错", "3": "运行超时", "4": "内存超限", "5": "答案错误"
                },
                languageNameTable: {
                    "0": "C", "1": "C++", "2": "Java", "3": "Python3"
                },
            }
        },
        methods: {},
        created() {
            const _this = this
            const postUrl = `http://localhost:8081/api/submission/${this.$route.params.id}`
            axios.get(postUrl).then(function (resp) {
                _this.result = resp.data
                console.log(resp.data)

                if (_this.result.status === -1 || _this.result.status === -2) {
                    _this.timer = setInterval(function () {
                        axios.get(postUrl).then(function (resp) {
                            _this.result = resp.data
                            if (_this.result.status !== -1 && _this.result.status !== -2) {
                                clearInterval(_this.timer)
                            }
                        })
                    }, 2000)
                }
            })
        },
        mounted() {

        }
    }
</script>