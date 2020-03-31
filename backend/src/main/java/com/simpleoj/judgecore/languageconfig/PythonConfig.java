package com.simpleoj.judgecore.languageconfig;

public class PythonConfig implements LanguageConfig {
    @Override
    public String getSaveFileName() {
        return "main.py";
    }

    @Override
    public String getCompileCmd() {
        return null;
    }

    @Override
    public String getRunCmd() {
        return "python main.py";
    }

    @Override
    public String getDockerImageName() {
        return "python:3.7-slim";
    }
}
