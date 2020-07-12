package com.sparksys.commons.core.utils.pdf;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: pdf base方法封装
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:12:10
 */
public class DocumentPdfTemplate extends PdfUtils {

    /**
     * 默认字体大小13
     */
    public float fontSize = 13;
    /**
     * 默认单个汉子的宽度，size为12
     */
    public float fontWidth = 12;
    /**
     * 默认数字的宽度，size 为7
     */
    public float numWidth = 7;
    /**
     * 默认行间距
     */
    public float rowSpacing = 23;
    /**
     * 预留宽度，保证右边对差距最小
     */
    public float reserveWidth = 0;
    /**
     * 当前页
     */
    public int currPage = 1;
    /**
     * 页数默认为1
     */
    public int pageCount = 1;
    /**
     * 一行可以于打印文本的宽度
     */
    public float rowWidth = 612;

    public float realTop = 0;

    public List<String> arrText = new ArrayList<>();

    public DocumentPdfTemplate() {
        initPdfData();
    }

    /**
     * 添加不带下划线文本
     *
     * @param str      文本(如果宽度不足缩小字体)
     * @param width    指定文本宽度
     * @param isCenter 文本是否居中
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:26:50
     */
    public void addAppointWidthText(String str, float width, boolean isCenter) {
        if (str == null) {
            str = "";
        }
        printLineOne(str, width, false, isCenter, true);
    }

    /**
     * 添加带下划线文本且指定下划线长度
     *
     * @param str      字符串
     * @param width    指定文本宽度
     * @param isCenter 是否居中
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:27:10
     */
    public void addAppointWidthLineText(String str, float width, boolean isCenter) {
        if (str == null) {
            str = "";
        }
        printLineOne(str, width, true, isCenter, true);
    }

    /**
     * 根据不同的条件进行打印
     *
     * @param str      字符串
     * @param width    指定文本宽度
     * @param isLine   是否有横线
     * @param isCenter 是否居中
     * @param autoSize 自动大小
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:27:22
     */
    public void printLineOne(String str, float width, boolean isLine, boolean isCenter, boolean autoSize) {
        if (str == null) {
            str = "";
        }
        str = str.toString();
        // 保存当前默认fontSize
        float currFontSize = fontSize;
        // 保存当前默认数字文本宽度
        float numW = numWidth;
        // 保存当前默认文本宽度
        float fontW = fontWidth;
        // 保存当前左边距
        float startLeft = currLeft;
        // 计算文本居中时要往右移动的宽度
        float addLeftWidth = 0;
        // 单个字符的宽度 默认为文本宽度
        float currFontWidth = fontWidth;
        // 缩小字体时文本显示时位置偏移高度
        float addTop = 0;
        // 要求自动大小
        if (autoSize) {
            Map<String, Float> map = runSize(str, width);
            // 当前宽度适合的fontSize
            currFontSize = map.get("fontSize");
            // 当前文本偏移高度
            addTop = map.get("topSpacing");
            // 当前文本单个宽度
            fontW = map.get("fontWidth");
            // 当前数字文本单个宽度
            numW = map.get("numWidth");
        }
        if (isCenter) {
            // 求出文本居中要往中间偏移的宽度
            addLeftWidth = textCenter(str, width, fontW, numW);
        }
        // 文本单个打印
        for (int i = 0; i < str.length(); i++) {
            String regEx = "[(a-zA-Z0-9)]";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher;
            String c = String.valueOf(str.charAt(i));
            matcher = pattern.matcher(c);
            boolean rs = matcher.find();
            if (rs) {
                currFontWidth = numW;
            } else {
                currFontWidth = fontW;
            }
            if (top + addTop > 1000) {
                addNewPage();
                currLeft = 55;
                top = 640;
            }
            addPrintText(top + addTop, currLeft + addLeftWidth, currFontSize, str.charAt(i));
            currLeft += currFontWidth;
        }
        if (isLine) {
            addLine(0.5f, startLeft, startLeft + width, top - 3);
        }
        // 左边距追加
        currLeft = startLeft + width;
    }

