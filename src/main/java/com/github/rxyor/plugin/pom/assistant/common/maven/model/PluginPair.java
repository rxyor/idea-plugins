package com.github.rxyor.plugin.pom.assistant.common.maven.model;

import lombok.Data;
import org.jetbrains.idea.maven.dom.model.MavenDomPlugin;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/3 周一 15:38:00
 * @since 1.0.0
 */
@Data
public class PluginPair {

    private MavenDomPlugin plugin;
    private MavenDomPlugin managementPlugin;
}
