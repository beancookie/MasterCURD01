package org.linlinjava.litemall.db.util;

public class IntegralConstant {
    /**
     * 签到积分
     */
    public static final Short SIGNIN = 0;


    /**
     * 管理修改积分
     */
    public static final Short ADMIN = 1;

    /**
     * 签到类型
     * 0 随即积分
     * 1 固定积分
     */
    public static final String SIGNIN_TYPE_KEY = "litemall_signin_type";

    /**
     * 固定签到分数
     */
    public static final String SIGNIN_INTEGRAL_KEY = "litemall_signin_integral";

    /**
     * 随机签到
     */
    public static final Short SIGNIN_RANDOM = 0;

}