    /**
     * 添加没有下划线的文本
     *
     * @param str 字符串
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:29:04
     */
    public void addText(String str) {
        if (str == null) {
            str = "";
        }
        str = str.toString();
        // 求出字符串所占宽度
        float width = getStrWidth(str, fontWidth, numWidth);
        // 得到当前行剩余宽度
        float currWidth = totalWidth - currLeft;
        // 判断是否要换行
        if (currWidth < width) {
            // 对文本进行分行
            List<String> arrText = subText(currWidth, str);
            // 打印
            printLineOne(arrText.get(0), currWidth, false, false, false);
            // 初始左边距默认值
            currLeft = left;
            // 换行上边距追加
            top -= rowSpacing;
            // 打印剩下的文本
            addText(arrText.get(1));
        } else {// 不换行的情况下
            // 打印
            printLineOne(str, width, false, false, false);
        }
    }

    /**
     * 添加有下划线的文本
     *
     * @param str          str需要打印的字符串
     * @param minTextCount 最小字数为0时自动
     * @param isFullLine   是否为整行
     * @param isCenter     是否居中
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:29:27
     */
    public void addLineText(String str, float minTextCount, boolean isFullLine, boolean isCenter) {
        if (str == null) {
            str = "";
        }
        // 有要求最小下划线长度的情况下
        if (minTextCount != 0) {
            str = fullText(str, minTextCount * fontWidth);
        }
        // 求出字符串所占宽度
        float width = getStrWidth(str, fontWidth, numWidth);
        // 得到当前行剩余宽度
        float currWidth = totalWidth - currLeft;
        // 判断是否要换行
        if (currWidth < width) {
            // 对文本进行分行
            List<String> arrText = subText(currWidth, str);
            // 打印
            printLineOne(arrText.get(0), currWidth, true, isCenter, false);
            // 初始左边距默认值
            currLeft = left;
            // 换行 上边距追加
            top -= rowSpacing;
            // 打印剩下的文本
            addLineText(arrText.get(1), 0, isFullLine, isCenter);
        } else {
            // 不换行的情况下
            if (!isFullLine) {
                //不要求末行为整行
                printLineOne(str, width, true, isCenter, false);
            } else {
                // 添加字符串长度为整行长度，以全角空格填充
                str = fullText(str, currWidth);
                // 打印
                printLineOne(str, currWidth, true, isCenter, false);
                // 初始左边距默认值
                currLeft = left;
                // 换行上边距追加
                top -= rowSpacing;
            }
        }
    }

    /**
     * 全角空格填充字符串为指定宽度
     *
     * @param str   字符串
     * @param width 剩余宽度
     * @return String
     * @author zhouxinlei
     * @date 2020-05-08 15:25:55
     */
    public String fullText(String str, float width) {
        float cWidth = totalWidth - currLeft;
        // 定义初始文本宽度
        float subStringWidth = 0;
        String regEx = "[(a-zA-Z0-9)]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher;
        for (int i = 0; i < str.length(); i++) {
            String c = String.valueOf(str.charAt(i));
            matcher = pattern.matcher(c);
            boolean rs = matcher.find();
            // 判断是否是英文数字或者空格 针对不同的类型追加宽度
            if (rs) {
                subStringWidth += numWidth;
                cWidth = (cWidth - numWidth) == 0 ? rowWidth : (cWidth - numWidth);
            } else {
                if (cWidth < fontWidth) {
                    subStringWidth += numWidth;
                    cWidth = rowWidth;
                }
                subStringWidth += fontWidth;
                cWidth = (cWidth - fontWidth) == 0 ? rowWidth : (cWidth - fontWidth);
            }
        }
        boolean bool = false;
        // 当字符串宽度不够时增加宽度
        StringBuilder strBuilder = new StringBuilder(str);
        while (subStringWidth <= width) {
            strBuilder.append("　");
            subStringWidth += fontWidth;
            bool = true;
        }
        str = strBuilder.toString();
        float num1 = subStringWidth - width;
        if (bool && num1 >= 0) {
            if (num1 > numWidth) {
                str = str.substring(0, str.length() - 1);
            } else {
                str = str.substring(0, str.length() - 1) + " ";
            }
        }
        return str;
    }

