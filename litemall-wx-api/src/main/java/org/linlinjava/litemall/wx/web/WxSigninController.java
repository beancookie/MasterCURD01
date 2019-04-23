package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 签到服务
 */
@RestController
@RequestMapping("/wx/signin")
@Validated
public class WxSigninController {
    /**
     * 获取总积分和连续签到天数
     *
     * @param userId 用户ID
     * @return 总积分和连续签到天数
     */
    @GetMapping
    public Object get(@LoginUser Integer userId) {
        return ResponseUtil.ok(null);
    }

    /**
     * 每日签到
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    @PostMapping
    public Object signin(@LoginUser Integer userId) {
        return ResponseUtil.ok(null);
    }
}
