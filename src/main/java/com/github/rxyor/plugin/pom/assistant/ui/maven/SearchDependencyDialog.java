package com.github.rxyor.plugin.pom.assistant.ui.maven;

import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.DependsSearchHelper;
import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.Page;
import com.github.rxyor.plugin.pom.assistant.common.notification.util.NotificationUtil;
import com.intellij.openapi.project.Project;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenId;

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
    private JList<String> searchRetList;

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

        DependsSearchHelper searcher = DependsSearchHelper.builder()
            .keyword(keywordTxt.getText()).build();
        Page<MavenId> page = searcher.search();
        if (page.getTotal() <= 0) {
            return;
        }

        List<String> listData = page.getItems().stream().map(
            mavenId -> mavenId.getGroupId() + ":" + mavenId.getArtifactId())
            .collect(Collectors.toList());

        page.getItems().forEach(mavenId -> {
            searchRetList.setListData(new Vector<>(listData));
        });

    }
}
