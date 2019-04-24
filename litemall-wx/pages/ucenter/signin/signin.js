var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

var app = getApp();

Page({
   /**
   * 页面的初始数据
   */
  data: {
    //img_url: config.imgUrl, //图片地址

    //签到模块
    signNum: 0,  //签到数
    signState: false, //签到状态
    min: 1,  //默认值日期第一天1
    max: 7,  //默认值日期最后一天7
    integralTotal: 0, // 总积分
    signinType: 0,
    integral: 0
  },

  //签到
  bindSignIn(e) {
    let that = this;
    util.request(api.Signin, {}, 'POST').then(function(res) {
      if (res.errno === 0) {
        wx.showToast({
          icon: 'success',
          title: '签到成功',
        })
        that.getSigninTotalAndIntegral()
      } else {
        wx.showToast({
          icon: 'none',
          title: '今日已签到',
        })
      }
    })
  },

  getSigninTotalAndIntegral() {
    let that = this;
    util.request(api.SigninTotalAndIntegral).then(function(res) {
      if (res.errno === 0) {
        let be = Math.floor(res.data.signinTotal / 7)
        that.setData({
          signNum: res.data.signinTotal,
          integralTotal: res.data.integralTotal,
          signinType: res.data.signinType,
          
          min: 7 * be == 0 ? 1 : 7 * be,
          max: 7 * be + 6 == 0 ? 1 : 7 * be + 6
        })
        if (res.data.signinType == 0) {
          that.setData({
            integral: '?'
          })
        } else {
          that.setData({
            integral: res.data.integral
          })
        }
        
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getSigninTotalAndIntegral()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    // 页面显示
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
})