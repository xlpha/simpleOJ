package com.simpleoj.judgecore.languageconfig;

public interface LanguageConfig {
    String getSaveFileName();

    String getCompileCmd();

    String getRunCmd();

    String getDockerImageName();
}
