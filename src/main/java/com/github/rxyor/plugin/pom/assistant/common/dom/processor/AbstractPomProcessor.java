package com.github.rxyor.plugin.pom.assistant.common.dom.processor;

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

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
    protected List<AbstractPomProcessor> processors = new ArrayList<>(4);

    public AbstractPomProcessor(String text) {
        try {
            this.document = DocumentHelper.parseText(text);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(String.format(
                "Select file is invalid, errors[%s]", e.getMessage()));
        }
    }

    public AbstractPomProcessor(String text,
        AbstractPomProcessor processor) {
        this(text);
        if (processor != null) {
            this.processors.add(processor);
        }
    }

    public abstract void process();
}
