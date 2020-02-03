package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.App;
import com.github.rxyor.plugin.pom.assistant.common.maven.model.DependencyPair;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenDependencyUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenIdUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenProjectUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenPropertyUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenUtil;
import com.github.rxyor.plugin.pom.assistant.common.notification.util.NotificationUtil;
import com.github.rxyor.plugin.pom.assistant.common.psi.util.PsiFileUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.model.MavenDomDependencies;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomDependencyManagement;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/27 周一 15:31:00
 * @since 1.0.0
 */
public class ExtractVersionAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CommandProcessor.getInstance().executeCommand(e.getProject(),
            (Runnable) () -> {
                extractAndReplaceVersion(e);
            }
            , App.GROUP_ID, App.GROUP_ID);
    }

    /**
     * 提取并替换占位符
     *
     * @param e
     */
    private void extractAndReplaceVersion(final AnActionEvent e) {
        final PsiFile psiFile = PsiFileUtil.getPsiFile(e);
        if (checkMavenProjectFile(psiFile)) {
            return;
        }
        final Editor editor = PsiFileUtil.getEditor(e);
        final MavenDomProjectModel model = MavenProjectUtil
            .getMavenDomProjectModel(psiFile);

        //获取点击到依赖
        MavenDomDependency clickDependency = MavenDependencyUtil
            .getClickDependency(model, editor);
        if (checkClickDependency(clickDependency)) {
            return;
        }

        MavenId mavenId = MavenIdUtil.convert(clickDependency);
        String property = mavenId.getArtifactId() + ".version";
        String placeholder = "${" + property + "}";
        //属性标签中添加对应的属性
        MavenPropertyUtil.addOrUpdateMavenProperty(model, property, mavenId.getVersion());
        //点击依赖版本号替换为占位符
        clickDependency.getVersion().setValue(placeholder);

        DependencyPair dependencyPair = MavenDependencyUtil
            .findDependency(model, clickDependency);
        removeDependency(dependencyPair);

        //格式化并刷新文件
        PsiFileUtil.reformat(psiFile);
        PsiFileUtil.refresh(psiFile);
    }

    /**
     * 检查点击文件是否有效的maven工程文件
     *
     * @param psiFile
     * @return
     */
    private boolean checkMavenProjectFile(PsiFile psiFile) {
        if (!MavenActionUtil.isMavenProjectFile(psiFile.getVirtualFile())) {
            NotificationUtil.warn("Warn", "Click file is not valid maven project file");
            return true;
        }
        return false;
    }

    /**
     * 检查点击依赖是否有效
     *
     * @param clickDependency
     * @return
     */
    private boolean checkClickDependency(MavenDomDependency clickDependency) {
        if (clickDependency == null) {
            NotificationUtil.warn("Warn", "Click xml tag is not valid dependency tag");
            return true;
        }
        if (StringUtils.contains(clickDependency.getVersion().getValue(), "$")) {
            NotificationUtil.warn("Warn", "Click dependency has replaced by placeholder");
            return true;
        }
        return false;
    }

    /**
     * 移除版本号
     *
     * @param pair
     */
    private void removeDependency(DependencyPair pair) {
        if (pair.getManagementDependency() != null && pair.getDependency() != null) {
            MavenDependencyUtil.removeVersion(pair.getDependency());
        }
    }

    public MavenDomDependency getOneMavenDomDependency(@NotNull DependencyPair dependencyPair) {
        if (dependencyPair.getManagementDependency() != null) {
            return dependencyPair.getManagementDependency();
        }
        return dependencyPair.getDependency();
    }

    public DependencyPair getMavenDomDependencyPair(@NotNull AnActionEvent e) {
        final PsiFile psiFile = PsiFileUtil.getPsiFile(e);
        MavenId mavenId = MavenUtil.parseMavenId(e);
        DependencyPair pair = new DependencyPair();

        MavenDomProjectModel model = MavenProjectUtil.getMavenDomProjectModel(psiFile);
        List<MavenDomDependency> managementDependencyList = Optional
            .ofNullable(model.getDependencyManagement())
            .map(MavenDomDependencyManagement::getDependencies)
            .map(MavenDomDependencies::getDependencies)
            .orElse(new ArrayList<>(0));
        for (MavenDomDependency dependency : managementDependencyList) {
            if (MavenUtil.isSame(dependency, mavenId)) {
                pair.setManagementDependency(dependency);
                break;
            }
        }

        List<MavenDomDependency> dependencyList = Optional
            .ofNullable(model.getDependencies())
            .map(MavenDomDependencies::getDependencies)
            .orElse(new ArrayList<>(0));
        for (MavenDomDependency dependency : dependencyList) {
            if (MavenUtil.isSame(dependency, mavenId)) {
                pair.setDependency(dependency);
                break;
            }
        }

        return pair;
    }
}
