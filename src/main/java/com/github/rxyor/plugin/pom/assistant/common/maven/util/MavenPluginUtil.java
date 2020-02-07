package com.github.rxyor.plugin.pom.assistant.common.maven.util;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst;
import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.PomTag;
import com.github.rxyor.plugin.pom.assistant.common.maven.model.PluginPair;
import com.github.rxyor.plugin.pom.assistant.common.psi.util.PsiUtil;
import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.psi.xml.XmlToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.model.MavenDomPlugin;
import org.jetbrains.idea.maven.dom.model.MavenDomPluginManagement;
import org.jetbrains.idea.maven.dom.model.MavenDomPlugins;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.model.MavenId;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/8 周六 00:35:00
 * @since 1.0.0
 */
public class MavenPluginUtil {

    private MavenPluginUtil() {
    }

    /**
     *寻找groupId、artifactId、version一致的依赖
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:10:18
     * @param model
     * @param mavenId
     * @return
     */
    public static PluginPair findPlugin(
        @NotNull final MavenDomProjectModel model,
        @NotNull final MavenId mavenId) {
        PluginPair pair = new PluginPair();
        MavenDomPlugin managementFound =
            findPlugin(model.getBuild().getPluginManagement(), mavenId);
        MavenDomPlugin found =
            findPlugin(model.getBuild().getPlugins(), mavenId);

        pair.setManagementPlugin(managementFound);
        pair.setPlugin(found);
        return pair;
    }

    /**
     *寻找groupId、artifactId、version一致的依赖
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:10:18
     * @param management
     * @param mavenId
     * @return
     */
    public static MavenDomPlugin findPlugin(
        @NotNull final MavenDomPluginManagement management,
        @NotNull final MavenId mavenId) {
        if (management.getPlugins() == null) {
            return null;
        }
        return findPlugin(management.getPlugins(), mavenId);
    }

    /**
     *寻找groupId、artifactId、version一致的依赖
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:10:18
     * @param plugins
     * @param mavenId
     * @return
     */
    public static MavenDomPlugin findPlugin(
        @NotNull final MavenDomPlugins plugins,
        @NotNull final MavenId mavenId) {
        List<MavenDomPlugin> list = plugins.getPlugins();
        if (list != null && !list.isEmpty()) {
            for (MavenDomPlugin e : list) {
                MavenId item = MavenIdUtil.convert(e);
                if (mavenId.equals(item)) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     *比较2个依赖的groupId、artifactId、version是否一致
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:08:55
     * @param d1
     * @param d2
     * @return
     */
    public static boolean equals(@NotNull MavenDomPlugin d1,
        @NotNull MavenDomPlugin d2) {
        boolean isSameGroupId = StringUtils.equals(d1.getGroupId().getValue(),
            d2.getGroupId().getValue());
        boolean isSameArtifactId = StringUtils.equals(d1.getArtifactId().getValue(),
            d2.getArtifactId().getValue());
        boolean isSameVersion = StringUtils.equals(d1.getVersion().getValue(),
            d2.getVersion().getValue());

        return isSameGroupId && isSameArtifactId && isSameVersion;
    }

    /**
     *移除版本号
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:18:57
     * @param plugin
     * @return
     */
    public static void removeVersion(@NotNull MavenDomPlugin plugin) {
        plugin.getVersion().setStringValue(null);
    }

    /**
     *获取点击的MavenId
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:48:45
     * @param e
     * @return
     */
    public static MavenId getClickMavenId(@NotNull AnActionEvent e) {
        PsiElement psiElement = PsiUtil.getClickPsiElement(e);
        return getClickMavenId(psiElement);
    }

    /**
     *获取点击的MavenId
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:48:45
     * @param psiElement
     * @return
     */
    @SuppressWarnings("all")
    public static MavenId getClickMavenId(@NotNull PsiElement psiElement) {

        XmlTag pluginTag = findPluginTag(psiElement);
        if (pluginTag == null) {
            return null;
        }

        //left store xml tag,right store xml value
        List<String> mavenIdTags = Lists.newArrayList(
            PomTag.GROUP_ID, PomTag.ARTIFACT_ID, PomTag.VERSION);

        Map<String, String> map = new HashMap<>(4);
        for (String tag : mavenIdTags) {
            XmlTag xmlTag = pluginTag.findFirstSubTag(tag);
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
     *寻找点击的plugin标签
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:45:41
     * @param element
     * @return
     */
    public static XmlTag findPluginTag(@NotNull PsiElement element) {

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
                .equalsIgnoreCase(PomTag.PLUGIN)) {
                parentTag = parentTag.getParentTag();
            }
            return parentTag;
        }
        return null;
    }
}
