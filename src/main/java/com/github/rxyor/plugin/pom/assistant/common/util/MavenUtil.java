package com.github.rxyor.plugin.pom.assistant.common.util;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst;
import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.File.SpecificFile;
import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.PomTag;
import com.github.rxyor.plugin.pom.assistant.common.model.TagTextPair;
import com.github.rxyor.plugin.pom.assistant.common.model.XmlDependency;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.psi.xml.XmlToken;
import java.util.ArrayList;
import java.util.List;
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

    private final static List<String> VALID_DEPENDENCY_SUB_TAGS = Lists.newArrayList(
        PomTag.GROUP_ID, PomTag.ARTIFACT_ID, PomTag.VERSION);

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

        findTargetElement(element);
        if (element instanceof XmlToken) {
            PsiFile psiFile = element.getContainingFile();
            XmlFile xmlFile = getXmlFile(psiFile);
            XmlTag rootTag = xmlFile.getDocument().getRootTag();
            XmlTag[] xmlTags = rootTag.getSubTags();
            Project project = psiFile.getProject();
            ASTNode node = element.getNode();
            System.out.println(node);
        }
        XmlFile xmlFile;
        return findTargetElement(element);
    }

    public static XmlFile getXmlFile(PsiFile psiFile) {
        Preconditions.checkNotNull(psiFile, "psiFile can't be null");

        PsiFile xmlFile = psiFile.getViewProvider().getPsi(XMLLanguage.INSTANCE);
        if (xmlFile == null) {
            throw new IllegalArgumentException("xml file not exists");
        }
        if (!(xmlFile instanceof XmlFile)) {
            throw new IllegalArgumentException(String.format("[%s] is not valid xml file", psiFile.getName()));
        } else {
            return (XmlFile) xmlFile;
        }
    }

    public static void addPropertiesSubTag(XmlFile xmlFile){
        Preconditions.checkNotNull(xmlFile, "xmlFile can't be bu null");
    }

    private static XmlDependency findTargetElement(PsiElement element) {

        List<TagTextPair> tagTextPairList = new ArrayList<>(8);

        XmlTag dependencyTag = findDependencyTag(element);
        if (dependencyTag == null) {
            return null;
        }
        for (String tag : VALID_DEPENDENCY_SUB_TAGS) {
            XmlTag firstTag = dependencyTag.findFirstSubTag(tag);
            if (firstTag != null) {
                tagTextPairList.add(new TagTextPair(tag, firstTag.getValue().getText()));
            }
        }

        final XmlDependency xmlDependency = new XmlDependency();
        tagTextPairList.forEach(o -> {
            if (PomTag.GROUP_ID.equalsIgnoreCase(o.getTag())) {
                xmlDependency.setGroupId(new TagTextPair(o.getTag(), o.getValue()));
            } else if (PomTag.ARTIFACT_ID.equalsIgnoreCase(o.getTag())) {
                xmlDependency.setArtifactId(new TagTextPair(o.getTag(), o.getValue()));
            } else if (PomTag.VERSION.equalsIgnoreCase(o.getTag())) {
                xmlDependency.setVersion(new TagTextPair(o.getTag(), o.getValue()));
            }
        });

        return xmlDependency;
    }

    private static XmlTag findDependencyTag(PsiElement element) {
        Preconditions.checkNotNull(element, "psiElement can't  be null");

        if (element instanceof XmlToken) {
            PsiElement p = element.getParent();
            XmlTag parentTag = null;
            if (p instanceof XmlText) {
                XmlText parent = (XmlText) p;
                XmlToken nextSibling = (XmlToken) parent.getNextSibling();
                 parentTag = (XmlTag) nextSibling.getParent();

            }else if(p instanceof XmlTag){
                parentTag = (XmlTag) p;
            }

            while (parentTag != null
                && !parentTag.toString().replaceAll(PluginConst.XmlTag.PREFIX, "")
                .equalsIgnoreCase(PomTag.DEPENDENCY)) {
                parentTag = parentTag.getParentTag();
            }
            return parentTag;
        }
        return null;
    }

}
