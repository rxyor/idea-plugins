package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.App;
import com.github.rxyor.plugin.pom.assistant.common.maven.model.DependencyPair;
import com.github.rxyor.plugin.pom.assistant.common.maven.model.PluginPair;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenDependencyUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenIdUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenPluginUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenProjectUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenPropertyUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenUtil.TagType;
import com.github.rxyor.plugin.pom.assistant.common.notification.util.NotificationUtil;
import com.github.rxyor.plugin.pom.assistant.common.psi.util.PsiUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomPlugin;
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

        TagType tagType = MavenUtil.findClickParentTagType(psiElement);
        if (tagType == null) {
            return;
        }
        //获取点击到依赖
        MavenId mavenId = MavenIdUtil.getClickMavenId(psiElement);

        if (checkClickDependency(mavenId)) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(PsiUtil.getProject(e), () -> {
            String property = mavenId.getArtifactId() + ".version";
            String placeholder = "${" + property + "}";
            //属性标签中添加对应的属性
            MavenPropertyUtil.addOrUpdateMavenProperty(model, property, mavenId.getVersion());

            if (TagType.dependency.equals(tagType)) {
                replaceAndRemoveForDependency(model, mavenId, placeholder);
            } else if (TagType.plugin.equals(tagType)) {
                replaceAndRemoveForPlugin(model, mavenId, placeholder);
            }

            //格式化并刷新文件
            PsiUtil.reformat(psiFile);
            PsiUtil.refresh(psiFile);
        });
    }

    private void replaceAndRemoveForDependency(MavenDomProjectModel model, MavenId mavenId, String placeholder) {
        DependencyPair dependencyPair = MavenDependencyUtil
            .findDependency(model, mavenId);

        //点击依赖版本号替换为占位符
        resetVersion(dependencyPair.getDependency(), placeholder);
        resetVersion(dependencyPair.getManagementDependency(), placeholder);

        removeVersion(dependencyPair);
    }

    private void replaceAndRemoveForPlugin(MavenDomProjectModel model, MavenId mavenId, String placeholder) {
        PluginPair pluginPair = MavenPluginUtil
            .findPlugin(model, mavenId);

        //点击依赖版本号替换为占位符
        resetVersion(pluginPair.getPlugin(), placeholder);
        resetVersion(pluginPair.getManagementPlugin(), placeholder);

        removeVersion(pluginPair);
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
    private void removeVersion(DependencyPair pair) {
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

    /**
     * 移除版本号
     *
     * @param pair
     */
    private void removeVersion(PluginPair pair) {
        if (pair.getManagementPlugin() != null && pair.getPlugin() != null) {
            MavenPluginUtil.removeVersion(pair.getPlugin());
        }
    }

    /**
     * 设置版本号
     *
     * @param plugin
     * @param version
     */
    private void resetVersion(MavenDomPlugin plugin, String version) {
        if (plugin != null) {
            plugin.getVersion().setValue(version);
        }
    }
}
