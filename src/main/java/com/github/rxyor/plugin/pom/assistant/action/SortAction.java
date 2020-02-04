package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.psi.util.PsiFileUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/4 周二 14:47:00
 * @since 1.0.0
 */
public class SortAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final PsiFile psiFile = PsiFileUtil.getPsiFile(e);
        Document document = this.getDocument(psiFile);
        String text = document.getText();

    }

    private Document getDocument(@NotNull PsiFile psiFile) {
        return psiFile.getViewProvider().getDocument();
    }

    protected String format(String text) {
        return null;
    }
}
