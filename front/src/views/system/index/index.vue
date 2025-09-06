<template>
  <div class="dashboard">
    <!-- 统计卡片区域 -->
    <div class="stat-section">
      <h2 class="section-title">数据概览</h2>
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon bg-primary">
              <i class="el-icon-place"></i>
            </div>
            <div class="stat-info">
              <div class="stat-title">景点总数</div>
              <div class="stat-value">{{ shuju.attractionsCount || 0 }}</div>
              <div class="stat-desc">系统中的景点总数量</div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon bg-success">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-info">
              <div class="stat-title">用户总数</div>
              <div class="stat-value">{{ shuju.userCount || 0 }}</div>
              <div class="stat-desc">注册用户总数量</div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon bg-warning">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="stat-info">
              <div class="stat-title">今日预约</div>
              <div class="stat-value">{{ shuju.todayOrderCount || 0 }}</div>
              <div class="stat-desc">今日新增预约数量</div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon bg-danger">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="stat-info">
              <div class="stat-title">酒店总数</div>
              <div class="stat-value">{{ shuju.hotelCount || 0 }}</div>
              <div class="stat-desc">系统中的酒店总数量</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- 图表区域 -->
    <div class="chart-section">
      <h2 class="section-title">数据统计</h2>
      <el-row :gutter="24">
        <el-col :xs="24" :lg="12">
          <div class="chart-card">
            <div class="chart-header">
              <div class="chart-title">
                <i class="el-icon-data-analysis"></i>
                <span>近七日景点预约</span>
              </div>
              <el-button type="text" class="refresh-btn" @click="refreshCharts">
                <i class="el-icon-refresh"></i>
              </el-button>
            </div>
            <div class="chart-wrapper" id="index1" v-loading="loading"></div>
          </div>
        </el-col>
        
        <el-col :xs="24" :lg="12">
          <div class="chart-card">
            <div class="chart-header">
              <div class="chart-title">
                <i class="el-icon-data-line"></i>
                <span>近七日酒店预约</span>
              </div>
              <el-button type="text" class="refresh-btn" @click="refreshCharts">
                <i class="el-icon-refresh"></i>
              </el-button>
            </div>
            <div class="chart-wrapper" id="index2" v-loading="loading"></div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
  import {getManageData} from '../../../api/api'
  import * as echarts from "echarts";
  export default {
    data() {
      return{
        myChart: null,
        myChart1: null,
        loading: true,
        shuju: {
          attractionsCount: 0,
          userCount: 0,
          todayOrderCount: 0,
          hotelCount: 0,
          dates: [],
          nums: [],
          orders: []
        }
      }
    },
    methods: {
      init() {
        this.loading = true;
        getManageData().then(res => {
          this.loading = false;
          if (res.code == 1000) {
            this.shuju = res.data;
            // 使用setTimeout确保DOM已经渲染完成
            setTimeout(() => {
              this.initCharts();
            }, 100);
          } else {
            this.$message.error('获取数据失败，请稍后再试');
          }
        }).catch(() => {
          this.loading = false;
          this.$message.error('网络异常，请稍后再试');
        });
      },
      
      initCharts() {
        // 初始化景点预约图表
        this.initAttractionChart();
        
        // 初始化酒店预约图表
        this.initHotelChart();
      },
      
      initAttractionChart() {
        var chartDom = document.getElementById('index1');
        if (!chartDom) return;
        
        // 如果已经存在图表实例，先销毁
        if (this.myChart) {
          this.myChart.dispose();
        }
        
        this.myChart = echarts.init(chartDom);
        var option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: [
            {
              type: 'category',
              data: this.shuju.dates || [],
              axisTick: {
                alignWithLabel: true
              }
            }
          ],
          yAxis: [
            {
              type: 'value'
            }
          ],
          series: [
            {
              name: '预约数',
              type: 'bar',
              barWidth: '60%',
              data: this.shuju.nums || [],
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#83bff6' },
                  { offset: 0.5, color: '#188df0' },
                  { offset: 1, color: '#188df0' }
                ])
              },
              emphasis: {
                itemStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#2378f7' },
                    { offset: 0.7, color: '#2378f7' },
                    { offset: 1, color: '#83bff6' }
                  ])
                }
              }
            }
          ]
        };
        this.myChart.setOption(option);
      },
      
      initHotelChart() {
        var chartDom1 = document.getElementById('index2');
        if (!chartDom1) return;
        
        // 如果已经存在图表实例，先销毁
        if (this.myChart1) {
          this.myChart1.dispose();
        }
        
        this.myChart1 = echarts.init(chartDom1);
        var option1 = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross',
              label: {
                backgroundColor: '#6a7985'
              }
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: [
            {
              type: 'category',
              boundaryGap: false,
              data: this.shuju.dates || []
            }
          ],
          yAxis: [
            {
              type: 'value'
            }
          ],
          series: [
            {
              name: '预约数',
              type: 'line',
              stack: 'Total',
              smooth: true,
              lineStyle: {
                width: 3,
                color: '#5470c6'
              },
              showSymbol: false,
              areaStyle: {
                opacity: 0.8,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(84,112,198,0.8)' },
                  { offset: 1, color: 'rgba(84,112,198,0.1)' }
                ])
              },
              emphasis: {
                focus: 'series'
              },
              data: this.shuju.orders || []
            }
          ]
        };
        this.myChart1.setOption(option1);
      },
      
      refreshCharts() {
        this.init();
      },
      
      // 监听窗口大小变化，重新调整图表大小
      resizeHandler() {
        if (this.myChart) {
          this.myChart.resize();
        }
        if (this.myChart1) {
          this.myChart1.resize();
        }
      }
    },
    mounted() {
      this.init();
      // 添加窗口大小变化的监听
      window.addEventListener('resize', this.resizeHandler);
    },
    beforeDestroy() {
      // 销毁图表实例，避免内存泄漏
      if(this.myChart) {
        this.myChart.dispose();
        this.myChart = null;
      }
      if(this.myChart1) {
        this.myChart1.dispose();
        this.myChart1 = null;
      }
      
      // 移除窗口大小变化的监听
      window.removeEventListener('resize', this.resizeHandler);
    }
 }
