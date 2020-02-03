package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.App;
import com.github.rxyor.plugin.pom.assistant.common.maven.MavenProjectUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.MavenPropertyUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.MavenUtil;
import com.github.rxyor.plugin.pom.assistant.common.util.PsiFileUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiFile;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.model.MavenDomDependencies;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomDependencyManagement;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;

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
//                extractAndReplaceVersion(psiFile, element);
                extractAndReplaceVersion(e);
            }
            , App.GROUP_ID, App.GROUP_ID);
    }

    private void extractAndReplaceVersion(final AnActionEvent e) {
        final PsiFile psiFile = PsiFileUtil.getPsiFile(e);

        DependencyPair dependencyPair = getMavenDomDependencyPair(e);
        MavenDomDependency mavenDomDependency = getOneMavenDomDependency(dependencyPair);

        String artifactId = mavenDomDependency.getArtifactId().getStringValue();
        String version = mavenDomDependency.getVersion().getStringValue();
        String property = artifactId + ".version";
        String placeholder = "${" + property + "}";

        MavenDomProjectModel model = MavenProjectUtil.getMavenDomProjectModel(psiFile);
        MavenPropertyUtil.addOrUpdateMavenProperty(model, property, version);
        mavenDomDependency.getVersion().setStringValue(placeholder);
        removeDependency(dependencyPair);
        PsiFileUtil.reformat(psiFile);
        PsiFileUtil.refresh(psiFile);
    }

    public MavenDomDependency getOneMavenDomDependency(@NotNull DependencyPair dependencyPair) {
        if (dependencyPair.managementDependency != null) {
            return dependencyPair.managementDependency;
        }
        return dependencyPair.dependency;
    }

    public DependencyPair getMavenDomDependencyPair(@NotNull AnActionEvent e) {
        final PsiFile psiFile = PsiFileUtil.getPsiFile(e);

        MavenDomProjectModel model = MavenProjectUtil.getMavenDomProjectModel(psiFile);
        MavenDomDependencies managementDependencies = Optional
            .ofNullable(model.getDependencyManagement())
            .map(MavenDomDependencyManagement::getDependencies)
            .orElse(null);

        DependencyPair pair = new DependencyPair();
        pair.dependency = MavenUtil.parseMavenDomDependency(e);
        return pair;
    }

    private void removeDependency(DependencyPair pair) {
        if (pair.managementDependency != null && pair.dependency != null) {
            MavenDomDependency dependency = pair.dependency;
            String text = dependency.getVersion().getRawText();
        }
    }

    private static class DependencyPair {

        private MavenDomDependency dependency;
        private MavenDomDependency managementDependency;
    }
}
