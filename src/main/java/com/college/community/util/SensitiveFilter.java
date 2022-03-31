package com.college.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger=LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT="***";

    //根节点
    private TrieNode rootNode=new TrieNode();

    //初始化根节点，被@PostConstruct修饰的方法在容器实例化bean后会被自动调用
    @PostConstruct
    public void init(){
        try (
                //字节流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words");
                //字节流转字符流转缓冲流
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while ((keyword=reader.readLine())!=null){
                //添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
           logger.error("加载敏感词文件失败"+e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树中
    private void addKeyword(String keyword){
        //指针指向根节点
        TrieNode tempNode=rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c=keyword.charAt(i);
            //查看是否有子节点
            TrieNode subNode=tempNode.getSubNode(c);
            //如果是空的
            if (subNode==null){
                //初始化子节点
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //指针指向子节点，进入下一层循环
            tempNode=subNode;

            //设置结束标识
            if (i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    //前缀树
    private class TrieNode{

       //关键词结束标识
       private boolean isKeywordEnd=false;

       //子节点(key是下级字符，value是下级节点),Character:字符
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

    /**
     * g过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        //指针1，指向树
        TrieNode tempNode=rootNode;
        //指针2
        int begin=0;
        //指针3
        int postion=0;
        //结果
        StringBuilder sb=new StringBuilder();

        while (postion<text.length()){
            char c=text.charAt(postion);

            //跳过符号，避免干扰
            if (isSymbol(c)){
                //若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if (tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间，指针3向下走一步
                postion++;
                continue;
            }
            //检查下级节点
            tempNode=tempNode.getSubNode(c);
            if (tempNode==null){
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                postion=++begin;
                //重新指向根节点
                tempNode=rootNode;
            }else if (tempNode.isKeywordEnd()){
                //发现敏感词，将begin-position字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin=++postion;
                //重新指向根节点
                tempNode=rootNode;
            }else {
                //检查下一个字符
                postion++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){

        //CharUtils.isAsciiAlphanumeric(c)判断是否为合法字符
        //0x2E80-0x9FFF为东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c)&&(c < 0x2E80 || c>0x9FFF);
    }
}
