package com.github.rxyor.plugin.pom.assistant.common.maven.util;

import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
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
}
