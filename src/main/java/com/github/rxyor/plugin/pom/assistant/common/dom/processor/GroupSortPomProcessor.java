package com.github.rxyor.plugin.pom.assistant.common.dom.processor;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jetbrains.annotations.NotNull;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/6 周四 13:59:00
 * @since 1.0.0
 */
public class GroupSortPomProcessor extends AbstractPomProcessor {

    public GroupSortPomProcessor(String text) {
        super(text);
    }

    public GroupSortPomProcessor(@NotNull AbstractPomProcessor processor) {
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

        Iterator<Element> it = cur.elements().iterator();
        while (it.hasNext()) {
            this.recursivelySort(it.next());
        }

        //给划分的组按组排序
        List<Group> groupList = sortGroup(cur);

        List<Node> sortList = new ArrayList<>(16);
        for (Group group : groupList) {
            List<Node> selectionSortedList = sortSingleGroup(group);
            sortList.addAll(selectionSortedList);
        }

        cur.clearContent();
        sortList.forEach(node -> cur.add(node));
    }

    /**
     *单个组内元素排序
     *
     * @author liuyang
     * @date 2020-02-06 周四 23:21:49
     * @param group group
     * @return
     */
    @NotNull
    private List<Node> sortSingleGroup(Group group) {
        Map<Element, List<Comment>> map = markupCommentForElement(group);
        List<Element> selectionList = rmFirstAndTailIfNeed(group).stream()
            .map(node -> {
                if (node instanceof Element) {
                    return (Element) node;
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        //标签排序
        selectionList.sort(new SortComparator());

        List<Node> selectionSortedList = new ArrayList<>(16);
        //恢复子级标签以及注释
        selectionList.forEach(e -> {
            List<Comment> comments = map.get(e);
            comments.forEach(c -> selectionSortedList.add(c));
            selectionSortedList.add(e);
        });

        //含有组注释的添加组注释到首尾
        if ((group.first() != null && (group.first() instanceof Comment))
            && (group.tail() != null && (group.tail() instanceof Comment))
            && StringUtils.equals(group.first().getText().trim(), group.tail().getText().trim())) {
            selectionSortedList.add(0, group.first());
            selectionSortedList.add(selectionSortedList.size(), group.tail());
        }
        return selectionSortedList;
    }

    @NotNull
    private List<Group> sortGroup(Element cur) {
        //注释分组
        List<Group> groupList = this.groupBySameComment(cur);
        groupList.sort((o1, o2) -> {
            if (o1.leader == null && o2.leader == null) {
                return 0;
            } else if (o1.leader == null && o2.leader != null) {
                return 1;
            } else if (o1.leader != null && o2.leader == null) {
                return -1;
            }

            return o1.leader.getText().compareTo(o2.leader.getText());
        });
        return groupList;
    }

    /**
     *给元素的注释分组
     *
     * @author liuyang
     * @date 2020-02-06 周四 00:43:51
     * @param group cur
     * @return
     */
    private Map<Element, List<Comment>> markupCommentForElement(Group group) {
        if (group == null || group.followers.isEmpty()) {
            return new HashMap<>(0);
        }

        List<Node> list = rmFirstAndTailIfNeed(group);

        Map<Element, List<Comment>> map = new HashMap<>(8);

        LinkedList<Comment> comments = new LinkedList<>();
        for (Node node : list) {
            if (node instanceof Comment) {
                comments.add((Comment) node);
            } else if (node instanceof Element) {
                map.put((Element) node, Lists.newArrayList(comments));
                //reset list
                comments.clear();
            }
        }
        return map;
    }

    @NotNull
    private List<Node> rmFirstAndTailIfNeed(Group group) {
        List<Node> list;
        if (group.first().getText().equals(group.tail().getText())) {
            list = group.followers.subList(1, group.followers.size() - 1);
        } else {
            list = Lists.newArrayList(group.followers);
        }
        return list;
    }

    private List<Group> groupBySameComment(Element cur) {
        if (cur == null || !cur.nodeIterator().hasNext()) {
            return new ArrayList<>(0);
        }

        List<Node> nodeList = Lists.newArrayList(cur.nodeIterator());
        List<Range> rangeList = splitToRange(nodeList);

        List<Group> splitGroupList = new ArrayList<>(8);

        Group noGroupNodes = splitNoLeaderGroup(nodeList, rangeList);

        splitGroupList.add(noGroupNodes);

        rangeList.forEach(r -> {
            List<Node> subList = nodeList.subList(r.start, r.end + 1);
            Group group = new Group(subList.get(0));
            group.addFollower(subList);
            splitGroupList.add(group);
        });

        return splitGroupList;
    }

    @NotNull
    private Group splitNoLeaderGroup(List<Node> nodeList, List<Range> rangeList) {
        Group noGroupNodes = new Group(null);
        for (int i = 0; i < nodeList.size(); i++) {
            boolean inOneRange = false;
            for (Range r : rangeList) {
                boolean in = i >= r.start && i <= r.end;
                inOneRange = inOneRange || in;
                if (inOneRange) {
                    break;
                }
            }
            if (!inOneRange) {
                noGroupNodes.addFollower(nodeList.get(i));
            }
        }
        return noGroupNodes;
    }

    @NotNull
    private List<Range> splitToRange(List<Node> nodeList) {
        List<Range> rangeList = new ArrayList<>(8);

        Stack<Integer> indexStack = new Stack<>();
        //寻找闭合注释标签对应下标
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            if (node instanceof Comment) {
                if (indexStack.isEmpty()) {
                    indexStack.push(i);
                } else {
                    Node peek = nodeList.get(indexStack.peek());
                    if (peek.getText().equals(node.getText())) {
                        rangeList.add(new Range(indexStack.peek(), i));
                        indexStack.clear();
                    }
                }
            }

            //如果注释标签不能闭合，返回标签元素下一个位置，继续寻找
            if (i == nodeList.size() - 1 && !indexStack.isEmpty()) {
                i = indexStack.peek();
                indexStack.clear();
            }
        }
        return rangeList;
    }

    private class Group {

        private final LinkedList<Node> followers = new LinkedList<>();
        private Node leader;

        public Group(Node leader) {
            this.leader = leader;
        }

        public void addFollower(Node node) {
            if (node != null) {
                this.followers.add(node);
            }
        }

        public void addFollower(List<Node> nodeList) {
            if (nodeList != null && !nodeList.isEmpty()) {
                this.followers.addAll(nodeList);
            }
        }

        public Node first() {
            if (!followers.isEmpty()) {
                return followers.get(0);
            }
            return null;
        }

        public Node tail() {
            if (!followers.isEmpty()) {
                return followers.get(followers.size() - 1);
            }
            return null;
        }
    }

    private class Range {

        private int start;
        private int end;

        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
