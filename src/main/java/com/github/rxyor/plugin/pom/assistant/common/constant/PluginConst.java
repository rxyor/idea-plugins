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

        String PROJECT = "project";
        String MODEL_VERSION = "modelVersion";
        String PARENT = "parent";
        String GROUP_ID = "groupId";
        String ARTIFACT_ID = "artifactId";
        String VERSION = "version";
        String PACKAGING = "packaging";
        String NAME = "name";
        String URL = "url";
        String MODULES = "modules";
        String PROPERTIES = "properties";
        String DEPENDENCIES = "dependencies";
        String DEPENDENCY_MANAGEMENT = "dependencyManagement";
        String PROFILES = "profiles";
        String REPOSITORIES = "repositories";
        String DISTRIBUTION_MANAGEMENT = "distributionManagement";
        String PLUGIN_REPOSITORIES = "pluginRepositories";


        String MODULE = "module";
        String PROPERTY = "property";
        String DEPENDENCY = "dependency";
    }

    interface XmlTag {

        String PREFIX = "XmlTag:";
    }
}
