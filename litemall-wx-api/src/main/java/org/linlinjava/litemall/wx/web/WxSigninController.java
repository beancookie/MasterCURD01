package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.util.IntegralConstant;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.SigninService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SigninService signinService;

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallSystemConfigService systemConfigService;

    /**
     * 获取总积分和总签到天数
     *
     * @param userId 用户ID
     * @return 总积分和连续签到天数
     */
    @GetMapping
    public Object getSigninTotalAndIntegral(@LoginUser Integer userId) {
        Map<Object, Object> data = new HashMap<>(2);
        data.put("signinTotal", signinService.signinTotal(userId));
        data.put("integralTotal", userService.findById(userId).getIntegral());
        Map<String, String> signinConfig = systemConfigService.listSignin();
        data.put("signinType", signinConfig.get(IntegralConstant.SIGNIN_TYPE_KEY));
        data.put("integral", signinConfig.get(IntegralConstant.SIGNIN_INTEGRAL_KEY));
        return ResponseUtil.ok(data);
    }

    /**
     * 每日签到
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    @PostMapping
    public Object signin(@LoginUser Integer userId) {
        boolean result = signinService.signin(userId);
        if (result) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.fail();
        }
    }
}
