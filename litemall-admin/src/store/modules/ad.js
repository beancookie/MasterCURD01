const ad = {
  state: {
    ad: {},
    isUpdate: false
  },
  mutations: {
    SET_AD: (state, ad) => {
      state.ad = ad
    },
    SET_IS_UPDATE: (state, isUpdate) => {
      state.isUpdate = isUpdate
    }
  },
  actions: {
    // 更新活动
    UpdateAd({ commit }, ad) {
      commit('SET_AD', ad)
    },
    // 更新是否是更新状态
    UpdateIsUpdate({ commit }, isUpdate) {
      commit('SET_IS_UPDATE', isUpdate)
    }
  }
}

export default ad

