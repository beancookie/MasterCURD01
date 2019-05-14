package org.linlinjava.litemall.admin.vo;

import org.linlinjava.litemall.db.domain.LitemallCoupon;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;

/**
 * @author LuZhong
 * @date 2019/5/14 13:59
 * @description
 */
public class CouponUserVO {

    private LitemallCoupon coupon;
    private LitemallCouponUser couponUser;

    public LitemallCoupon getCoupon() {
        return coupon;
    }

    public void setCoupon(LitemallCoupon coupon) {
        this.coupon = coupon;
    }

    public LitemallCouponUser getCouponUser() {
        return couponUser;
    }

    public void setCouponUser(LitemallCouponUser couponUser) {
        this.couponUser = couponUser;
    }
}
