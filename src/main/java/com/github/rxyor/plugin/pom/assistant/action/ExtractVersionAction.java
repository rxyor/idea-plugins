package com.github.rxyor.plugin.pom.assistant.action;

import com.google.common.base.Preconditions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/27 周一 15:31:00
 * @since 1.0.0
 */
public class ExtractVersionAction extends DumbAwareAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        VirtualFile virtualFile = Optional.ofNullable(psiFile)
            .map(PsiFile::getVirtualFile).orElse(null);

        Preconditions.checkNotNull(editor, "editor can't be null");
        Preconditions.checkNotNull(psiFile, "psiFile can't be null");

        int offset = editor.getCaretModel().getOffset();
    }
}
