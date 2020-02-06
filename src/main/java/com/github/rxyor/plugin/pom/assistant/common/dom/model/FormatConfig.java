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

    public final static Map<String, String> NEWLINE_CONFIG = new HashMap<>(16);
    private final static String NEWLINE = "\n";

    private final static FormatConfig INSTANCE = new FormatConfig();

    static {
        //project root tag
        NEWLINE_CONFIG.put(PomTag.MODULES, NEWLINE);
        NEWLINE_CONFIG.put(PomTag.PROPERTIES, NEWLINE);
        NEWLINE_CONFIG.put(PomTag.DEPENDENCIES, NEWLINE);
        NEWLINE_CONFIG.put(PomTag.DEPENDENCY_MANAGEMENT, NEWLINE);
        NEWLINE_CONFIG.put(PomTag.PROFILES, NEWLINE);
        NEWLINE_CONFIG.put(PomTag.REPOSITORIES, NEWLINE);
        NEWLINE_CONFIG.put(PomTag.DISTRIBUTION_MANAGEMENT, NEWLINE);
        NEWLINE_CONFIG.put(PomTag.PLUGIN_REPOSITORIES, NEWLINE);
    }

    private FormatConfig() {
    }

    public static FormatConfig getInstance() {
        return INSTANCE;
    }

    public String getNewline(String tag) {
        return NEWLINE_CONFIG.get(tag);
    }

}
