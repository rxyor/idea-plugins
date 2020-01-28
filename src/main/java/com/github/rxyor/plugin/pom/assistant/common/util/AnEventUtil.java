package com.github.rxyor.plugin.pom.assistant.common.util;

import com.google.common.base.Preconditions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiFile;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/28 周二 22:41:00
 * @since 1.0.0
 */
public class AnEventUtil {

    private AnEventUtil() {
    }

    public static PsiFile getPsiFile(AnActionEvent e) {
        checkNonnull(e);
        return e.getData(LangDataKeys.PSI_FILE);
    }

    private static void checkNonnull(AnActionEvent e) {
        Preconditions.checkNotNull(e, "action event can't be null");
    }
}
