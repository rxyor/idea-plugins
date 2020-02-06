package com.github.rxyor.plugin.pom.assistant.common.dom.model;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.PomTag;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/4 周二 23:24:00
 * @since 1.0.0
 */
public class SortOrderConfig {

    public final static Map<String, Map<String, Integer>> TAG_ORDER_CONFIG = new HashMap<>(16);

    private final static SortOrderConfig INSTANCE = new SortOrderConfig();

    static {
        //project root tag
        Map<String, Integer> project = new HashMap<>(16);
        TAG_ORDER_CONFIG.put(PomTag.PROJECT, project);
        int i = 0;
        project.put(PomTag.MODEL_VERSION, i++);
        project.put(PomTag.PARENT, i++);
        project.put(PomTag.GROUP_ID, i++);
        project.put(PomTag.ARTIFACT_ID, i++);
        project.put(PomTag.VERSION, i++);
        project.put(PomTag.PACKAGING, i++);
        project.put(PomTag.NAME, i++);
        project.put(PomTag.URL, i++);
        project.put(PomTag.MODULES, i++);
        project.put(PomTag.PROPERTIES, i++);
        project.put(PomTag.DEPENDENCIES, i++);
        project.put(PomTag.DEPENDENCY_MANAGEMENT, i++);
        project.put(PomTag.PROFILES, i++);
        project.put(PomTag.REPOSITORIES, i++);
        project.put(PomTag.DISTRIBUTION_MANAGEMENT, i++);
        project.put(PomTag.PLUGIN_REPOSITORIES, i++);

        //project root tag
        Map<String, Integer> properties = new HashMap<>(16);
        TAG_ORDER_CONFIG.put(PomTag.PROPERTIES, properties);
        i = 0;
        properties.put("java.version", i++);
        properties.put("java.encoding", i++);
        properties.put("project.encoding", i++);
        properties.put("app.name", i++);
        properties.put("timestamp", i++);
        properties.put("maven.test.skip", i++);
        properties.put("maven.test.failure.ignore", i++);
        properties.put("maven.compiler.source", i++);
        properties.put("maven.compiler.target", i++);
        properties.put("maven.compiler.compilerVersion", i++);
        properties.put("maven.build.timestamp.format", i++);
    }

    private SortOrderConfig() {
    }

    public static SortOrderConfig getInstance() {
        return INSTANCE;
    }

    public int getOrder(String parentTag, String curTag) {
        Map<String, Integer> config = getOrderMapByTag(parentTag);
        return Optional.ofNullable(config.get(curTag)).orElse(Integer.MAX_VALUE);
    }

    public Map<String, Integer> getOrderMapByTag(String tag) {
        if (StringUtils.isBlank(tag)) {
            return new HashMap<>(0);
        }
        Map<String, Integer> config = Optional.ofNullable(TAG_ORDER_CONFIG.get(tag))
            .orElse(TAG_ORDER_CONFIG.get(PomTag.PROJECT));
        return Optional.ofNullable(config).orElse(new HashMap<>(0));
    }
}
