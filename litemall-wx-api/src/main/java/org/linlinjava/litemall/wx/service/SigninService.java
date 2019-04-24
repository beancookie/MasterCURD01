package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.domain.LitemallIntegral;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallIntegralService;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.util.IntegralConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SigninService {
    @Autowired
    private LitemallIntegralService integralService;

    @Resource
    private LitemallUserMapper userMapper;

    /**
     * 每日签到
     * 签到成功return true
     * 当天已经签到过return false
     * @param userId
     * @return
     */
    public boolean signin(Integer userId) {
        if (integralService.isSignin(userId)) {
            return false;
        }
        LitemallIntegral signinIntegral = new LitemallIntegral();
        signinIntegral.setIntegral(integralService.signinIntegral());
        signinIntegral.setUserId(userId);
        signinIntegral.setType(IntegralConstant.SIGNIN);

        /**
         * 更新用户积分
         */
        LitemallUser user = userMapper.selectByPrimaryKey(userId);
        user.setIntegral(user.getIntegral() + signinIntegral.getIntegral());
        userMapper.updateByPrimaryKey(user);
        return integralService.add(signinIntegral) > 0;
    }

    /**
     * 获取签到总天数
     * @param userId
     * @return
     */
    public long signinTotal(Integer userId) {
        return integralService.signinTotal(userId);
    }
}
