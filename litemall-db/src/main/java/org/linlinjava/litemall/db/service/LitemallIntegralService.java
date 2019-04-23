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

    public int add(LitemallIntegral integral) {
        integral.setAddTime(LocalDateTime.now());
        return integralMapper.insertSelective(integral);
    }

    public boolean isSignin(Integer userId) {
        LitemallIntegralExample example = new LitemallIntegralExample();
        LitemallIntegralExample.Criteria criteria = example.createCriteria();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59);
        criteria.andUserIdEqualTo(userId)
                .andAddTimeBetween(begin, end)
                .andTypeEqualTo(IntegralConstant.SIGNIN);
        return integralMapper.selectOneByExample(example) == null;
    }

    public long signinTotal(Integer userId) {
        LitemallIntegralExample example = new LitemallIntegralExample();
        LitemallIntegralExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId)
                .andTypeEqualTo(IntegralConstant.SIGNIN);
        return integralMapper.countByExample(example);
    }

    public int signinIntegral() {
        Map<String, String> signinConfig = systemConfigService.listSignin();
        int type = Integer.parseInt(signinConfig.get(IntegralConstant.SIGNIN_TYPE_KEY));
        int integral = Integer.parseInt(signinConfig.get(IntegralConstant.SIGNIN_TYPE_KEY));
        if (type == IntegralConstant.SIGNIN_RANDOM) {
            return random.nextInt(3);
        }
        return integral;
    }
}
