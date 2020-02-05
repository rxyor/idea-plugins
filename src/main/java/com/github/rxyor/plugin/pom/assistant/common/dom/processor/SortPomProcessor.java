package com.github.rxyor.plugin.pom.assistant.common.dom.processor;


import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.PomTag;
import com.github.rxyor.plugin.pom.assistant.common.dom.model.SortOrderConfig;
import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Node;

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
        super(processor);
    }

    @Override
    protected void doProcess() {
        sort();
    }

    private void sort() {
        this.recursivelySort(super.document.getRootElement());
    }

    /**
     *递归排序
     *
     * @author liuyang
     * @date 2020-02-06 周四 00:43:10
     * @param cur cur
     * @return
     */
    private void recursivelySort(Element cur) {
        if (cur == null || cur.elements().isEmpty()) {
            return;
        }

        List<Element> list = cur.elements();
        Iterator<Element> it = list.iterator();
        while (it.hasNext()) {
            this.recursivelySort(it.next());
        }

        //注释分组
        final Map<Element, List<Comment>> commentGroup = groupComment(cur);
        //标签排序
        list.sort(new SortComparator());
        //清空标签
        cur.clearContent();
        //恢复子级标签以及注释
        list.forEach(e -> {
            List<Comment> comments = commentGroup.get(e);
            comments.forEach(c -> cur.add(c));
            cur.add(e);
        });
        System.out.println(list);
    }

    /**
     *给元素的注释分组
     *
     * @author liuyang
     * @date 2020-02-06 周四 00:43:51
     * @param cur cur
     * @return
     */
    private Map<Element, List<Comment>> groupComment(Element cur) {
        final Map<Element, List<Comment>> group = new HashMap<>(4);
        Iterator<Node> it = cur.nodeIterator();
        if (cur == null || !it.hasNext()) {
            return group;
        }

        LinkedList<Comment> comments = new LinkedList<>();
        while (it.hasNext()) {
            Node node = it.next();
            if (node instanceof Comment) {
                comments.add((Comment) node);
            } else if (node instanceof Element) {
                group.put((Element) node, Lists.newArrayList(comments));
                //reset list
                comments.clear();
            }
        }

        return group;
    }

    /**
     *<p>
     * 排序比较器
     *</p>
     *
     * @author liuyang
     * @date 2020-02-06 周四 00:29:34
     * @since 1.0.0
     */
    private static class SortComparator implements Comparator<Element> {

        /**
         * 那些标签需要比较Value或者Text
         */
        public static final Map<String, Boolean> COMPARE_VALUE_TAG_MAP = new HashMap<>(8);

        static {
            COMPARE_VALUE_TAG_MAP.put(PomTag.GROUP_ID, true);
            COMPARE_VALUE_TAG_MAP.put(PomTag.MODULE, true);
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
                //递归比较，直到比较出结果
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
