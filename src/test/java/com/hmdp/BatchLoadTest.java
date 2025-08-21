package com.hmdp;

import com.hmdp.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BatchLoadTest {
    @Autowired
    private IUserService userService;

    @Test
    public void testBatchLoad() {
        userService.batchInsertUsersToRedis();
    }

    @Test
    public void testExportTokens() {
        // 从Redis导出token到文件
        userService.exportTokensFromRedisToFile();
    }
}