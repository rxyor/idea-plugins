package com.github.rxyor.plugin.pom.assistant.action;

import com.github.rxyor.plugin.pom.assistant.common.dom.processor.SortPomProcessor;
import com.github.rxyor.plugin.pom.assistant.common.psi.util.PsiUtil;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/4 周二 14:47:00
 * @since 1.0.0
 */
public class SortAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final PsiFile psiFile = PsiUtil.getPsiFile(e);
        Document document = this.getDocument(psiFile);
        String source = document.getText();
        SortPomProcessor processor = new SortPomProcessor(source);
        processor.process();
        String result = processor.toText();
        final XmlFile xmlFile = (XmlFile) PsiFileFactory.getInstance(psiFile.getProject())
            .createFileFromText("", XmlFileType.INSTANCE, result);
        PsiUtil.reformat(psiFile);
        WriteCommandAction.runWriteCommandAction(PsiUtil.getProject(e),
            () -> document.setText(xmlFile.getText()));
    }

    private Document getDocument(@NotNull PsiFile psiFile) {
        return psiFile.getViewProvider().getDocument();
    }

    protected String format(String text) {
        return null;
    }
}
