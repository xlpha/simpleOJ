package com.simpleoj.judgecore.languageconfig;

public class CConfig implements LanguageConfig {
    @Override
    public String getSaveFileName() {
        return "main.c";
    }

    @Override
    public String getCompileCmd() {
        return "gcc main.c -o main";
    }

    @Override
    public String getRunCmd() {
        return "./main";
    }

    @Override
    public String getDockerImageName() {
        return "gcc:4.9";
    }
}
