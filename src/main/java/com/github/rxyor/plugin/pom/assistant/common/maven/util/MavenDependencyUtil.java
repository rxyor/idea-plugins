package com.github.rxyor.plugin.pom.assistant.common.maven.util;

import com.github.rxyor.plugin.pom.assistant.common.maven.model.DependencyPair;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.xml.XmlElement;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.dom.model.MavenDomDependencies;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomDependencyManagement;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;

/**
 *<p>
 *Copy from MavenDomUtil
 *</p>
 *
 * @author liuyang
 * @date 2020/2/3 周一 00:48:00
 * @since 1.0.0
 */
public class MavenDependencyUtil {

    private MavenDependencyUtil() {
    }

    /**
     * get dependency from <dependencies> or <dependencyManagement> where click
     *
     * @author liuyang
     * @date 2020-02-03 周一 14:44:39
     * @param model
     * @param editor
     * @return
     */
    public static MavenDomDependency getClickDependency(
        @NotNull MavenDomProjectModel model, @Nullable Editor editor) {
        MavenDomDependency dependency = getClickMavenDomDependency(model, editor);
        if (dependency == null) {
            return getClickDomManagementDependency(model, editor);
        }
        return null;
    }

    /**
     *get dependency from <dependencies> where click
     *
     * @author liuyang
     * @date 2020-02-03 周一 14:46:19
     * @param model
     * @param editor
     * @return
     */
    public static MavenDomDependency getClickMavenDomDependency(
        @NotNull MavenDomProjectModel model, @Nullable Editor editor) {
        return getClickMavenDomDependency(model.getDependencies(), editor);
    }

    /**
     *get dependency from <dependencyManagement> where click
     *
     * @author liuyang
     * @date 2020-02-03 周一 14:46:49
     * @param model
     * @param editor
     * @return
     */
    public static MavenDomDependency getClickDomManagementDependency(
        @NotNull MavenDomProjectModel model, @Nullable Editor editor) {
        MavenDomDependencyManagement management = model.getDependencyManagement();
        if (management != null) {
            return getClickMavenDomDependency(management.getDependencies(), editor);
        }
        return null;
    }

    /**
     *get dependency from MavenDomDependencies where click
     *
     * @author liuyang
     * @date 2020-02-03 周一 14:47:58
     * @param dependencies
     * @param editor
     * @return
     */
    public static MavenDomDependency getClickMavenDomDependency(
        @NotNull final MavenDomDependencies dependencies,
        @Nullable final Editor editor) {
        if (editor != null) {
            int offset = editor.getCaretModel().getOffset();

            List<MavenDomDependency> dependencyList = dependencies.getDependencies();

            for (int i = 0; i < dependencyList.size(); i++) {
                MavenDomDependency dependency = dependencyList.get(i);
                XmlElement xmlElement = dependency.getXmlElement();

                if (xmlElement != null
                    && xmlElement.getTextRange().getStartOffset() <= offset
                    && xmlElement.getTextRange().getEndOffset() >= offset
                ) {
                    return dependencyList.get(i);
                }
            }
        }
        return null;
    }

    /**
     *寻找groupId、artifactId、version一致的依赖
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:10:18
     * @param model
     * @param dependency
     * @return
     */
    public static DependencyPair findDependency(
        @NotNull final MavenDomProjectModel model,
        @NotNull final MavenDomDependency dependency) {
        DependencyPair pair = new DependencyPair();
        MavenDomDependency managementFound =
            findDependency(model.getDependencyManagement(), dependency);
        MavenDomDependency found =
            findDependency(model.getDependencies(), dependency);

        pair.setManagementDependency(managementFound);
        pair.setDependency(found);
        return pair;
    }

    /**
     *寻找groupId、artifactId、version一致的依赖
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:10:18
     * @param management
     * @param dependency
     * @return
     */
    public static MavenDomDependency findDependency(
        @NotNull final MavenDomDependencyManagement management,
        @NotNull final MavenDomDependency dependency) {
        if (management.getDependencies() == null) {
            return null;
        }
        return findDependency(management.getDependencies(), dependency);
    }

    /**
     *寻找groupId、artifactId、version一致的依赖
     *
     * @author liuyang
     * @date 2020-02-03 周一 16:10:18
     * @param dependencies
     * @param dependency
     * @return
     */
    public static MavenDomDependency findDependency(
        @NotNull final MavenDomDependencies dependencies,
        @NotNull final MavenDomDependency dependency) {
        List<MavenDomDependency> list = dependencies.getDependencies();
        if (list != null && !list.isEmpty()) {
            for (MavenDomDependency e : list) {
                if (equals(e, dependency)) {
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
    public static boolean equals(@NotNull MavenDomDependency d1,
        @NotNull MavenDomDependency d2) {
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
     * @param dependency
     * @return
     */
    public static void removeVersion(@NotNull MavenDomDependency dependency) {
        dependency.getVersion().setStringValue(null);
    }

}