    /**
     * 分行
     *
     * @param width 剩余宽度
     * @param str   字符串
     * @return List<String>
     * @author zhouxinlei
     * @date 2020-05-08 15:30:59
     */
    public List<String> subText(float width, String str) {
        // 定义分行的文本数组
        List<String> list = new ArrayList<>();
        // 定义初始文本宽度
        float subStringWidth = 0;
        // 定义初始截取字符串
        String subString = "";
        // 定义初始字符下标
        int index = 0;
        String regEx = "[(a-zA-Z0-9)]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = null;
        // 循环截取字符 直到宽度不够
        while (subStringWidth <= width && str.length() > index) {
            // 获取对应下标的字符
            String c = String.valueOf(str.charAt(index));
            matcher = pattern.matcher(c);
            boolean rs = matcher.find();
            // 追加到截取字符串
            subString += c;
            // 判断是否是英文数字或者空格 针对不同的类型追加宽度
            if (rs) {
                subStringWidth += numWidth;
            } else {
                subStringWidth += fontWidth;
            }
            // 截取下标往后挪
            index++;
        }
        int subStringLength = subString.length();
        // 去掉截取字符超出的一位
        if (subStringWidth - width > reserveWidth) {
            subStringLength -= 1;
            subString = subString.substring(0, subStringLength);
        }
        String leftStr = str.substring(subStringLength, str.length());
        // 加入文本List
        list.add(subString);
        // 将剩余文本加入文本List
        list.add(leftStr);
        return list;
    }

    /**
     * 打印时间
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:31:28
     */
    public void printTime(String year, String month, String day) {
        printLineOne(year, 40, false, true, false);
        addText("年");
        printLineOne(month, 25, false, true, false);
        addText("月");
        printLineOne(day, 25, false, true, false);
        addText("日");
    }

    /**
     * 添加印章内容
     *
     * @param text 内容
     * @param top  高度
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:31:54
     */
    public void addSeal(String text, float top) {
        if (StringUtils.isNotEmpty(text)) {
            super.addPrintText(top, 360, fontSize, text);
        }
        currLeft += 144;
    }

    /**
     * 换行
     *
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:33:12
     */
    public void wrap() {
        if (currLeft != left) {
            currLeft = left;
            top -= rowSpacing;
        }
    }

    /**
     * 空行
     *
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:33:21
     */
    public void newLine() {
        top -= rowSpacing;
    }

    /**
     * 添加头部内容
     *
     * @param data 内容
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:34:15
     */
    public void addHeadContent(String data) {
        addHead(data);
        top = top - 43;
    }

    /**
     * 添加主标题
     *
     * @param topSpacing 向上高度
     * @param title      标题
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:34:43
     */
    public void addMainTitle(float topSpacing, String title) {
        documentInfo.setTitle(title);
        if (topSpacing != 0) {
            addParagraphText(title, 20, 0, topSpacing, true, true, false);
        } else {
            addParagraphText(title, 20, 0, 10, true, true, false);
        }
        top = top - 30;
    }

    /**
     * 添加副标题
     *
     * @param topSpacing 向上高度
     * @param title      标题
     * @return void
     * @author zhouxinlei
     * @date 2020-05-08 15:35:18
     */
    public void addSubTitle(float topSpacing, String title) {
        addParagraphText(title, 13, 0, 0, false, true, false);
        top = top - 30;
    }

    /**
     * 添加页脚
     *
     * @param topSpacing 向上高度
     * @param title      标题
     * @return void
     * @throws
     * @author zhouxinlei
     * @date 2020-05-08 15:35:49
     */
    public void addFooter(float topSpacing, String title) {
        addFooter(title, topSpacing);
        currLeft = 55;
        Map<String, Float> map = runSize(title, 500);
        // 当前宽度适合的fontSize
        float currFontSize = map.get("fontSize");
        addFixedPositionText(title, currFontSize, pageSize.getWidth() / 2 - 250, topSpacing - 20, false, true, false);
    }

    /**
     * 文书右侧内容(联名)
     *
     * @param lContent
     * @param bContent
     * @param first
     */
    public void addRightBottomContent(String lContent, String bContent, boolean first) {
        /*************** 文书右侧内容(联名) ******************/
        float length = getStrWidth(lContent, fontWidth, numWidth) * 2;
        float end = (580 - length) / 2;
        float top = (380 - getStrWidth(lContent, fontWidth, numWidth)) / 2;
        if (!"".equals(lContent) && !"　".equals(lContent)) {
            addVerticalLine(0.5f, 565, 680, 690 - end);
            addVerticalText(end + 120, 558, fontSize, lContent);
            addVerticalLine(0.5f, 565, 680 - end - length, 100);
        }
        // 底部内容
        if (bContent != null && !"".equals(bContent)) {
            addFooter(50, bContent);
        }
    }

