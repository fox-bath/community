package com.college.community;

import com.college.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text="这里可以赌博，嫖娼，吸毒，吃饭喝酒，哈哈哈哈哈哈哈";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
        //加入非法字符
        String text1="啦啦啦我是赌😥博的小行家，操你₯妈";
        text1 = sensitiveFilter.filter(text1);
        System.out.println(text1);
    }
}
