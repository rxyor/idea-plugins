package com.github.rxyor.plugin.pom.assistant.common.notification.util;


import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.App;
import com.google.common.base.Preconditions;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/24 周五 17:17:00
 * @since 1.0.0
 */
public class NotificationUtil {

    private NotificationUtil() {
    }

    public static void info(String title, String content) {
        notify(title, content, NotificationType.INFORMATION);
    }

    public static void info(String title, String content, Project project) {
        notify(title, content, NotificationType.INFORMATION,project);
    }

    public static void warn(String title, String content) {
        notify(title, content, NotificationType.WARNING);
    }

    public static void warn(String title, String content, Project project) {
        notify(title, content, NotificationType.WARNING,project);
    }

    public static void error(String title, String content) {
        notify(title, content, NotificationType.ERROR);
    }

    public static void error(String title, String content, Project project) {
        notify(title, content, NotificationType.ERROR,project);
    }

    public static void notify(String title, String content, NotificationType type) {
        notify(title, content, type, null);
    }

    public static void notify(String title, String content, NotificationType type, Project project) {
        Notification notification = NotificationBuilder.builder()
            .myGroupId(App.GROUP_ID)
            .myContent(content)
            .myTitle(title)
            .myType(type)
            .build();
        if (project != null) {
            notify(notification, project);
        } else {
            notify(notification);
        }
    }

    public static void notify(Notification notification) {
        Preconditions.checkNotNull(notification, "notification arguments can't be null");
        Notifications.Bus.notify(notification);
    }

    public static void notify(Notification notification, Project project) {
        Preconditions.checkNotNull(notification, "notification arguments can't be null");
        Preconditions.checkNotNull(project, "project arguments can't be null");
        Notifications.Bus.notify(notification, project);
    }
}
