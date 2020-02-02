package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.App;
import com.github.rxyor.plugin.pom.assistant.common.maven.MavenProjectUtil;
import com.github.rxyor.plugin.pom.assistant.common.maven.MavenUtil;
import com.github.rxyor.plugin.pom.assistant.common.util.NotificationUtil;
import com.google.common.base.Preconditions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;

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
        MavenId mavenId = MavenUtil.parseMavenId(e);
        MavenProject mavenProject = MavenProjectUtil.getMavenProject(e.getDataContext());
        List<MavenArtifact> mavenArtifactList = mavenProject.findDependencies(mavenId);
        MavenDomDependency mavenDomDependency = MavenUtil.parseMavenDomDependency(e);
        String groupId = mavenDomDependency.getGroupId().getStringValue();
        String version = mavenDomDependency.getVersion().getStringValue();

        CommandProcessor.getInstance().executeCommand(e.getProject(),
            (Runnable) () -> {
//                extractAndReplaceVersion(psiFile, element);
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
}
