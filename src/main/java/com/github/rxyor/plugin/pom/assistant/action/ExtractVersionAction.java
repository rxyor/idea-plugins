package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.App;
import com.github.rxyor.plugin.pom.assistant.common.maven.model.DependencyPair;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenDependencyUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenProjectUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenPropertyUtil;
import com.github.rxyor.plugin.pom.assistant.common.notification.util.NotificationUtil;
import com.github.rxyor.plugin.pom.assistant.common.psi.util.PsiUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.model.MavenId;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/27 周一 15:31:00
 * @since 1.0.0
 */
public class ExtractVersionAction extends AbstractPomAction {


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
        final PsiFile psiFile = PsiUtil.getPsiFile(e);
        if (checkMavenProjectFile(psiFile)) {
            return;
        }
        final PsiElement psiElement = PsiUtil.getClickPsiElement(e);
        final MavenDomProjectModel model = MavenProjectUtil
            .getMavenDomProjectModel(psiFile);

        //获取点击到依赖
        MavenId mavenId = MavenDependencyUtil.getClickMavenId(psiElement);

        if (checkClickDependency(mavenId)) {
            return;
        }

        String property = mavenId.getArtifactId() + ".version";
        String placeholder = "${" + property + "}";
        //属性标签中添加对应的属性
        MavenPropertyUtil.addOrUpdateMavenProperty(model, property, mavenId.getVersion());

        DependencyPair dependencyPair = MavenDependencyUtil
            .findDependency(model, mavenId);

        //点击依赖版本号替换为占位符
        resetVersion(dependencyPair.getDependency(),placeholder);
        resetVersion(dependencyPair.getManagementDependency(),placeholder);

        removeDependency(dependencyPair);

        //格式化并刷新文件
        PsiUtil.reformat(psiFile);
        PsiUtil.refresh(psiFile);
    }

    /**
     * 检查点击依赖是否有效
     *
     * @param mavenId
     * @return
     */
    private boolean checkClickDependency(MavenId mavenId) {
        if (mavenId == null) {
            NotificationUtil.warn("Warn", "Click xml tag is not valid dependency tag");
            return true;
        }
        if (StringUtils.contains(mavenId.getVersion(), "$")) {
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

    /**
     * 设置版本号
     *
     * @param dependency
     * @param version
     */
    private void resetVersion(MavenDomDependency dependency, String version) {
        if (dependency != null) {
            dependency.getVersion().setValue(version);
        }
    }
}
