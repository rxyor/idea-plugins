package com.github.rxyor.plugin.pom.assistant.ui.maven;

import com.github.rxyor.plugin.pom.assistant.common.psi.util.PsiUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import java.awt.Dimension;
import javax.swing.JDialog;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/8 周六 15:39:00
 * @since 1.0.0
 */
public abstract class BaseDialog extends JDialog {

    protected final AnActionEvent e;
    protected final Project project;

    public BaseDialog(@NotNull AnActionEvent e) {
        this.e = e;
        this.project = PsiUtil.getProject(e);
    }

    /**
     *弹出Dialog
     *
     * @author liuyang
     * @date 2020-02-08 周六 15:58:27
     * @param title title
     * @param minSize minSize
     * @param maxSize maxSize
     * @return
     */
    public void popup(String title, Dimension minSize, Dimension maxSize) {
        if (StringUtils.isNotBlank(title)) {
            super.setTitle(title);
        }
        if (minSize != null) {
            super.setMinimumSize(minSize);
        }
        if (maxSize != null) {
            super.setMaximumSize(maxSize);
        }
        if (maxSize == null && minSize == null) {
            super.pack();
        }
        //两个屏幕处理出现问题，跳到主屏幕去了 https://blog.csdn.net/weixin_33919941/article/details/88129513
        super.setLocationRelativeTo(WindowManager.getInstance().getFrame(project));
        super.setVisible(true);
    }


    public void close() {
        super.dispose();
    }

    protected abstract void createUIComponents();
}
