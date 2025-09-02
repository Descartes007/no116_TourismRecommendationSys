<template>
  <div class="pagination-container">
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[4, 8, 12, 20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      background
    ></el-pagination>
  </div>
</template>

<script>
export default {
  name: "Pagination",
  props: {
    total: {
      required: true,
      type: Number
    },
    page: {
      type: Number,
      default: 1
    },
    limit: {
      type: Number,
      default: 10
    },
    pageSizes: {
      type: Array,
      default() {
        return [4, 8, 12, 20];
      }
    },
    layout: {
      type: String,
      default: "total, sizes, prev, pager, next, jumper"
    },
    background: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    currentPage: {
      get() {
        return this.page;
      },
      set(val) {
        this.$emit("update:page", val);
      }
    },
    pageSize: {
      get() {
        return this.limit;
      },
      set(val) {
        this.$emit("update:limit", val);
      }
    }
  },
  methods: {
    handleSizeChange(val) {
      this.$emit("pagination", { page: this.currentPage, limit: val });
    },
    handleCurrentChange(val) {
      this.$emit("pagination", { page: val, limit: this.pageSize });
    }
  }
};
</script>

<style scoped>
.pagination-container {
  padding: 15px 0;
  margin-top: 15px;
  text-align: right;
}
</style> 