package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallIntegralMapper;
import org.linlinjava.litemall.db.domain.LitemallIntegral;
import org.linlinjava.litemall.db.domain.LitemallIntegralExample;
import org.linlinjava.litemall.db.util.IntegralConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
public class LitemallIntegralService {
    @Resource
    private LitemallIntegralMapper integralMapper;

    @Autowired
    private LitemallSystemConfigService systemConfigService;

    private static final Random random = new Random();

    /**
     * 添加一条积分变化量
     *
     * @param integral 积分变化量
     * @return
     */
    public int add(LitemallIntegral integral) {
        integral.setAddTime(LocalDateTime.now());
        return integralMapper.insertSelective(integral);
    }

    /**
     * 是否已经签到
     * @param userId
     * @return
     */
    public boolean isSignin(Integer userId) {
        LitemallIntegralExample example = new LitemallIntegralExample();
        LitemallIntegralExample.Criteria criteria = example.createCriteria();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59);
        criteria.andUserIdEqualTo(userId)
                .andAddTimeBetween(begin, end)
                .andTypeEqualTo(IntegralConstant.SIGNIN);
        return integralMapper.selectOneByExample(example) != null;
    }

    /**
     * 签到总天数
     * @param userId
     * @return
     */
    public long signinTotal(Integer userId) {
        LitemallIntegralExample example = new LitemallIntegralExample();
        LitemallIntegralExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId)
                .andTypeEqualTo(IntegralConstant.SIGNIN);
        return integralMapper.countByExample(example);
    }

    /**
     * 根据配置表中的是否随即签订积分还是固定签到积分返回签到积分
     * @return
     */
    public int signinIntegral() {
        Map<String, String> signinConfig = systemConfigService.listSignin();
        int type = Integer.parseInt(signinConfig.get(IntegralConstant.SIGNIN_TYPE_KEY));
        int integral = Integer.parseInt(signinConfig.get(IntegralConstant.SIGNIN_INTEGRAL_KEY));
        if (type == IntegralConstant.SIGNIN_RANDOM) {
            return random.nextInt(3) + 1;
        }
        return integral;
    }
}
