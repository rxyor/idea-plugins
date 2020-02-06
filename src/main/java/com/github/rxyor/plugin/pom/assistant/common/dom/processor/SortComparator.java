package com.github.rxyor.plugin.pom.assistant.common.dom.processor;

import com.github.rxyor.plugin.pom.assistant.common.constant.PluginConst.PomTag;
import com.github.rxyor.plugin.pom.assistant.common.dom.model.SortOrderConfig;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Element;

/**
 *<p>
 * 排序比较器
 *</p>
 *
 * @author liuyang
 * @date 2020-02-06 周四 00:29:34
 * @since 1.0.0
 */
public class SortComparator implements Comparator<Element> {

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