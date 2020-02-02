package com.github.rxyor.plugin.pom.assistant.common.maven;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.xml.XmlElement;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.dom.model.MavenDomDependencies;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
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

    @NotNull
    public static MavenDomDependency getDomDependency(MavenDomProjectModel model,
        @Nullable Editor editor) {
        return getDomDependency(model.getDependencies(), editor);
    }

    public static MavenDomDependency getDomDependency(@NotNull final MavenDomDependencies dependencies,
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
