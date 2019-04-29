var app = getApp();
var WxParse = require('../../lib/wxParse/wxParse.js');
var util = require('../../utils/util.js');
var api = require('../../config/api.js');

Page({
  data: {
    ad: {
      name: '五一特价',
      gallery: [
        {}
      ]
    },
    openShare: false,
  },

  // 页面分享
  onShareAppMessage: function() {
    let that = this;
    return {
      title: that.data.ad.name,
      desc: that.data.ad.content,
      path: '/pages/index/index?goodId='
    }
  },

  shareFriendOrCircle: function() {
    //var that = this;
    if (this.data.openShare === false) {
      this.setData({
        openShare: !this.data.openShare
      });
    } else {
      return false;
    }
  },
  handleSetting: function(e) {
      var that = this;
      // console.log(e)
      if (!e.detail.authSetting['scope.writePhotosAlbum']) {
          wx.showModal({
              title: '警告',
              content: '不授权无法保存',
              showCancel: false
          })
          that.setData({
              canWrite: false
          })
      } else {
          wx.showToast({
              title: '保存成功'
          })
          that.setData({
              canWrite: true
          })
      }
  },
  // 保存分享图
  saveShare: function() {
    let that = this;
    wx.downloadFile({
      url: that.data.shareImage,
      success: function(res) {
        console.log(res)
        wx.saveImageToPhotosAlbum({
          filePath: res.tempFilePath,
          success: function(res) {
            wx.showModal({
              title: '存图成功',
              content: '图片成功保存到相册了，可以分享到朋友圈了',
              showCancel: false,
              confirmText: '好的',
              confirmColor: '#a78845',
              success: function(res) {
                if (res.confirm) {
                  console.log('用户点击确定');
                }
              }
            })
          },
          fail: function(res) {
            console.log('fail')
          }
        })
      },
      fail: function() {
        console.log('fail')
      }
    })
  },

  //从分享的团购进入
  getGrouponInfo: function(grouponId) {
    let that = this;
    util.request(api.GroupOnJoin, {
      grouponId: grouponId
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          grouponLink: res.data.groupon,
          id: res.data.goods.id
        });
        //获取商品详情
        that.getGoodsInfo();
      }
    });
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    if (options.id) {
      this.setData({
        id: parseInt(options.id)
      });
    }
    let that = this
    util.request(api.AdDetail, {id: this.data.id}).then(res => {
      if (res.errno === 0) {
        WxParse.wxParse('adDetail', 'html', res.data.ad.detail, this)
        that.setData({
          ad: res.data.ad
        })
      }
    })
    
    wx.getSetting({
        success: function (res) {
            console.log(res)
            //不存在相册授权
            if (!res.authSetting['scope.writePhotosAlbum']) {
                wx.authorize({
                    scope: 'scope.writePhotosAlbum',
                    success: function () {
                        that.setData({
                            canWrite: true
                        })
                    },
                    fail: function (err) {
                        that.setData({
                            canWrite: false
                        })
                    }
                })
            } else {
                that.setData({
                    canWrite: true
                });
            }
        }
    })
  },
  closeShare: function() {
    this.setData({
      openShare: false,
    });
  },
  onShow: function() {
  },
  onHide: function() {
    // 页面隐藏

  },
  onUnload: function() {
    // 页面关闭

  },
  onReady: function() {
    // 页面渲染完成

  }

})