package com.github.rxyor.plugin.pom.assistant.common.psi.util;

import com.github.rxyor.plugin.pom.assistant.common.constant.ValidConst.NoNull;
import com.google.common.base.Preconditions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/2 周日 21:50:00
 * @since 1.0.0
 */
public class PsiUtil {

    private PsiUtil() {
    }

    /**
     *get Project from AnActionEvent
     *
     * @author liuyang
     * @date 2020-02-03 周一 00:17:41
     * @param e
     * @return
     */
    public static Project getProject(@NotNull AnActionEvent e) {
        return e.getData(PlatformDataKeys.PROJECT);
    }

    /**
     *get PsiFile from AnActionEvent
     *
     * @author liuyang
     * @date 2020-02-03 周一 00:17:41
     * @param e
     * @return
     */
    public static VirtualFile getVirtualFile(@NotNull AnActionEvent e) {
        return e.getData(CommonDataKeys.VIRTUAL_FILE);
    }

    /**
     * get PsiElement where click
     *
     * @author liuyang
     * @date 2020-02-02 周日 22:30:10
     * @param e
     * @return
     */
    public static PsiElement getClickPsiElement(AnActionEvent e) {
        Preconditions.checkNotNull(e, NoNull.AnActionEvent);

        final Editor editor = getEditor(e);
        final PsiFile psiFile = getPsiFile(e);

        int offset = editor.getCaretModel().getOffset();
        return psiFile.findElementAt(offset);
    }

    /**
     *get PsiFile from AnActionEvent
     *
     * @author liuyang
     * @date 2020-02-03 周一 00:17:41
     * @param e
     * @return
     */
    public static PsiFile getPsiFile(AnActionEvent e) {
        Preconditions.checkNotNull(e, NoNull.AnActionEvent);

        return e.getData(CommonDataKeys.PSI_FILE);
    }

    /**
     *get Editor from AnActionEvent
     *
     * @author liuyang
     * @date 2020-02-03 周一 00:17:41
     * @param e
     * @return
     */
    public static Editor getEditor(AnActionEvent e) {
        Preconditions.checkNotNull(e, NoNull.AnActionEvent);

        return e.getData(CommonDataKeys.EDITOR);
    }

    /**
     *refresh butter/temp/cache
     *
     * @author liuyang
     * @date 2020-02-03 周一 02:12:05
     * @param psiFile
     * @return
     */
    public static void refresh(@NotNull PsiFile psiFile) {
        psiFile.getVirtualFile().refresh(true, false);
    }

    /**
     *reformat file
     *
     * @author liuyang
     * @date 2020-02-03 周一 02:13:37
     * @param psiFile
     * @return
     */
    public static void reformat(@NotNull PsiFile psiFile) {
        CodeStyleManager.getInstance(psiFile.getProject()).reformat(psiFile);
    }

}
