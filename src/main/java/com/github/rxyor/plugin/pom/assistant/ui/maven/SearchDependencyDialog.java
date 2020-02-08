package com.github.rxyor.plugin.pom.assistant.ui.maven;

import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.DependsSearchHelper;
import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.Page;
import com.intellij.openapi.project.Project;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
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

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4);

    private JPanel panel;
    private JTextField keywordTxt;
    private JButton searchBtn;
    private JList<String> searchRetList;
    private JScrollPane scrollPane;
    JScrollBar scrollBar = scrollPane.getVerticalScrollBar();

    private DefaultListModel<String> listModel = new DefaultListModel();
    private DependsSearchHelper searcher = null;

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
            THREAD_POOL.submit(() -> doSearch());
        });

        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int curH = e.getValue();
                int barLen = scrollBar.getHeight();
                int listH = searchRetList.getHeight();
                System.out.println(String.format("listH:%s, curH:%s,barLen:%s", listH, curH, barLen));
                if (isCloseToBottom(listH, curH, barLen)) {
                    System.out.println(String.format("listH-(curH+barLen)=%s", listH - (curH + barLen)));
                    THREAD_POOL.submit(() -> nextPage());
                }
            }
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
}
