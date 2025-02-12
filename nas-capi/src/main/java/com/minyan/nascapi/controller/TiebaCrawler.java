package com.minyan.nascapi.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiebaCrawler {

    public static void main(String[] args) {
        // 用于存储符合条件的评论
        List<String> resultList = new ArrayList<>();

        // 百度贴吧“王者荣耀吧”的 URL（kw 参数已 URL 编码）
        String forumUrl = "https://tieba.baidu.com/f?kw=%E7%8E%8B%E8%80%85%E8%8D%A3%E8%80%80&ie=utf-8";

        try {
            // 获取论坛首页的文档
            Document forumDoc = Jsoup.connect(forumUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/XX.X.X.X Safari/537.36")
                    .timeout(10000)
                    .get();

            // 选择论坛首页中的帖子链接
            // 百度贴吧帖子的标题链接一般带有类名 "j_th_tit"
            Elements threadLinks = forumDoc.select("a.j_th_tit");

            // 遍历每个帖子链接，直到找到 10 条符合条件的评论
            for (Element threadLink : threadLinks) {
                if (resultList.size() >= 10) {
                    break;
                }
                String href = threadLink.attr("href");
                // 构造帖子的完整 URL
                String threadUrl = "https://tieba.baidu.com" + href;

                // 获取帖子页面的文档
                Document threadDoc = Jsoup.connect(threadUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/XX.X.X.X Safari/537.36")
                        .timeout(10000)
                        .get();

                // 选择帖子中的评论内容（通常评论内容在 "div.d_post_content" 中）
                Elements comments = threadDoc.select("div.d_post_content");
                for (Element comment : comments) {
                    if (resultList.size() >= 10) {
                        break;
                    }
                    String text = comment.text();
                    // 判断评论中是否包含关键字 "小蛇糕"
                    if (text.contains("小蛇糕")) {
                        // 使用正则匹配三位数（单词边界，避免匹配到更多数字）
                        Pattern pattern = Pattern.compile("\\b(\\d{3})\\b");
                        Matcher matcher = pattern.matcher(text);
                        while (matcher.find()) {
                            String numStr = matcher.group(1);
                            try {
                                int number = Integer.parseInt(numStr);
                                // 判断三位数是否大于 900
                                if (number > 900) {
                                    resultList.add(text);
                                    break; // 当前评论符合条件，退出当前评论的数字匹配循环
                                }
                            } catch (NumberFormatException e) {
                                // 数字格式异常时忽略
                            }
                        }
                    }
                }
            }

            // 输出前十条匹配的评论
            System.out.println("匹配的评论：");
            for (String comment : resultList) {
                System.out.println(comment);
                System.out.println("--------------");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