</script>

<style scoped>
  .dashboard {
    padding: 24px;
    min-height: 100%;
    box-sizing: border-box;
    overflow-x: hidden;
    position: relative;
    z-index: 0;
    background-color: #f5f7fa;
  }
  
  /* 区域标题样式 */
  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 20px 0;
    padding-left: 12px;
    border-left: 4px solid #409EFF;
    line-height: 24px;
  }
  
  /* 统计卡片区域样式 */
  .stat-section {
    margin-bottom: 32px;
    position: relative;
    z-index: 5;
  }
  
  .stat-card {
    display: flex;
    align-items: center;
    height: 120px;
    transition: all 0.3s;
    overflow: hidden;
    margin-bottom: 20px;
    background-color: #fff;
    position: relative;
    z-index: 10;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    padding: 20px;
  }
  
  .stat-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  }
  
  .stat-icon {
    width: 64px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    margin-right: 20px;
    flex-shrink: 0;
  }
  
  .stat-icon i {
    font-size: 28px;
    color: white;
  }
  
  .bg-primary {
    background: linear-gradient(135deg, #409EFF, #53a8ff);
  }
  
  .bg-success {
    background: linear-gradient(135deg, #67C23A, #85ce61);
  }
  
  .bg-warning {
    background: linear-gradient(135deg, #E6A23C, #ebb563);
  }
  
  .bg-danger {
    background: linear-gradient(135deg, #F56C6C, #f78989);
  }
  
  .stat-info {
    flex: 1;
    padding-right: 10px;
  }
  
  .stat-title {
    font-size: 16px;
    color: #606266;
    margin-bottom: 10px;
    font-weight: 500;
  }
  
  .stat-value {
    font-size: 32px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 10px;
    line-height: 1;
  }
  
  .stat-desc {
    font-size: 13px;
    color: #909399;
  }
  
  /* 图表区域样式 */
  .chart-section {
    margin-bottom: 32px;
    position: relative;
    z-index: 5;
  }
  
  .chart-card {
    margin-bottom: 24px;
    background-color: #fff;
    position: relative;
    z-index: 5;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    padding: 0;
    overflow: hidden;
  }
  
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  }
  
  .chart-title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 500;
    color: #303133;
  }
  
  .chart-title i {
    margin-right: 8px;
    color: #409EFF;
  }
  
  .refresh-btn {
    padding: 6px;
    color: #909399;
  }
  
  .refresh-btn:hover {
    color: #409EFF;
  }
  
  .chart-wrapper {
    height: 350px;
    width: 100%;
    position: relative;
    z-index: 1;
    padding: 16px;
    box-sizing: border-box;
  }
  
  /* 确保图表容器正确显示 */
  :deep(.echarts) {
    width: 100% !important;
    height: 100% !important;
  }
  
  /* 修复响应式布局问题 */
  @media screen and (max-width: 768px) {
    .dashboard {
      padding: 16px;
    }
    
    .section-title {
      font-size: 16px;
      margin-bottom: 16px;
    }
    
    .stat-card {
      height: auto;
      padding: 16px;
    }
    
    .stat-icon {
      width: 50px;
      height: 50px;
      margin-right: 15px;
    }
    
    .stat-icon i {
      font-size: 24px;
    }
    
    .stat-value {
      font-size: 24px;
      margin-bottom: 5px;
    }
    
    .chart-wrapper {
      height: 280px;
    }
    
    .stat-section .el-col {
      margin-bottom: 16px;
    }
  }
</style>