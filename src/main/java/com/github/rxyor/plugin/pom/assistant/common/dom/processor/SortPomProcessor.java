package com.github.rxyor.plugin.pom.assistant.common.dom.processor;


import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/4 周二 21:51:00
 * @since 1.0.0
 */
public class SortPomProcessor extends AbstractPomProcessor {

    public SortPomProcessor(String text) {
        super(text);
    }

    public SortPomProcessor(String text,
        AbstractPomProcessor processor) {
        super(text, processor);
    }

    @Override
    public void process() {

    }

    private void sort() {
        this.sort(super.document.getRootElement());
    }

    private void sort(Element cur) {
        if (cur == null || cur.elements().isEmpty()) {
            return;
        }

        List<Element> list = cur.elements();
        Iterator<Element> it = list.iterator();
        while (it.hasNext()) {
            this.sort(it.next());
        }


    }

}
