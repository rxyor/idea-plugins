package com.github.rxyor.plugin.pom.assistant.common.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import lombok.extern.slf4j.Slf4j;

/**
 * 剪切板工具类
 *
 * @author 汪小哥
 * @date 20-11-2019
 */
@Slf4j
public class ClipboardUtil {

    private ClipboardUtil() {
    }

    /**
     * 从剪贴板中获取文本（粘贴）
     */
    public static String getFromClipboard() {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪贴板中的内容
        Transferable trans = clipboard.getContents(null);

        if (trans != null) {
            // 判断剪贴板中的内容是否支持文本
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
                    return text;
                } catch (Exception e) {
                }
            }
        }

        return null;
    }

    /**
     * 把文本设置到剪贴板（复制）
     */
    public static void setToClipboard(String text) {
        try {
            // 获取系统剪贴板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // 封装文本内容
            Transferable trans = new StringSelection(text);
            // 把文本内容设置到系统剪贴板
            clipboard.setContents(trans, null);
        } catch (Exception e) {
        }
    }


}
