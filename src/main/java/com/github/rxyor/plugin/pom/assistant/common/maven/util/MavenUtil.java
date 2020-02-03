package com.github.rxyor.plugin.pom.assistant.common.maven.util;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst;
import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.PomTag;
import com.github.rxyor.plugin.pom.assistant.common.constant.ValidConst.NoNull;
import com.github.rxyor.plugin.pom.assistant.common.psi.util.PsiFileUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.psi.xml.XmlToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.model.MavenId;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/2 周日 21:43:00
 * @since 1.0.0
 */
public class MavenUtil {

    private MavenUtil() {
    }

    public static MavenDomDependency parseMavenDomDependency(AnActionEvent e) {
        Preconditions.checkNotNull(e, NoNull.AnActionEvent);

        final PsiFile psiFile = PsiFileUtil.getPsiFile(e);
        final Editor editor = PsiFileUtil.getEditor(e);
        MavenDomProjectModel projectModel = MavenProjectUtil.getMavenDomProjectModel(psiFile);
        return MavenDependencyUtil.getClickMavenDomDependency(projectModel, editor);
    }

    public static MavenDomDependency parseMavenDomManagementDependency(AnActionEvent e) {
        Preconditions.checkNotNull(e, NoNull.AnActionEvent);

        final PsiFile psiFile = PsiFileUtil.getPsiFile(e);
        final Editor editor = PsiFileUtil.getEditor(e);
        MavenDomProjectModel projectModel = MavenProjectUtil.getMavenDomProjectModel(psiFile);
        return MavenDependencyUtil.getClickDomManagementDependency(projectModel, editor);
    }

    public static MavenId parseMavenId(AnActionEvent e) {
        Preconditions.checkNotNull(e, NoNull.AnActionEvent);

        PsiElement psiElement = PsiFileUtil.getClickPsiElement(e);
        return parseMavenId(psiElement);
    }

    public static MavenId parseMavenId(PsiElement psiElement) {
        Preconditions.checkNotNull(psiElement, NoNull.PsiElement);

        XmlTag dependencyTag = findDependencyTag(psiElement);
        if (dependencyTag == null) {
            return null;
        }

        //left store xml tag,right store xml value
        List<String> mavenIdTags = Lists.newArrayList(
            PomTag.GROUP_ID, PomTag.ARTIFACT_ID, PomTag.VERSION);

        Map<String, String> map = new HashMap<>(4);
        for (String tag : mavenIdTags) {
            XmlTag xmlTag = dependencyTag.findFirstSubTag(tag);
            if (xmlTag != null) {
                map.put(tag, xmlTag.getValue().getText());
            }
        }

        if (map.size() != 0) {
            return new MavenId(map.get(PomTag.GROUP_ID),
                map.get(PomTag.ARTIFACT_ID), map.get(PomTag.VERSION));
        }
        return null;
    }

    /**
     * 是否是同一个版本号
     *
     * @param dependency
     * @param mavenId
     * @return
     */
    public static boolean isSame(MavenDomDependency dependency, MavenId mavenId) {
        if (dependency == null || mavenId == null) {
            return false;
        }

        boolean isSameGroupId = StringUtils.equals(
            dependency.getGroupId().getValue(), mavenId.getGroupId());
        boolean isSameArtifactId = StringUtils.equals(
            dependency.getArtifactId().getValue(), mavenId.getArtifactId());
        boolean isSameVersion = StringUtils.equals(
            dependency.getVersion().getValue(), mavenId.getVersion());
        return isSameGroupId && isSameArtifactId && isSameVersion;
    }

    private static XmlTag findDependencyTag(PsiElement element) {
        Preconditions.checkNotNull(element, NoNull.PsiElement);

        if (element instanceof XmlToken) {
            PsiElement p = element.getParent();
            XmlTag parentTag = null;
            if (p instanceof XmlText) {
                XmlText parent = (XmlText) p;
                XmlToken nextSibling = (XmlToken) parent.getNextSibling();
                parentTag = (XmlTag) nextSibling.getParent();

            } else if (p instanceof XmlTag) {
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
