package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.File.SpecificFile;
import com.github.rxyor.plugin.pom.assistant.common.util.NotificationUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/24 周五 15:31:00
 * @since 1.0.0
 */
public class PomFileInfoAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = pickupCurrentFile(e);

        PsiElement element = e.getData(LangDataKeys.PSI_ELEMENT);

        XmlTag xmlTag = (XmlTag) element;
        System.out.println(xmlTag);
        ASTNode node = element.getNode();
        PsiFile xmlFile = psiFile.getViewProvider().getPsi(XMLLanguage.INSTANCE);
        NotificationUtil.info("INFO", element.getParent().getText());
        NotificationUtil.info("INFO", psiFile.getName());
    }


    private PsiFile pickupCurrentFile(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            NotificationUtil.warn("Invalid Project", "Project is empty, please open valid project");
            return null;
        }

        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null
            || !SpecificFile.POM.equalsIgnoreCase(psiFile.getName())
            || !StdFileTypes.XML.equals(psiFile.getFileType())
            || !(psiFile instanceof XmlFile)) {
            NotificationUtil.error("Invalid File", "Please select valid pom.xml");
            return null;
        }

        return psiFile;
    }
}