    /**
     * 添加尾部内容
     */
    public void addPage() {
        addText("注：本页不够，可以另附页。");
        currLeft = 520;
        addText("共");
        printLineOne(String.valueOf(pageCount), 2 * fontWidth, false, true, false);
        addText("页　第");
        printLineOne(String.valueOf(currPage), 2 * fontWidth, false, true, false);
        addText("页");
    }

    //是否退出文本打印
    public boolean isQuit = false;

    /***
     * 文书内容超出本页
     * @param str 打印文本内容
     * @param minTextCount 打印下划线预长度
     * @param isFullLine 是否添加下划线
     * @param isCenter 添加下划线是否整行
     * @param isPrint 是否打印文本内容
     * @param writTop 打印最低高度
     */
    public void addLineTextNextPage(String str, float minTextCount, boolean isFullLine, boolean isCenter, boolean isPrint, float writTop) {
        isQuit = false;
        if (str == null) {
            str = "";
        }
        str = str.toString();
        // 有要求最小下划线长度的情况下
        if (minTextCount != 0) {
            str = fullText(str, minTextCount * fontWidth);
        }
        // 求出字符串所占宽度
        float width = getStrWidth(str, fontWidth, numWidth);
        // 得到当前行剩余宽度
        float currWidth = totalWidth - currLeft;
        //判断是否打印
        if (!isQuit) {
            // 判断是否要换行
            if (currWidth < width) {
                // 对文本进行分行
                List<String> arrText = subText(currWidth, str);
                //超出值加页
                nextPageContent(str, writTop);
                //当打印的文本超出本页时退出，停止打印。
                if (isQuit) {
                    return;
                }
                //判断该文本是否打印
                if (isPrint) {
                    // 打印
                    printLineOne(arrText.get(0), currWidth, isFullLine, isCenter, false);
                }
                // 初始左边距默认值
                currLeft = left;
                // 换行 上边距追加
                top -= rowSpacing;
                if (!isQuit) {
                    // 打印剩下的文本
                    addLineTextNextPage(arrText.get(1), 0, isFullLine, isCenter, isPrint, writTop);
                }
            } else {
                // 不换行的情况下
                if (!isCenter) {// 不要求末行为整行
                    //超出值加页
                    nextPageContent(str, writTop);
                    //当打印的文本超出本页时退出，停止打印。
                    if (isQuit) {
                        return;
                    }
                    //判断该文本是否打印
                    if (isPrint) {
                        // 打印
                        printLineOne(str, width, isFullLine, isCenter, false);
                    }
                } else {// 要求末行为整行
                    // 添加字符串长度为整行长度，以全角空格填充
                    str = fullText(str, currWidth);
                    //超出值加页
                    nextPageContent(str, writTop);
                    //当打印的文本超出本页时退出，停止打印。
                    if (isQuit) {
                        return;
                    }
                    //判断该文本是否打印
                    if (isPrint) {
                        // 打印
                        printLineOne(str, currWidth, isFullLine, isCenter, false);
                    }
                    // 初始左边距默认值
                    currLeft = left;
                    // 换行上边距追加
                    top -= rowSpacing;
                }
            }
        }
    }

    /***
     *文本内容超过定制高度添加附页，保存下页内容。
     * @param str 文本内容
     * @param writTop 定制高度
     */
    public void nextPageContent(String str, float writTop) {
        if (top < writTop) {
            isQuit = true;
            arrText.add(str);
            currPage++;
        }
    }

    /**
     * 初始化页面参数
     */
    public void initDefaultParam() {
        top = 785;
        totalWidth = 550;
        left = 55;
        currLeft = 55;
        bindLeft = 35;
        fontSize = 13;
        fontWidth = 14;
        numWidth = 9;
        rowSpacing = 23;
        reserveWidth = 0;
    }

    /**
     * 根据宽度调整字体大小和文本位置
     *
     * @param str   字符串
     * @param width 宽度
     * @return Map<String, Float>
     * @author zhouxinlei
     * @date 2020-05-08 16:28:26
     */
    public Map<String, Float> runSize(String str, float width) {
        float[] fontSize = {13, 12, 11, 10, 9, 8, 7, 6};
        float[] topSpacing = {0, 0, 0, (float) 2.9, 3, 4, 5, 6};
        float[] fontWidth = {12, 11, 10, 9, 8, 7, 6, 5};
        float[] numWidth = {7, (float) 6.5, 6, 5, (float) 4.5, 4, (float) 3.5, 3};
        float[] rowSpacing = {28, 26, 22, 18, 16, 12, 8, 4};
        int index = 0;
        while (true) {
            float strWidth = getStrWidth(str, fontWidth[index], numWidth[index]);
            if (strWidth > width && index < 7) {
                index++;
            } else {
                break;
            }
        }
        return getFontFloatMap(fontSize, topSpacing, fontWidth, numWidth, rowSpacing, index);
    }

