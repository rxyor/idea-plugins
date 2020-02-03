package com.github.rxyor.plugin.pom.assistant.common.maven;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.xml.XmlElement;
import java.util.List;
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
     * get dependency from <dependencies> or <dependencyManagement>
     *
     * @author liuyang
     * @date 2020-02-03 周一 14:44:39
     * @param model
     * @param editor
     * @return
     */
    public static MavenDomDependency getDependency(MavenDomProjectModel model,
        @Nullable Editor editor) {
        MavenDomDependency dependency = getMavenDomDependency(model, editor);
        if (dependency == null) {
            return getDomManagementDependency(model, editor);
        }
        return null;
    }

    /**
     *get dependency from <dependencies>
     *
     * @author liuyang
     * @date 2020-02-03 周一 14:46:19
     * @param model
     * @param editor
     * @return
     */
    public static MavenDomDependency getMavenDomDependency(MavenDomProjectModel model,
        @Nullable Editor editor) {
        return getMavenDomDependency(model.getDependencies(), editor);
    }

    /**
     *get dependency from <dependencyManagement>
     *
     * @author liuyang
     * @date 2020-02-03 周一 14:46:49
     * @param model
     * @param editor
     * @return
     */
    public static MavenDomDependency getDomManagementDependency(MavenDomProjectModel model,
        @Nullable Editor editor) {
        MavenDomDependencyManagement management = model.getDependencyManagement();
        if (management != null) {
            return getMavenDomDependency(management.getDependencies(), editor);
        }
        return null;
    }

    /**
     *get dependency from MavenDomDependencies
     *
     * @author liuyang
     * @date 2020-02-03 周一 14:47:58
     * @param dependencies
     * @param editor
     * @return
     */
    public static MavenDomDependency getMavenDomDependency(@NotNull final MavenDomDependencies dependencies,
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

}
