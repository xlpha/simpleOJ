<template>
    <div id="app">
        <el-container>
            <el-header>
                <div id="nav">
                    <el-menu
                            class="el-menu-demo"
                            mode="horizontal"
                            background-color="#545c64"
                            text-color="#fff"
                            active-text-color="#ffd04b">
                        <el-menu-item>
                            <router-link to="/">首页</router-link>
                        </el-menu-item>
                        <el-menu-item>
                            <router-link to="/problems">题库</router-link>
                        </el-menu-item>
                    </el-menu>
                </div>
            </el-header>
            <el-main>
                <router-view/>
            </el-main>
        </el-container>
    </div>
</template>

<script>

    export default {
        name: 'app',
        components: {}
    }
</script>

<style>
    .el-header, .el-footer {
        background-color: #B3C0D1;
        color: #333;
        text-align: center;
        line-height: 60px;
    }

    .el-main {

    }

</style>
