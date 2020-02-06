package com.github.rxyor.plugin.pom.assistant.common.dom.model;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.PomTag;
import java.util.HashMap;
import java.util.Map;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/4 周二 23:24:00
 * @since 1.0.0
 */
public class FormatConfig {

    public final static Map<String, String> TAG_CONFIG = new HashMap<>(16);
    public final static Map<String, String> PATH_CONFIG = new HashMap<>(16);
    private final static String NEWLINE = "\n";
    private final static String PATH_REPLACE_HOLDER = "/*[name()='%s']";

    private final static FormatConfig INSTANCE = new FormatConfig();

    static {
        //tag
        TAG_CONFIG.put(PomTag.MODULES, NEWLINE);
        TAG_CONFIG.put(PomTag.PROPERTIES, NEWLINE);
        TAG_CONFIG.put(PomTag.DEPENDENCIES, NEWLINE);
        TAG_CONFIG.put(PomTag.DEPENDENCY_MANAGEMENT, NEWLINE);
        TAG_CONFIG.put(PomTag.PROFILES, NEWLINE);
        TAG_CONFIG.put(PomTag.REPOSITORIES, NEWLINE);
        TAG_CONFIG.put(PomTag.DISTRIBUTION_MANAGEMENT, NEWLINE);
        TAG_CONFIG.put(PomTag.PLUGIN_REPOSITORIES, NEWLINE);

        //path
        putPath(PomTag.PROJECT, PomTag.MODULES);
        putPath(PomTag.PROJECT, PomTag.PROPERTIES);
        putPath(PomTag.PROJECT, PomTag.DEPENDENCIES);
        putPath(PomTag.PROJECT, PomTag.DEPENDENCY_MANAGEMENT);
        putPath(PomTag.PROJECT, PomTag.PROFILES);
        putPath(PomTag.PROJECT, PomTag.REPOSITORIES);
        putPath(PomTag.PROJECT, PomTag.DISTRIBUTION_MANAGEMENT);
        putPath(PomTag.PROJECT, PomTag.PLUGIN_REPOSITORIES);
        putPath(PomTag.PROJECT, PomTag.BUILD);
    }

    private FormatConfig() {
    }

    public static FormatConfig getInstance() {
        return INSTANCE;
    }

    private static void putPath(String... tags) {
        if (tags == null || tags.length == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            sb.append(String.format(PATH_REPLACE_HOLDER, tag));
        }
        PATH_CONFIG.put(sb.toString(), NEWLINE);
    }

    public String getNewlineByTag(String tag) {
        return TAG_CONFIG.get(tag);
    }

    public String getNewlineByPath(String tag) {
        return PATH_CONFIG.get(tag);
    }

}
