package com.github.rxyor.plugin.pom.assistant.common.util;

import com.google.common.base.Preconditions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
}