    private Map<String, Float> getFontFloatMap(float[] fontSize, float[] topSpacing, float[] fontWidth, float[] numWidth, float[] rowSpacing,
                                               int index) {
        Map<String, Float> map = new HashMap<>();
        map.put("fontSize", fontSize[index]);
        map.put("topSpacing", topSpacing[index]);
        map.put("fontWidth", fontWidth[index]);
        map.put("numWidth", numWidth[index]);
        map.put("rowSpacing", rowSpacing[index]);
        return map;
    }


    /**
     * 根据框架的高宽调整文字的大小与位置
     *
     * @param str     字符串
     * @param downTop 下边距高度
     * @return
     */
    public Map<String, Float> runHeightWidthSize(String str, float downTop) {
        float[] fontSize = {13, 12, 11, 10, 9, 8, 7, 6};
        float[] topSpacing = {0, 0, 0, (float) 2.9, 3, 4, 5, 6};
        float[] fontWidth = {12, 11, 10, 9, 8, 7, 6, 5};
        float[] numWidth = {7, (float) 6.5, 6, 5, (float) 4.5, 4, (float) 3.5, 3};
        float[] rowSpacing = {28, 26, 22, 18, 16, 12, 8, 4};
        int index = 0;
        //获取宽度高度为上高为top、downTop、左left、宽度totalWidtht的数据计算此框架内文字的宽度
        while (true) {
            //内容宽度
            float contentWidth = totalWidth - left;
            //内容高度
            float contentHeight = top - downTop;
            //行数
            float rowNum = contentHeight / rowSpacing[index];
            //去掉小数
            float rowNumber = Math.round(rowNum);
            //根据行高调整的内容宽度
            float width = rowNumber * contentWidth;
            float strWidth = getStrWidth(str, fontWidth[index], numWidth[index]);
            if (strWidth > width && index < 7) {
                index++;
            } else {
                break;
            }
        }
        return getFontFloatMap(fontSize, topSpacing, fontWidth, numWidth, rowSpacing, index);
    }


    /**
     * 求页数
     *
     * @param textCount
     * @param rows
     * @param str
     */
    public void getPageCount(float textCount, float rows, String str) {
        arrText.clear();
        float w1 = (textCount * fontWidth) % rowWidth;
        double rs = Math.ceil((textCount * fontWidth) / rowWidth);
        List<String> texts = subText(w1, str);
        for (int i = 0; i < rs; i++) {
            List<String> arr1 = subText(rowWidth, texts.get(1));
            String text0 = texts.get(0);
            text0 += arr1.get(0);
            texts.set(0, text0);
            String text1 = arr1.get(1);
            texts.set(1, text1);
        }
        arrText.add(texts.get(0));
        while (texts.get(1).length() != 0) {
            pageCount++;
            texts.set(0, "");
            for (int i = 0; i < rows; i++) {
                List<String> arr1 = subText(rowWidth, texts.get(1));
                String text0 = texts.get(0);
                text0 += arr1.get(0);
                texts.set(0, text0);
                String text1 = arr1.get(1);
                texts.set(1, text1);
            }
            arrText.add(texts.get(0));
        }
    }

    /**
     * 添加底部内容
     *
     * @param content
     * @param isPage
     */
    public void addFootContent(String content, boolean isPage, Float currentTop) {
        if (content != null && !"".equals(content)) {
            top = 70;
            addFooter(content, top);
            currLeft = 55;
            Map<String, Float> map = runSize(content, 500);
            // 当前宽度适合的fontSize
            float currFontSize = map.get("fontSize");
            top -= rowSpacing;
            addFixedPositionText(content, currFontSize, pageSize.getWidth() / 2 - 250, top, false, true, false);
            top -= 15;
        } else {
            if (currentTop != null) {
                top = currentTop;
            } else {
                top = 30;
            }
        }
        currLeft = 420;
        if (isPage) {
            addText("共");
            printLineOne(String.valueOf(pageCount), 2 * fontWidth, false, true, false);
            addText("页　第");
            printLineOne(String.valueOf(currPage), 2 * fontWidth, false, true, false);
            addText("页");
        }
    }

