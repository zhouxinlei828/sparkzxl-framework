package com.sparksys.commons.core.utils.pdf;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePartDataSource;

/**
 * description: Mht2Html转换工具类
 *
 * @author zhouxinlei
 * @date  2020-05-24 13:13:53
 */
@Slf4j
public class Mht2HtmlUtil {

    public static void main(String[] args) {
        mht2html("http://www.en2hr.com/Resume/20180411/b22ff028-bae7-46a7-b94a-7d76d1b5b599.mht", "C:\\test2\\test.html");
    }

    /**
     * 将 mht文件转换成 html文件
     *
     * @param srcMht   源mht文件
     * @param descHtml 目标html
     * @return void
     */
    public static void mht2html(String srcMht, String descHtml) {
        try {
            InputStream fis = new URL(srcMht).openStream();
            Session mailSession = Session.getDefaultInstance(System.getProperties(), null);
            MimeMessage msg = new MimeMessage(mailSession, fis);
            Object content = msg.getContent();
            if (content instanceof Multipart) {
                MimeMultipart mp = (MimeMultipart) content;
                MimeBodyPart bp1 = (MimeBodyPart) mp.getBodyPart(0);
                //获取mht文件内容代码的编码
                String strEncode = getEncoding(bp1);
                //获取mht文件的内容
                String strText = getHtmlText(bp1, strEncode);
                if (strText == null) {
                    return;
                }
                //创建以mht文件名称的文件夹，主要用来保存资源文件。
                File parent = null;
                if (mp.getCount() > 1) {
                    parent = new File(new File(descHtml).getAbsolutePath() + ".files");
                    parent.mkdirs();
                    //创建文件夹失败的话则退出
                    if (!parent.exists()) {
                        return;
                    }
                }
                for (int i = 1; i < mp.getCount(); ++i) {
                    MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(i);
                    String strUrl = getResourcesUrl(bp);
                    if (strUrl == null || strUrl.length() == 0) {
                        continue;
                    }
                    DataHandler dataHandler = bp.getDataHandler();
                    MimePartDataSource source = (MimePartDataSource) dataHandler.getDataSource();
                    //获取资源文件的绝对路径
                    assert parent != null;
                    String filePath = parent.getAbsolutePath() + File.separator + getName(strUrl, i);
                    File resources = new File(filePath);
                    //保存资源文件
                    if (saveResourcesFile(resources, bp.getInputStream())) {
                        //将远程地址替换为本地地址  如图片、JS、CSS样式等等
                        strText = strText.replace(strUrl, resources.getAbsolutePath());
                    }
                }
                //最后保存HTML文件
                saveHtml(strText, descHtml, strEncode);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 获取mht文件内容中资源文件的名称
     *
     * @param strName
     * @param ID
     * @return
     */
    public static String getName(String strName, int ID) {
        char separator1 = '/';
        char separator2 = '\\';
        //将换行替换
        strName = strName.replaceAll("\r\n", "");

        //获取文件名称
        if (strName.lastIndexOf(separator1) >= 0) {
            return strName.substring(strName.lastIndexOf(separator1) + 1);
        }
        if (strName.lastIndexOf(separator2) >= 0) {
            return strName.substring(strName.lastIndexOf(separator2) + 1);
        }
        return "";
    }


    /**
     * 将提取出来的html内容写入保存的路径中
     *
     * @param htmlText
     * @param htmlPath
     * @param encode
     * @author zhouxinlei
     * @date 2020-05-15 15:21:02
     */
    public static void saveHtml(String htmlText, String htmlPath, String encode) {
        try {
            Writer out;
            out = new OutputStreamWriter(new FileOutputStream(htmlPath, false), encode);
            out.write(htmlText);
            out.close();
        } catch (Exception ignored) {
        }
    }


    /**
     * 保存网页中的JS、图片、CSS样式等资源文件
     *
     * @param srcFile     源文件
     * @param inputStream 输入流
     * @return boolean
     */
    private static boolean saveResourcesFile(File srcFile, InputStream inputStream) {
        if (srcFile == null || inputStream == null) {
            return false;
        }

        BufferedInputStream in = null;
        FileOutputStream fio = null;
        BufferedOutputStream osw = null;
        try {
            in = new BufferedInputStream(inputStream);
            fio = new FileOutputStream(srcFile);
            osw = new BufferedOutputStream(new DataOutputStream(fio));
            int index = 0;
            byte[] a = new byte[1024];
            while ((index = in.read(a)) != -1) {
                osw.write(a, 0, index);
            }
            osw.flush();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
                if (fio != null) {
                    fio.close();
                }
                if (in != null) {
                    in.close();
                }
                inputStream.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }


    /**
     * 获取mht文件里资源文件的URL路径
     *
     * @param bp
     * @return String
     * @author zhouxinlei
     * @date 2020-05-15 15:19:21
     */
    private static String getResourcesUrl(MimeBodyPart bp) {
        if (bp == null) {
            return null;
        }
        try {
            Enumeration list = bp.getAllHeaders();
            while (list.hasMoreElements()) {
                javax.mail.Header head = (javax.mail.Header) list.nextElement();
                if (head.getName().compareTo("Content-Location") == 0) {
                    return head.getValue();
                }
            }
            return null;
        } catch (MessagingException e) {
            return null;
        }
    }


    /**
     * 获取mht文件中的内容代码
     *
     * @param bp
     * @param strEncoding mht文件编码
     * @return String
     * @author zhouxinlei
     * @date 2020-05-15 15:19:38
     */
    private static String getHtmlText(MimeBodyPart bp, String strEncoding) {
        InputStream textStream = null;
        BufferedInputStream buff = null;
        BufferedReader br = null;
        Reader r = null;
        try {
            textStream = bp.getInputStream();
            buff = new BufferedInputStream(textStream);
            r = new InputStreamReader(buff, strEncoding);
            br = new BufferedReader(r);
            StringBuilder strHtml = new StringBuilder("");
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                strHtml.append(strLine).append("\r\n");
            }
            br.close();
            r.close();
            textStream.close();
            return strHtml.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (buff != null) {
                    buff.close();
                }
                if (textStream != null) {
                    textStream.close();
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 获取mht网页文件中内容代码的编码
     *
     * @param bp
     * @return
     */
    private static String getEncoding(MimeBodyPart bp) {
        if (bp == null) {
            return null;
        }
        try {
            Enumeration list = bp.getAllHeaders();
            while (list.hasMoreElements()) {
                javax.mail.Header head = (javax.mail.Header) list.nextElement();
                if (head.getName().compareTo("Content-Type") == 0) {
                    String strType = head.getValue();
                    int pos = strType.indexOf("charset=");
                    if (pos >= 0) {
                        String strEncoding = strType.substring(pos + 8);
                        if (strEncoding.startsWith("\"") || strEncoding.startsWith("\'")) {
                            strEncoding = strEncoding.substring(1);
                        }
                        if (strEncoding.endsWith("\"") || strEncoding.endsWith("\'")) {
                            strEncoding = strEncoding.substring(0, strEncoding.length() - 1);
                        }
                        if (strEncoding.toLowerCase().compareTo("gb2312") == 0) {
                            strEncoding = "gbk";
                        }
                        return strEncoding;
                    }
                }
            }
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
