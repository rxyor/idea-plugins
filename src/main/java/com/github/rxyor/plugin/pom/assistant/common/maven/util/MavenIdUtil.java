package com.github.rxyor.plugin.pom.assistant.common.maven.util;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomPlugin;
import org.jetbrains.idea.maven.model.MavenId;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/3 周一 15:43:00
 * @since 1.0.0
 */
public class MavenIdUtil {

    private MavenIdUtil() {
    }

    public static MavenId convert(MavenDomDependency dependency) {
        if (dependency == null) {
            return null;
        }

        return new MavenId(dependency.getGroupId().getValue(),
            dependency.getArtifactId().getValue(), dependency.getVersion().getValue());
    }

    public static MavenId convert(MavenDomPlugin plugin) {
        if (plugin == null) {
            return null;
        }

        return new MavenId(plugin.getGroupId().getValue(),
            plugin.getArtifactId().getValue(), plugin.getVersion().getValue());
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
        MavenId mavenId = MavenDependencyUtil.getClickMavenId(psiElement);
        if (mavenId == null) {
            return MavenPluginUtil.getClickMavenId(psiElement);
        }
        return mavenId;
    }
}
