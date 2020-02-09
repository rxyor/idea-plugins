package com.github.rxyor.plugin.pom.assistant.ui.maven;

import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.DependsSearchHelper;
import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.ListVersionHelper;
import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.Page;
import com.google.common.base.Splitter;
import com.intellij.openapi.project.Project;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
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

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4);

    private JPanel panel;
    private JTextField keywordTxt;
    private JButton searchBtn;
    private JList<String> searchRetList;
    private JScrollPane scrollPane;
    JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
    private JTextArea dependDetail;
    private JComboBox<String> versionBox;
    private JButton copyBtn;
    private JButton addBtn;
    private JButton replaceBtn;
    private DefaultListModel<String> listModel = new DefaultListModel();
    private DependsSearchHelper searcher = null;
    private MavenId clickMavenId = null;

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
        //搜索按钮点击事件
        searchBtn.addActionListener(e -> {
            THREAD_POOL.submit(() -> doSearch());
        });

        //滑条到底事件
        scrollBar.addAdjustmentListener(e -> {
            int curH = e.getValue();
            int barLen = scrollBar.getHeight();
            int listH = searchRetList.getHeight();
            if (isCloseToBottom(listH, curH, barLen)) {
                THREAD_POOL.submit(() -> nextPage());
            }
        });

        searchRetList.addListSelectionListener(e -> {
            THREAD_POOL.submit(() -> {
                String s = searchRetList.getSelectedValue();
                List<String> splitList = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(s);
                if (splitList.size() < 2) {
                    return;
                }
                clickMavenId = new MavenId(splitList.get(0), splitList.get(1), null);

                if (clickMavenId == null) {
                    return;
                }
                List<String> versions = searchAndAddToVersionComboBox(clickMavenId.getGroupId(),
                    clickMavenId.getArtifactId());
                if (!versions.isEmpty()) {
                    setToDependDetail(new MavenId(
                        clickMavenId.getGroupId(), clickMavenId.getArtifactId(), versions.get(0)));
                }
            });
        });

        versionBox.addItemListener(e -> {
            THREAD_POOL.submit(() -> {
                setToDependDetail(new MavenId(
                    clickMavenId.getGroupId(), clickMavenId.getArtifactId(), e.getItem().toString()));
            });
        });
    }

    private void doSearch() {
        searcher = DependsSearchHelper.builder().keyword(keywordTxt.getText()).build();
        listModel.clear();

        addToListModel(searcher.search());
    }

    private void nextPage() {
        if (searcher == null) {
            searcher = DependsSearchHelper.builder().keyword(keywordTxt.getText()).build();
        }
        addToListModel(searcher.nextPage());
    }

    private void addToListModel(Page<MavenId> page) {
        if (page.getTotal() <= 0) {
            return;
        }

        List<String> listData = page.getItems().stream().map(
            mavenId -> mavenId.getGroupId() + ":" + mavenId.getArtifactId())
            .collect(Collectors.toList());

        listData.forEach(s -> {
            if (!listModel.contains(s)) {
                listModel.addElement(s);
            }
        });
        refreshList();
    }


    private void refreshList() {
        searchRetList.setModel(listModel);
    }

    private boolean isCloseToBottom(int listH, int curH, int barLen) {
        if (listH == 0) {
            return true;
        }
        int dValue = listH / 5;
        if (listH - (curH + barLen) <= dValue) {
            return true;
        }
        return false;
    }

    private List<String> searchAndAddToVersionComboBox(String groupId, String artifactId) {
        List<String> list = this.searchVersionList(groupId, artifactId);
        this.addToVersionComboBox(list);
        return list;
    }

    private void addToVersionComboBox(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ListComboBoxModel<String> model = new ListComboBoxModel<>(list);
        versionBox.setModel(model);
    }

    private List<String> searchVersionList(String groupId, String artifactId) {
        List<MavenId> list = ListVersionHelper.list(groupId, artifactId);
        return list.stream().map(MavenId::getVersion).filter(StringUtils::isNotBlank)
            .distinct().collect(Collectors.toList());
    }

    private void setToDependDetail(MavenId mavenId) {
        if (mavenId == null) {
            return;
        }

        StringBuilder sb = new StringBuilder("<dependency>\n");
        if (StringUtils.isNotBlank(mavenId.getGroupId())) {
            sb.append("\t<groupId>" + mavenId.getGroupId() + "</groupId>\n");
        }
        if (StringUtils.isNotBlank(mavenId.getArtifactId())) {
            sb.append("\t<artifactId>" + mavenId.getArtifactId() + "</artifactId>\n");
        }
        if (StringUtils.isNotBlank(mavenId.getVersion())) {
            sb.append("\t<version>" + mavenId.getVersion() + "</version>\n");
        }
        sb.append("</dependency>");

        dependDetail.setText(sb.toString());
    }
}
