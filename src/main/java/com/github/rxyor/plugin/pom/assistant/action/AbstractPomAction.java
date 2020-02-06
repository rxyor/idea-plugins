package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.notification.util.NotificationUtil;
import com.github.rxyor.plugin.pom.assistant.common.psi.util.PsiUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/6 周四 23:48:00
 * @since 1.0.0
 */
public abstract class AbstractPomAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        VirtualFile virtualFile = PsiUtil.getVirtualFile(e);
        boolean isMavenProjectFile = MavenActionUtil.isMavenProjectFile(virtualFile);
        if (isMavenProjectFile) {
            presentation.setEnabledAndVisible(true);
        } else {
            presentation.setVisible(false);
        }
    }

    /**
     * 检查点击文件是否有效的maven工程文件
     *
     * @param psiFile
     * @return
     */
    protected boolean checkMavenProjectFile(PsiFile psiFile) {
        if (!MavenActionUtil.isMavenProjectFile(psiFile.getVirtualFile())) {
            NotificationUtil.warn("Warn", "Click file is not valid maven project file");
            return true;
        }
        return false;
    }
}
