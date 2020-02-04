package com.github.rxyor.plugin.pom.assistant.common.constant;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/24 周五 17:14:00
 * @since 1.0.0
 */
public interface PluginConst {

    interface App {

        String GROUP_ID = "PomAssistant";
    }

    interface Biz {

        String DEPENDENCY = "Dependency";
        String MANAGEMENT_DEPENDENCY = "ManagementDependency";
    }

    interface File {

        interface SpecificFile {

            String POM = "pom.xml";
        }
    }

    interface PomTag {

        String DEPENDENCY_MANAGEMENT = "dependencyManagement";
        String DEPENDENCIES = "dependencies";
        String DEPENDENCY = "dependency";
        String GROUP_ID = "groupId";
        String ARTIFACT_ID = "artifactId";
        String VERSION = "version";

        String PROPERTIES = "properties";
        String PROPERTY = "property";
    }

    interface XmlTag {

        String PREFIX = "XmlTag:";
    }
}
