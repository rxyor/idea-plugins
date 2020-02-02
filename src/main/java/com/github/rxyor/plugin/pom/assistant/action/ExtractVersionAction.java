package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.App;
import com.github.rxyor.plugin.pom.assistant.common.maven.MavenProjectUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.MavenPropertyUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.MavenUtil;
import com.github.rxyor.plugin.pom.assistant.common.util.NotificationUtil;
import com.github.rxyor.plugin.pom.assistant.common.util.PsiFileUtil;
import com.google.common.base.Preconditions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
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


    public void actionPerformed2(@NotNull AnActionEvent e) {
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        Preconditions.checkNotNull(editor, "editor can't be null");
        Preconditions.checkNotNull(psiFile, "psiFile can't be null");

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);

        if (element == null) {
            NotificationUtil.warn("Pom Assistant", "please select valid pom.xml node");
            return;
        }

        CommandProcessor.getInstance().executeCommand(e.getProject(),
            (Runnable) () -> {
                extractAndReplaceVersion(psiFile, element);
            }
            , App.GROUP_ID, App.GROUP_ID);
    }

    private void extractAndReplaceVersion(final AnActionEvent e) {
        DependencyPair dependencyPair = getMavenDomDependencyPair(e);
        MavenDomDependency mavenDomDependency = getOneMavenDomDependency(dependencyPair);

        String artifactId = mavenDomDependency.getArtifactId().getStringValue();
        String version = mavenDomDependency.getVersion().getStringValue();
        String property = artifactId + ".version";
        String placeholder = "${" + property + "}";

        final PsiFile psiFile = PsiFileUtil.getPsiFile(e);
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
        DependencyPair pair = new DependencyPair();
        pair.dependency = MavenUtil.parseMavenDomDependency(e);
        pair.managementDependency = MavenUtil.parseMavenDomManagementDependency(e);
        return pair;
    }

    private void removeDependency(DependencyPair pair) {
        if (pair.managementDependency != null && pair.dependency != null) {
            MavenDomDependency dependency = pair.dependency;
            String text = dependency.getVersion().getRawText();
        }
    }

    private void extractAndReplaceVersion(final PsiFile psiFile, final PsiElement psiElement) {
//        Preconditions.checkNotNull(psiFile, "psiFile can't be null");
//        Preconditions.checkNotNull(psiElement, "psiElement can't be null");
//
//        final XmlFile xmlFile = MavenUtil.getXmlFile(psiFile);
//        XmlDependency xmlDependency = MavenUtil.parseXmlDependency(psiElement);
//        String tag = xmlDependency.getArtifactId() + ".version";
//
//        MavenUtil.addPropertiesSubTag(xmlFile, new TagTextPair(tag, xmlDependency.getVersion()));
//        MavenUtil.updateDependencyTag(xmlFile, xmlDependency);
//        CodeStyleManager.getInstance(psiFile.getProject()).reformat(xmlFile);
//        xmlFile.getVirtualFile().refresh(true, true);
    }

    private static class DependencyPair {

        private MavenDomDependency dependency;
        private MavenDomDependency managementDependency;
    }
}
