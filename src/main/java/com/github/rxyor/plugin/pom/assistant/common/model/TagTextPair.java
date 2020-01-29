package com.github.rxyor.plugin.pom.assistant.common.model;

import lombok.Data;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/1/29 周三 23:27:00
 * @since 1.0.0
 */
@Data
public class TagTextPair {

    /**
     * xml 标签
     */
    private String tag;

    /**
     * xml 值
     */
    private String value;

}
