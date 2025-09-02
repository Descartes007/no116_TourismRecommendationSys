const state = {
  activeMenu: "/index",
}

const getters = {

}

const mutations = {
  setActiveMenu(state, menu) {
    state.activeMenu = menu
  }
}

const actions = {

}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}
