package com.github.rxyor.plugin.pom.assistant.common.dom.processor;


import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.PomTag;
import com.github.rxyor.plugin.pom.assistant.common.dom.model.SortOrderConfig;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

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
        sort();
    }

    public String toText() {
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

        list.sort(new SortComparator());
        cur.clearContent();
        list.forEach(e -> cur.add(e));
        System.out.println(list);
    }

    private static class SortComparator implements Comparator<Element> {

        public static final Map<String, Boolean> COMPARE_VALUE_TAG_MAP = new HashMap<>(8);

        static {
            COMPARE_VALUE_TAG_MAP.put(PomTag.GROUP_ID, true);
        }

        @Override
        public int compare(Element o1, Element o2) {
            CompareRet ret1 = compareObject(o1, o2);
            if (!CompareRet.NONNULL.equals(ret1)) {
                return enumToInt(ret1);
            }

            int ret2 = compareByConfig(o1, o2);
            if (ret2 != 0) {
                return ret2;
            }

            int ret3 = compareByName(o1, o2);
            if (ret3 != 0) {
                return ret3;
            }

            return 0;
        }

        private CompareRet compareObject(Object o1, Object o2) {
            if (o1 == null && o2 == null) {
                return CompareRet.NULL;
            } else if (o1 == null && o2 != null) {
                return CompareRet.LARGE;
            } else if (o1 != null && o2 == null) {
                return CompareRet.SMALL;
            } else {
                return CompareRet.NONNULL;
            }
        }

        private int compareByConfig(Element o1, Element o2) {
            SortOrderConfig config = SortOrderConfig.getInstance();
            int order1 = config.getOrder(o1.getParent().getName(), o1.getName());
            int order2 = config.getOrder(o2.getParent().getName(), o2.getName());
            return order1 - order2;
        }

        private int compareByName(Element o1, Element o2) {
            CompareRet compareRet = compareObject(o1, o2);
            if (!CompareRet.NONNULL.equals(compareRet)) {
                return enumToInt(compareRet);
            }

            int ret;
            if (Boolean.TRUE.equals(COMPARE_VALUE_TAG_MAP.get(o1.getName()))
                && Boolean.TRUE.equals(COMPARE_VALUE_TAG_MAP.get(o2.getName()))) {
                ret = o1.getText().compareTo(o2.getText());
            } else {
                ret = o1.getName().compareTo(o2.getName());
            }

            if (ret != 0) {
                return ret;
            } else {
                return compareByName(getElementFirst(o1.elements()),
                    getElementFirst(o2.elements()));
            }

        }

        private Element getElementFirst(List<Element> list) {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return list.get(0);
        }

        private int enumToInt(CompareRet ret) {
            switch (ret) {
                case SMALL:
                    return -1;
                case LARGE:
                    return 1;
                default:
                    return 0;
            }
        }

        public enum CompareRet {
            NULL, LARGE, SMALL, NONNULL
        }
    }
}
