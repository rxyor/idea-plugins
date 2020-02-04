package com.github.rxyor.plugin.pom.assistant.common.constant;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/2 周日 21:53:00
 * @since 1.0.0
 */
public interface ValidConst {

    interface NoNull {

        String Project = "Project";
        String AnActionEvent = "AnActionEvent can't be null";
        String DataContext = "DataContext can't be null";
        String PsiFile = "PsiFile can't be null";
        String PsiElement = "PsiElement can't be null";

        String MavenDomProjectModel = "MavenDomProjectModel";
    }
}
