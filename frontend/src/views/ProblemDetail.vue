<template>
    <div>
        <h2>
            {{problemData.title}}
        </h2>
        <div v-html="compiledMarkdown"></div>
        <div style="margin: 20px 0;"></div>
        <div>
            <el-select v-model="form.language" placeholder="请选择编程语音">
                <el-option
                        v-for="item in languageOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                </el-option>
            </el-select>

            <div style="margin: 20px 0;"></div>
            <el-input
                    type="textarea"
                    :autosize="{ minRows: 10, maxRows: 100}"
                    placeholder="请输入内容"
                    v-model="form.code">
            </el-input>
            <div style="margin: 20px 0;"></div>
            <el-button type="primary" @click="onSubmit">提交代码</el-button>
        </div>
    </div>
</template>

<script>
    let marked = require("marked")
    export default {
        name: 'app',
        data() {
            return {
                problemData: "",
                languageOptions:[{label: "C", value: 0}, {label: "C++", value: 1},{label: "Java", value: 2},{label: "Python3", value: 3}],
                form: {
                    userId: 1,
                    problemId: this.$route.params.id,
                    language: null,
                    code: "",
                },
            }
        },
        methods: {
            onSubmit(){
                if (this.form.language == null || this.form.code === "") return
                console.log(this.form)
                const _this = this
                axios.post("http://localhost:8081/api/submission", this.form).then(function (resp) {
                    _this.$router.push({name: "SubmissionDetail", params:{id: resp.data.id}})
                })
            },
        },
        created() {
            const _this = this
            const id = this.$route.params.id;
            axios.get(`http://localhost:8081/api/problem/${id}`).then(function (resp) {
                _this.problemData = resp.data
            })
        },
        computed:{
            compiledMarkdown () {
                return marked(this.problemData.description)
            }
        },
    }
</script>