    /**
     * 求字符串的宽度
     *
     * @param str
     * @param fontWidth
     * @param numWidth
     * @return
     */
    public Float getStrWidth(String str, float fontWidth, float numWidth) {
        Float strWidth = 0f;
        String regEx = "[(a-zA-Z0-9)]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = null;
        for (int i = 0; i < str.length(); i++) {
            // 获取对应下标的字符
            String c = String.valueOf(str.charAt(i));
            matcher = pattern.matcher(c);
            boolean rs = matcher.find();
            // 判断是否是英文数字或者空格 针对不同的类型追加宽度
            if (rs) {
                strWidth += numWidth;
            } else {
                strWidth += fontWidth;
            }
        }
        return strWidth;
    }

    /**
     * 计算文本居中应该移动的宽度
     *
     * @param str
     * @param width
     * @param fontWidth
     * @param numWidth
     * @return
     */
    public Float textCenter(String str, float width, float fontWidth, float numWidth) {
        String reg = "/^[ ]*|[ ]*$/g";
        String reg1 = "/^[　]*|[　]*$/g";
        str = str.replace(reg, "");
        str = str.replace(reg1, "");
        float getWidth = getStrWidth(str, fontWidth, numWidth);
        width = width - getWidth;
        if (width > 0) {
            return width / 2;
        }
        return (float) 0;
    }

    /**
     * text文本大小适配
     *
     * @param str
     * @param leftLine
     * @param rightLine
     * @param downLine
     */
    public void autoAddTopText(String str, float leftLine, float rightLine, float downLine) {
        if (str == null || str == "") {
            str = "";
        }
        str = str.toString();
        //求出字符串所占宽度
        Float width = getStrWidth(str, fontWidth, numWidth);
        //得到当前行剩余宽度
        Float currWidth = rightLine - leftLine;
        //判断是否要换行
        if (currWidth < width) {
            //对文本进行分行
            List<String> arrText = subText(currWidth, str);
            //打印
            printLineOne(arrText.get(0), currWidth, false, false, false);
            //初始左边距默认值
            currLeft = leftLine;
            Map<String, Float> map = runSize(str, width);
            float addTop = map.get("rowSpacing");
            //换行上边距追加
            top += addTop;
            //打印剩下的文本
            autoAddTopText(arrText.get(1), leftLine, rightLine, downLine);
        } else {//不换行的情况下
            //打印
            printLineOne(str, width, false, false, false);

        }
    }

    /**
     * 添加行文本自动大小
     *
     * @param str
     * @param minTextCount
     * @param isFullLine
     * @param isCenter
     */
    public void addAutoLineText(String str, int minTextCount, boolean isFullLine, boolean isCenter) {
        if (str == null || str == "") {
            str = "";
        }
        str = str.toString();
        //有要求最小下划线长度的情况下
        if (minTextCount != 0) {
            str = fullText(str, minTextCount * fontWidth);
        }
        //求出字符串所占宽度
        float width = getStrWidth(str, fontWidth, numWidth);

        //得到当前行剩余宽度
        float currWidth = totalWidth - currLeft;
        //判断是否要换行
        if (currWidth < width) {
            //对文本进行分行
            List<String> arrText = subText(currWidth, str);
            //打印
            printLineOne(arrText.get(0), currWidth, true, isCenter, false);
            //初始左边距默认值
            currLeft = left;
            //换行 上边距追加
            top += rowSpacing;
            realTop = top;
            //打印剩下的文本
            addLineText(arrText.get(1), 0, isFullLine, isCenter);
        } else { //不换行的情况下
            if (!isFullLine) {//不要求末行为整行
                //打印
                printLineOne(arrText.get(0), currWidth, false, isCenter, false);
            } else {//要求末行为整行

                //添加字符串长度为整行长度，以全角空格填充
                str = fullText(str, currWidth);
                //打印
                printLineOne(arrText.get(0), currWidth, true, isCenter, false);

                //初始左边距默认值
                currLeft = left;
                //换行上边距追加
                top += rowSpacing;
                realTop = top;
            }
        }
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {

            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        return new String(c);
    }
}
