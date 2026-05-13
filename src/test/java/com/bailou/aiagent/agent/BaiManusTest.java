package com.bailou.aiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BaiManusTest {

    @Resource
    private BaiManus baiManus;

    @Test
    public void run() {
        String userPrompt = """
                My partner lives in the Jing'an District of Shanghai. 
                Please help me find suitable date spots within 5 kilometers, 
                and combine them with some online pictures to create a detailed date plan, 
                and output it in PDF format.""";
        String answer = baiManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}