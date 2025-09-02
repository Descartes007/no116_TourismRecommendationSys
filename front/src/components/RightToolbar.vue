<template>
  <div class="right-toolbar">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-tooltip content="刷新" placement="top">
          <el-button size="mini" icon="el-icon-refresh" circle @click="refresh"></el-button>
        </el-tooltip>
      </el-col>
      <el-col :span="1.5">
        <el-tooltip content="搜索" placement="top">
          <el-button size="mini" icon="el-icon-search" circle @click="toggleSearch"></el-button>
        </el-tooltip>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "RightToolbar",
  props: {
    showSearch: {
      type: Boolean,
      default: true
    }
  },
  methods: {
    refresh() {
      this.$emit("queryTable");
    },
    toggleSearch() {
      this.$emit("update:showSearch", !this.showSearch);
    }
  }
};
</script>

<style scoped>
.right-toolbar {
  display: flex;
  justify-content: flex-end;
  float: right;
}
</style> 