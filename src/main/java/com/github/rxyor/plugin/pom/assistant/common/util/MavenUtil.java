package com.github.rxyor.plugin.pom.assistant.common.util;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.File.SpecificFile;
import com.github.rxyor.plugin.pom.assistant.common.model.XmlDependency;
import com.google.common.base.Preconditions;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlToken;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/27 周一 15:54:00
 * @since 1.0.0
 */
public class MavenUtil {

    public static MavenProject getMavenProject(Project project, VirtualFile file) {
        Preconditions.checkNotNull(project, "project can't be null");
        Preconditions.checkNotNull(file, "file can't be null");

        return MavenProjectsManager.getInstance(project).findProject(file);
    }

    public static boolean isPomXml(PsiFile psiFile) {
        if (psiFile == null) {
            return false;
        }

        if (psiFile == null
            || !SpecificFile.POM.equalsIgnoreCase(psiFile.getName())
            || !StdFileTypes.XML.equals(psiFile.getFileType())
            || !(psiFile instanceof XmlFile)) {
            return false;
        }

        return true;
    }

    public static XmlDependency parseXmlDependency(PsiElement element) {
        Preconditions.checkNotNull(element, "psiElement can't be null");
        if (!isPomXml(element.getContainingFile())) {
            NotificationUtil.warn("Warn", "file is not valid pom.xml");
            throw new IllegalArgumentException("file is not valid pom.xml");
        }

        if (element instanceof XmlToken) {
            PsiFile psiFile = element.getContainingFile();
            Project project = psiFile.getProject();
            ASTNode node = element.getNode();
            System.out.println(node);
        }
        XmlFile xmlFile;
        return new XmlDependency();
    }

}
