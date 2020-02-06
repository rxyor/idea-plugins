package com.github.rxyor.plugin.pom.assistant.common.dom.processor;

import com.github.rxyor.plugin.pom.assistant.common.dom.model.FormatConfig;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.dom.DOMText;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/6 周四 10:41:00
 * @since 1.0.0
 */
public class FormatPomProcessor extends AbstractPomProcessor {

    public FormatPomProcessor(String text) {
        super(text);
    }

    public FormatPomProcessor(@NotNull AbstractPomProcessor processor) {
        super(processor);
    }

    @Override
    protected void doProcess() {
        format();
    }

    private void format() {
        this.format(super.document.getRootElement());
    }

    private void format(Element cur) {
        if (cur == null) {
            return;
        }

        List<Element> elements = cur.elements();
        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()) {
            format(iterator.next());
        }

        FormatConfig config = FormatConfig.getInstance();
        String newline = config.getNewlineByPath(cur.getPath());
        if (newline == null) {
            return;
        }

        Element parent = cur.getParent();
        if (parent == null) {
            return;
        }

        List<Node> list = new LinkedList<>();
        Iterator<Node> it = parent.nodeIterator();
        while (it.hasNext()) {
            Node node = it.next();
            //特定标签添加换行
            if ((node instanceof Element) && cur.getName().equals(node.getName())) {
                Text text = new DOMText(newline);
                list.add(text);
            }
            list.add(node);
        }

        parent.clearContent();
        list.forEach(node -> parent.add(node));
    }
}
