package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;
import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserService userService;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //1.校验手机号
        if(RegexUtils.isPhoneInvalid(phone)) {
            //2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        //3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        //4.保存验证码到redis 加上业务前缀保证业务层次 set key value ex 120
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        //5.发送验证码
        log.debug("发送短信验证码成功，验证码：{}", code);
        //6，返回ok
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        //1.校验手机号 不同请求进行不同的校验（发送完验证码之后又改了手机号的情况）
        String phone = loginForm.getPhone();
        if(RegexUtils.isPhoneInvalid(phone)) {
            //2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        //3.从redis中获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = loginForm.getCode();
        if(cacheCode == null || !cacheCode.equals(code)) {
            //不一致，返回错误信息
            return Result.fail("验证码错误！");
        }

        //4.一致，根据手机号查询用户 select * from user where phone = ? 使用mybatisPlus
        User user = query().eq("phone", phone).one();

        //5.判断用户是否存在
        if (user == null) {
            //6.不存在，创建新用户并保存到数据库
            user = createUserWithPhone(phone);
        }

        //7.保存用户信息到redis中
        //7.1.随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        //7.2.将UserDTO对象转为Hash存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);//这行代码的作用是将 User 实体对象转换为 UserDTO 数据传输对象，避免敏感字符传递
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        //7.3.存储
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        //7.4.设置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        //8.返回token
        return Result.ok(token);
    }

    private User createUserWithPhone(String phone) {
        //1.创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));

        //2.保存用户
        save(user);
        return user;
    }

    public void batchInsertUsersToRedis() {
        // 获取所有用户数据
        List<User> users = userService.list();

        for (User user : users) {
            // 1. 生成token
            String token = UUID.randomUUID().toString(true);

            // 2. 转换用户对象为DTO
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

            // 3. 转换为Map格式用于Hash存储
            Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

            // 4. 存储到Redis
            String tokenKey = LOGIN_USER_KEY + token;
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);

            // 5. 设置过期时间
            stringRedisTemplate.expire(tokenKey, 36000L, TimeUnit.MINUTES);

            // 记录token与用户ID的映射关系，方便查找
            //stringRedisTemplate.opsForValue().set(LOGIN_USER_KEY + "id:" + user.getId(), token, LOGIN_USER_TTL, TimeUnit.MINUTES);
        }
    }

    public void exportTokensFromRedisToFile() {
        try {
            // 指定输出文件
            String tokenFilePath = "redis_tokens.txt";

            // 清空文件内容
            try (PrintWriter writer = new PrintWriter(new FileWriter(tokenFilePath, false))) {
                writer.print("");
            }

            // 获取所有匹配的keys
            Set<String> keys = stringRedisTemplate.keys(LOGIN_USER_KEY + "*");

            if (keys == null || keys.isEmpty()) {
                log.info("Redis中没有找到用户token数据");
                return;
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(tokenFilePath, true))) {
                for (String key : keys) {
                    // 从Redis Hash中获取用户数据
                    Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);

                    if (!userMap.isEmpty()) {
                        // 提取用户信息
                        String userId = (String) userMap.get("id");
                        String phone = (String) userMap.get("phone");

                        // 从key中提取token (去除前缀)
                        String token = key.substring(LOGIN_USER_KEY.length());

                        // 写入文件
                        writer.println(token);

                        log.debug("导出用户token: userId={}, token={}", userId,  token);
                    }
                }

                log.info("共导出 {} 个用户token到文件 {}", keys.size(), tokenFilePath);
            }

        } catch (Exception e) {
            log.error("从Redis导出token到文件失败", e);
        }
    }
}
