package com.github.rxyor.plugin.pom.assistant.common.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.ui.popup.Balloon;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.swing.Icon;
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
@ToString
public class NotificationBuilder {

    private String id;
    private String myGroupId;
    private Icon myIcon;
    private NotificationType myType;
    private String myTitle;
    private String mySubtitle;
    private String myContent;
    private NotificationListener myListener;
    private String myDropDownText;
    private List<AnAction> myActions;
    private AnAction myContextHelpAction;
    private Runnable myWhenExpired;
    private Boolean myImportant;
    private WeakReference<Balloon> myBalloonRef;
    private long myTimestamp;

    NotificationBuilder() {
    }

    public NotificationBuilder id(String id) {
        this.id = id;
        return this;
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

    public NotificationBuilder myDropDownText(String myDropDownText) {
        this.myDropDownText = myDropDownText;
        return this;
    }

    public NotificationBuilder myActions(List<AnAction> myActions) {
        this.myActions = myActions;
        return this;
    }

    public NotificationBuilder myContextHelpAction(AnAction myContextHelpAction) {
        this.myContextHelpAction = myContextHelpAction;
        return this;
    }

    public NotificationBuilder myWhenExpired(Runnable myWhenExpired) {
        this.myWhenExpired = myWhenExpired;
        return this;
    }

    public NotificationBuilder myImportant(Boolean myImportant) {
        this.myImportant = myImportant;
        return this;
    }

    public NotificationBuilder myBalloonRef(WeakReference<Balloon> myBalloonRef) {
        this.myBalloonRef = myBalloonRef;
        return this;
    }

    public NotificationBuilder myTimestamp(long myTimestamp) {
        this.myTimestamp = myTimestamp;
        return this;
    }

    public Notification build() {
        return new Notification(this.myGroupId, this.myIcon, this.myTitle, this.mySubtitle,
            this.myContent, this.myType, this.myListener);
    }

}
