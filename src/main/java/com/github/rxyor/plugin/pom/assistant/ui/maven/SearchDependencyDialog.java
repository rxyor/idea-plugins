package com.github.rxyor.plugin.pom.assistant.ui.maven;

import com.github.rxyor.plugin.pom.assistant.common.notification.util.NotificationUtil;
import com.intellij.openapi.project.Project;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/8 周六 12:55:00
 * @since 1.0.0
 */
public class SearchDependencyDialog extends BaseDialog {

    private JPanel panel;
    private JTextField keywordTxt;
    private JButton searchBtn;

    public SearchDependencyDialog(@NotNull Project project) {
        super(project);
        super.setContentPane(panel);
        this.init();
    }

    @Override
    protected void createUIComponents() {
    }

    protected void init() {
        this.initViewBindListener();
    }

    private void initViewBindListener() {
        searchBtn.addActionListener(e -> {
            doSearch();
        });
    }

    private void doSearch() {
        NotificationUtil.info("Click Event", keywordTxt.getText());
    }
}
