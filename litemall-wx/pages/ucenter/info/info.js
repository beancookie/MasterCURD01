var api = require('../../../config/api.js');
var check = require('../../../utils/check.js');

var app = getApp();
Page({
  data: {
    userinfo: {}
  },
  changeBirthday(e) {
    this.data.userinfo.birthday = e.detail.value
    this.setData({
      userinfo: this.data.userinfo
    })
  },
  bindgetphonenumber(e) {
    if (e.detail.errMsg !== "getPhoneNumber:ok") {
      // 拒绝授权
      return;
    }

    if (!this.data.hasLogin) {
      wx.showToast({
        title: '绑定失败：请先登录',
        icon: 'none',
        duration: 2000
      });
      return;
    }

    util.request(api.AuthBindPhone, {
      iv: e.detail.iv,
      encryptedData: e.detail.encryptedData
    }, 'POST').then(function(res) {
      if (res.errno === 0) {
        wx.showToast({
          title: '绑定手机号码成功',
          icon: 'success',
          duration: 2000
        });
      }
    });
  },
  onLoad: function(options) {
    if (app.globalData.hasLogin) {
      let userinfo = wx.getStorageSync('userInfo');
      userinfo.gender = userinfo.gender == 0 ? '女' : '男'
      this.setData({
        userinfo: userinfo
      });
    }
  },
  onReady: function() {

  },
  onShow: function() {
  },
  onHide: function() {
    // 页面隐藏

  },
  onUnload: function() {
    // 页面关闭

  }
})