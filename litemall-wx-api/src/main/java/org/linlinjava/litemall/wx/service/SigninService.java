package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.db.domain.LitemallIntegral;
import org.linlinjava.litemall.db.service.LitemallIntegralService;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SigninService {
    @Autowired
    private LitemallIntegralService integralService;

    @Autowired
    private LitemallSystemConfigService configService;

    public boolean signin(Integer userId) {
        if (integralService.isSignin(userId)) {
            return false;
        }
        LitemallIntegral integral = new LitemallIntegral();
        return integralService.add(integral) > 0;
    }

    public long signinTotal(Integer userId) {
        return integralService.signinTotal(userId);
    }
}
