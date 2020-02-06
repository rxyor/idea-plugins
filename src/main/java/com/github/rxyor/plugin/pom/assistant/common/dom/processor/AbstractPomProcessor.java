package com.github.rxyor.plugin.pom.assistant.common.dom.processor;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/4 周二 21:02:00
 * @since 1.0.0
 */
public abstract class AbstractPomProcessor {

    protected final Document document;
    protected final List<AbstractPomProcessor> processors = new LinkedList<>();

    public AbstractPomProcessor(String text) {
        try {
            this.document = DocumentHelper.parseText(text);
            processors.add(this);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(String.format(
                "Select file is invalid, errors[%s]", e.getMessage()));
        }
    }

    public AbstractPomProcessor(@NotNull AbstractPomProcessor processor) {
        this.document = processor.document;
        this.processors.addAll(processor.processors);
        this.processors.add(this);
    }

    protected abstract void doProcess();

    public void process() {
        if (processors == null || processors.isEmpty()) {
            return;
        }
        processors.forEach(p -> p.doProcess());
    }

    public String text() {
        try {
            OutputFormat format = new OutputFormat();
            format.setIndent(true);
            format.setNewlines(true);
            format.setNewLineAfterDeclaration(false);
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(this.document);
            xmlWriter.close();
            return writer.toString();
        } catch (Exception e) {
            return this.document.getXMLEncoding();
        }
    }
}
