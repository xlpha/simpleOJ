<template>
    <div>
        <el-container>

            <el-main>

                <p style="color: dimgray" v-if="this.result.status===-1">
                    <i class="el-icon-loading"></i> 排队中
                </p>
                <p style="color: seagreen" v-else-if="this.result.status===0">
                    <i class="el-icon-success"></i> 已通过
                </p>
                <p style="color: red" v-else-if="this.result.status===5">
                    <i class="el-icon-error"></i> 答案错误
                </p>
            </el-main>
        </el-container>


    </div>
</template>

<script>
    export default {
        name: 'app',
        data() {
            return {
                result: {

                }
            }
        },
        methods: {},
        created() {
            const _this = this
            const postUrl = `http://localhost:8081/api/submission/${this.$route.params.id}`
            axios.get(postUrl).then(function (resp) {
                _this.result = resp.data
                console.log(resp.data)

                if (_this.result.status === -1) {
                    _this.timer = setInterval(function () {
                        axios.get(postUrl).then(function (resp) {
                            _this.result = resp.data
                            if (_this.result.status !== -1) {
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