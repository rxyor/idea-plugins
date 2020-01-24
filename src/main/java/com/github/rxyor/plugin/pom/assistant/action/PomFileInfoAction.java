package com.github.rxyor.plugin.pom.assistant.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/24 周五 15:31:00
 * @since 1.0.0
 */
public class PomFileInfoAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        String fileName = file.getName();

        Notifications.Bus.notify(
            new Notification("xtools", "文件信息", fileName, NotificationType.INFORMATION)
        );
    }

    private PsiFile pickupCurrentFile(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            Messages.showWarningDialog("Project is empty, please open valid project", "Invalid Project");
            return null;
        }

        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            Notifications.Bus.notify(new Notification("PomAssistant", "告警", "文件无效", NotificationType.ERROR), project);
            return null;
        }

        return null;
    }
}
