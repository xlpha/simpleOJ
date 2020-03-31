package com.simpleoj.judgecore.languageconfig;

public class JavaConfig implements LanguageConfig{
    @Override
    public String getSaveFileName() {
        return "Main.java";
    }

    @Override
    public String getCompileCmd() {
        return "javac Main.java";
    }

    @Override
    public String getRunCmd() {
        return "java Main";
    }

    @Override
    public String getDockerImageName() {
        return "openjdk:8";
    }
}
