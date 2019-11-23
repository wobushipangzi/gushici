package com.gushici.common.utils;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class ParseWordUtils {

    private static Logger logger = Logger.getLogger(ParseWordUtils.class);

    /**
     * 解析文字
     * @param words  未解析的文字
     * @return
     */
    public static String parseWord(String words) {

        logger.info("开始解析文字......");

        String[] wordStrs = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
                            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                            "0","1","2","3","4","5","6","7","8","9","0","\\s*"};

        for (String wordStr : wordStrs) {
            words = words.replaceAll(wordStr,"");
        }

        String[] wordsplit = words.split("，");
        for (String word : wordsplit) {
            if(StringUtils.isNotEmpty(word) && !word.equals("。") && !word.equals("！") && !word.equals("？") && !word.equals("：")
                    && !word.equals(",") && !word.equals("、") && !word.equals("!") && !word.equals("?") && !word.equals(":") && !word.equals("·")){
                words = word;
                break;
            }
        }

        wordsplit = words.split("。");
        for (String word : wordsplit) {
            if(StringUtils.isNotEmpty(word) && !word.equals("。") && !word.equals("！") && !word.equals("？") && !word.equals("：")
                    && !word.equals(",") && !word.equals("、") && !word.equals("!") && !word.equals("?") && !word.equals(":") && !word.equals("·")){
                words = word;
                break;
            }
        }

        wordsplit = words.split("！");
        for (String word : wordsplit) {
            if(StringUtils.isNotEmpty(word) && !word.equals("。") && !word.equals("！") && !word.equals("？") && !word.equals("：")
                    && !word.equals(",") && !word.equals("、") && !word.equals("!") && !word.equals("?") && !word.equals(":") && !word.equals("·")){
                words = word;
                break;
            }
        }

        wordsplit = words.split("？");
        for (String word : wordsplit) {
            if(StringUtils.isNotEmpty(word) && !word.equals("。") && !word.equals("！") && !word.equals("？") && !word.equals("：")
                    && !word.equals(",") && !word.equals("、") && !word.equals("!") && !word.equals("?") && !word.equals(":") && !word.equals("·")){
                words = word;
                break;
            }
        }

        wordsplit = words.split("：");
        for (String word : wordsplit) {
            if(StringUtils.isNotEmpty(word) && !word.equals("。") && !word.equals("！") && !word.equals("？") && !word.equals("：")
                    && !word.equals(",") && !word.equals("、") && !word.equals("!") && !word.equals("?") && !word.equals(":") && !word.equals("·")){
                words = word;
                break;
            }
        }

        wordsplit = words.split(",");
        for (String word : wordsplit) {
            if(StringUtils.isNotEmpty(word) && !word.equals("。") && !word.equals("！") && !word.equals("？") && !word.equals("：")
                    && !word.equals(",") && !word.equals("、") && !word.equals("!") && !word.equals("?") && !word.equals(":") && !word.equals("·")){
                words = word;
                break;
            }
        }

        wordsplit = words.split("、");
        for (String word : wordsplit) {
            if(StringUtils.isNotEmpty(word) && !word.equals("。") && !word.equals("！") && !word.equals("？") && !word.equals("：")
                    && !word.equals(",") && !word.equals("、") && !word.equals("!") && !word.equals("?") && !word.equals(":") && !word.equals("·")){
                words = word;
                break;
            }
        }

        wordsplit = words.split("!");
        for (String word : wordsplit) {
            if(StringUtils.isNotEmpty(word) && !word.equals("。") && !word.equals("！") && !word.equals("？") && !word.equals("：")
                    && !word.equals(",") && !word.equals("、") && !word.equals("!") && !word.equals("?") && !word.equals(":") && !word.equals("·")){
                words = word;
                break;
            }
        }

        wordsplit = words.split(":");
        for (String word : wordsplit) {
            if(StringUtils.isNotEmpty(word) && !word.equals("。") && !word.equals("！") && !word.equals("？") && !word.equals("：")
                    && !word.equals(",") && !word.equals("、") && !word.equals("!") && !word.equals("?") && !word.equals(":") && !word.equals("·")){
                words = word;
                break;
            }
        }

        logger.info("解析后的文字出参为：==>" + words);
        return words;
    }

}
