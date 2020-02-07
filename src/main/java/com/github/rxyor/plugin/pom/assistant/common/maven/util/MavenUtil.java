package com.github.rxyor.plugin.pom.assistant.common.maven.util;

import static com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenDependencyUtil.findDependencyTag;
import static com.github.rxyor.plugin.pom.assistant.common.maven.util.MavenPluginUtil.findPluginTag;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/8 周六 00:40:00
 * @since 1.0.0
 */
public class MavenUtil {

    private MavenUtil() {
    }

    /**
     *寻找点击的标签的父标签类型
     *
     * @param element
     * @return
     */
    public static TagType findClickParentTagType(@NotNull PsiElement element) {
        if (findDependencyTag(element) != null) {
            return TagType.dependency;
        } else if (findPluginTag(element) != null) {
            return TagType.plugin;
        }
        return null;
    }

    /**
     * 标签类型
     */
    public enum TagType {
        plugin, dependency
    }
}
