package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.model.TagTextPair;
import com.github.rxyor.plugin.pom.assistant.common.model.XmlDependency;
import com.github.rxyor.plugin.pom.assistant.common.util.MavenUtil;
import com.github.rxyor.plugin.pom.assistant.common.util.NotificationUtil;
import com.google.common.base.Preconditions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/27 周一 15:31:00
 * @since 1.0.0
 */
public class ExtractVersionAction extends DumbAwareAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        Preconditions.checkNotNull(editor, "editor can't be null");
        Preconditions.checkNotNull(psiFile, "psiFile can't be null");

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);

        if (element == null) {
            NotificationUtil.warn("Pom Assistant", "please select valid pom.xml node");
            return;
        }

        XmlDependency xmlDependency = MavenUtil.parseXmlDependency(element);
        XmlFile xmlFile = MavenUtil.getXmlFile(psiFile);

        String tag = xmlDependency.getArtifactId() + ".version";
        CommandProcessor.getInstance().executeCommand(e.getProject(),
            (Runnable) () -> {
                MavenUtil.addPropertiesSubTag(xmlFile, new TagTextPair(tag, xmlDependency.getVersion()));
                CodeStyleManager.getInstance(e.getProject()).reformat(xmlFile);
                xmlFile.getVirtualFile().refresh(true, true);
            }
            , "Pom", "pom");
        NotificationUtil.info("INFO", xmlDependency.toString());
    }
}
