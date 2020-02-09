package com.github.rxyor.plugin.pom.assistant.ui.maven;

import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.DependsSearchHelper;
import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.ListVersionHelper;
import com.github.rxyor.plugin.pom.assistant.common.jsoup.parse.Page;
import com.google.common.base.Splitter;
import com.intellij.openapi.project.Project;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
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

    /**
     * 初始化
     */
    protected void init() {
        this.initViewBindListener();
    }

    /**
     * 视图绑定监听器
     */
    private void initViewBindListener() {
        //搜索按钮点击事件
        searchBtn.addActionListener(e -> {
            THREAD_POOL.submit(() -> doSearch());
        });

        //滑条到底事件
        scrollBar.addAdjustmentListener(e -> {
            THREAD_POOL.submit(() -> {
                handleScrollBottomEvent(e);
            });
        });

        //点击搜索列表事件
        searchRetList.addListSelectionListener(e -> {
            THREAD_POOL.submit(() -> {
                handleClickSearchItemEvent();
            });
        });

        //版本选择框选中改变事件
        versionBox.addItemListener(e -> {
            THREAD_POOL.submit(() -> {
                handleSelectVersionEvent(e);
            });
        });
    }

    /**
     * 处理列表滑动底部事件
     *
     * @param e
     */
    private void handleScrollBottomEvent(AdjustmentEvent e) {
        int curH = e.getValue();
        int barLen = scrollBar.getHeight();
        int listH = searchRetList.getHeight();
        if (isCloseToBottom(listH, curH, barLen)) {
            nextPage();
        }
    }

    /**
     * 处理列表项点击事件
     */
    private void handleClickSearchItemEvent() {
        String s = searchRetList.getSelectedValue();
        clickMavenId = parseMavenId(s);

        if (clickMavenId == null) {
            clearDependDetailAndVersionListUI();
            return;
        }

        //查询点击的依赖的所有版本号
        List<String> versions = searchAndAddToVersionComboBoxUI(clickMavenId.getGroupId(),
            clickMavenId.getArtifactId());
        if (!versions.isEmpty()) {
            //填充到展示面板里
            setToDependDetail(new MavenId(
                clickMavenId.getGroupId(), clickMavenId.getArtifactId(), versions.get(0)));
        } else {
            clearDependDetailAndVersionListUI();
        }
    }

    /**
     * 处理选择版本事件
     *
     * @param e
     */
    private void handleSelectVersionEvent(ItemEvent e) {
        if (ItemEvent.SELECTED == e.getStateChange()) {
            setToDependDetail(new MavenId(
                clickMavenId.getGroupId(), clickMavenId.getArtifactId(), e.getItem().toString()));
        }
    }

    /**
     *发起网络请求搜索依赖列表
     *
     * @author liuyang
     * @date 2020-02-09 周日 15:08:32
     * @return
     */
    private void doSearch() {
        MavenId mavenId = parseMavenId(keywordTxt.getText());
        Page page;
        //如果是com.alibaba:fastjson这种唯一标识的关键字，不用模糊搜索
        if (mavenId != null) {
            List<MavenId> list = new ArrayList<>(1);
            list.add(mavenId);
            page = new Page<>(1, 10, 1, list);
        } else {
            searcher = DependsSearchHelper.builder().keyword(keywordTxt.getText()).build();
            page = searcher.search();
        }
        listModel.clear();

        this.addToSearchListUI(page);
    }

    /**
     *发起网络请求搜索下一页
     *
     * @author liuyang
     * @date 2020-02-09 周日 15:17:38
     * @return
     */
    private void nextPage() {
        if (searcher == null) {
            return;
        }
        this.addToSearchListUI(searcher.nextPage());
    }

    /**
     *将数据添加到搜索列表当中
     *
     * @author liuyang
     * @date 2020-02-09 周日 15:18:11
     * @param page page
     * @return
     */
    private void addToSearchListUI(Page<MavenId> page) {
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
        refreshSearchListUI();
    }

    /**
     *刷新搜索列表
     *
     * @author liuyang
     * @date 2020-02-09 周日 15:20:48
     * @return
     */
    private void refreshSearchListUI() {
        searchRetList.setModel(listModel);
    }

    /**
     *滑条是否接近底部了
     *
     * @author liuyang
     * @date 2020-02-09 周日 15:21:41
     * @param listH listH
     * @param curH curH
     * @param barLen barLen
     * @return
     */
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

    /**
     *搜索特定的groupId,artifactId 并把版本号填充到下拉列表
     *
     * @author liuyang
     * @date 2020-02-09 周日 15:22:23
     * @param groupId groupId
     * @param artifactId artifactId
     * @return
     */
    private List<String> searchAndAddToVersionComboBoxUI(String groupId, String artifactId) {
        List<String> list = this.searchVersionList(groupId, artifactId);
        this.refreshVersionComboBoxUI(list);
        return list;
    }

    /**
     * 发起网络请求搜索搜索特定的groupId,artifactId的所有版本号
     *
     * @param groupId
     * @param artifactId
     * @return
     */
    private List<String> searchVersionList(String groupId, String artifactId) {
        List<MavenId> list = ListVersionHelper.list(groupId, artifactId);
        return list.stream().map(MavenId::getVersion).filter(StringUtils::isNotBlank)
            .distinct().collect(Collectors.toList());
    }

    /**
     * 刷新版本号下拉列表
     *
     * @param list
     */
    private void refreshVersionComboBoxUI(List<String> list) {
        if (list == null) {
            list = new ArrayList<>(0);
        }
        ListComboBoxModel<String> model = new ListComboBoxModel<>(list);
        versionBox.setModel(model);
    }

    /**
     * 设置依赖面板的内容
     *
     * @param mavenId
     */
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

    /**
     * 清空依赖面板的内容以及版本列表
     */
    private void clearDependDetailAndVersionListUI() {
        dependDetail.setText("");
        ListComboBoxModel<String> model = new ListComboBoxModel<>(new ArrayList<>(0));
        versionBox.setModel(model);
    }

    /**
     * 解析字符串为MavenId
     *
     * @param s
     * @return
     */
    private MavenId parseMavenId(String s) {
        List<String> splitList = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(s);
        if (splitList.size() < 2) {
            return null;
        }
        return new MavenId(splitList.get(0), splitList.get(1), null);
    }
}
