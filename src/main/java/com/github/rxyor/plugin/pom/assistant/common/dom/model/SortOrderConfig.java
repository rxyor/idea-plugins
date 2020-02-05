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

    public final static Map<String, Map<String, Integer>> ORDER_CONFIG = new HashMap<>(16);

    private final static SortOrderConfig INSTANCE = new SortOrderConfig();

    static {
        //project root tag
        Map<String, Integer> project = new HashMap<>(16);
        ORDER_CONFIG.put(PomTag.PROJECT, project);
        int projectI = 0;
        project.put(PomTag.MODEL_VERSION, projectI++);
        project.put(PomTag.PARENT, projectI++);
        project.put(PomTag.GROUP_ID, projectI++);
        project.put(PomTag.ARTIFACT_ID, projectI++);
        project.put(PomTag.VERSION, projectI++);
        project.put(PomTag.PACKAGING, projectI++);
        project.put(PomTag.NAME, projectI++);
        project.put(PomTag.URL, projectI++);
        project.put(PomTag.MODULES, projectI++);
        project.put(PomTag.PROPERTIES, projectI++);
        project.put(PomTag.DEPENDENCIES, projectI++);
        project.put(PomTag.DEPENDENCY_MANAGEMENT, projectI++);
        project.put(PomTag.PROFILES, projectI++);
        project.put(PomTag.REPOSITORIES, projectI++);
        project.put(PomTag.DISTRIBUTION_MANAGEMENT, projectI++);
        project.put(PomTag.PLUGIN_REPOSITORIES, projectI++);
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
        Map<String, Integer> config = Optional.ofNullable(ORDER_CONFIG.get(tag))
            .orElse(ORDER_CONFIG.get(PomTag.PROJECT));
        return Optional.ofNullable(config).orElse(new HashMap<>(0));
    }
}
