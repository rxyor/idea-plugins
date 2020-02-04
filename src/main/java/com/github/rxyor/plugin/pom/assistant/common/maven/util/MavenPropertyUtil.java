package com.github.rxyor.plugin.pom.assistant.common.maven.util;

import com.github.rxyor.plugin.pom.assistant.common.constant.ValidConst.NoNull;
import com.google.common.base.Preconditions;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.dom.model.MavenDomProperties;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/3 周一 01:08:00
 * @since 1.0.0
 */
public class MavenPropertyUtil {

    private MavenPropertyUtil() {
    }

    /**
     *添加或者修改属性
     *
     * @author liuyang
     * @date 2020-02-03 周一 01:20:06
     * @param model
     * @param tag
     * @param valueText
     * @return
     */
    public static void addOrUpdateMavenProperty(@NotNull MavenDomProjectModel model,
        @NotNull String tag,
        @NotNull String valueText) {
        Preconditions.checkNotNull(model, NoNull.MavenDomProjectModel);

        MavenDomProperties mavenDomProperties = model.getProperties();
        XmlTag xmlTag = mavenDomProperties.ensureTagExists();
        XmlTag exist = xmlTag.findFirstSubTag(tag);
        if (exist != null) {
            exist.getValue().setText(valueText);
        } else {
            XmlTag propertyTag = xmlTag.createChildTag(tag, xmlTag.getNamespace(), valueText, false);
            xmlTag.add(propertyTag);
        }
    }
}
