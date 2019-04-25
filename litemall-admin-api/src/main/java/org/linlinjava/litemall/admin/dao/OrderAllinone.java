package org.linlinjava.litemall.admin.dao;

/**
 * @author LuZhong
 * @date 2019/4/25 10:24
 * @description
 */
public class OrderAllinone {
    /**
     * 用户ID
     */
    Integer userId;
    /**
     * 收货人名称
     */
    String consignee;

    /**
     * 收货人手机号
     */
    String mobile;

    /**
     * 收货人地址
     */
    String address;

    GoodsAllinone goodsAllinone;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GoodsAllinone getGoodsAllinone() {
        return goodsAllinone;
    }

    public void setGoodsAllinone(GoodsAllinone goodsAllinone) {
        this.goodsAllinone = goodsAllinone;
    }
}
