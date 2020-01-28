package com.github.rxyor.plugin.pom.assistant.common.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import javax.swing.Icon;
import lombok.Getter;
import lombok.ToString;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/24 周五 17:20:00
 * @since 1.0.0
 */
@Getter
@ToString
public class NotificationBuilder {

    private String myGroupId;
    private Icon myIcon;
    private NotificationType myType;
    private String myTitle;
    private String mySubtitle;
    private String myContent;
    private NotificationListener myListener;

    NotificationBuilder() {
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public NotificationBuilder myGroupId(String myGroupId) {
        this.myGroupId = myGroupId;
        return this;
    }

    public NotificationBuilder myIcon(Icon myIcon) {
        this.myIcon = myIcon;
        return this;
    }

    public NotificationBuilder myType(NotificationType myType) {
        this.myType = myType;
        return this;
    }

    public NotificationBuilder myTitle(String myTitle) {
        this.myTitle = myTitle;
        return this;
    }

    public NotificationBuilder mySubtitle(String mySubtitle) {
        this.mySubtitle = mySubtitle;
        return this;
    }

    public NotificationBuilder myContent(String myContent) {
        this.myContent = myContent;
        return this;
    }

    public NotificationBuilder myListener(NotificationListener myListener) {
        this.myListener = myListener;
        return this;
    }

    public Notification build() {
        return new Notification(this.myGroupId, this.myIcon, this.myTitle, this.mySubtitle,
            this.myContent, this.myType, this.myListener);
    }

}
