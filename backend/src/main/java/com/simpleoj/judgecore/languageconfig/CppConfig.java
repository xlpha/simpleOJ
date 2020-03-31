package com.simpleoj.judgecore.languageconfig;

public class CppConfig implements LanguageConfig{
    @Override
    public String getSaveFileName() {
        return "main.cpp";
    }

    @Override
    public String getCompileCmd() {
        return "g++ main.cpp -o main";
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
