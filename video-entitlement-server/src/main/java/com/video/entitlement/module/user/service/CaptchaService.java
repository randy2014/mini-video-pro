package com.video.entitlement.module.user.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.module.user.dto.CaptchaVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

/**
 * 图形验证码服务：生成图片验证码并存入 Redis，登录时校验。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final int EXPIRE_MINUTES = 2;
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final StringRedisTemplate stringRedisTemplate;

    /** 生成图形验证码，返回 key 与 base64 图片 */
    public CaptchaVO generate() {
        String code = randomCode(4);
        BufferedImage image = drawImage(code);
        String base64 = encode(image);
        String key = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(CAPTCHA_PREFIX + key, code, Duration.ofMinutes(EXPIRE_MINUTES));
        return CaptchaVO.builder()
                .captchaKey(key)
                .imageBase64("data:image/png;base64," + base64)
                .build();
    }

    /** 校验图形验证码（一次性，校验成功后删除） */
    public void validate(String key, String code) {
        if (key == null || code == null || key.isBlank() || code.isBlank()) {
            throw new BusinessException(ErrorCode.CAPTCHA_INVALID);
        }
        String stored = stringRedisTemplate.opsForValue().get(CAPTCHA_PREFIX + key);
        if (stored == null) {
            throw new BusinessException(ErrorCode.CAPTCHA_EXPIRED);
        }
        if (!stored.equalsIgnoreCase(code)) {
            throw new BusinessException(ErrorCode.CAPTCHA_INVALID);
        }
        stringRedisTemplate.delete(CAPTCHA_PREFIX + key);
    }

    private String randomCode(int len) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(r.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private BufferedImage drawImage(String code) {
        int w = 120, h = 40;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        Random r = new Random();
        // 干扰线
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 6; i++) {
            g.drawLine(r.nextInt(w), r.nextInt(h), r.nextInt(w), r.nextInt(h));
        }
        // 字符（每个字随机颜色，增加辨识难度）
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(r.nextInt(160), r.nextInt(160), r.nextInt(160)));
            g.drawString(String.valueOf(code.charAt(i)), 18 + i * 24, 30);
        }
        g.dispose();
        return image;
    }

    private String encode(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.error("captcha encode failed", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图形码生成失败");
        }
    }
}
