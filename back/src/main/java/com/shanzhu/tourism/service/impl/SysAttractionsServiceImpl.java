package com.shanzhu.tourism.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.shanzhu.tourism.domain.SysAttractions;
import com.shanzhu.tourism.domain.SysComments;
import com.shanzhu.tourism.mapper.SysAttractionsMapper;
import com.shanzhu.tourism.mapper.SysCommentsMapper;
import com.shanzhu.tourism.service.SysAttractionsService;
import com.shanzhu.tourism.utils.recommend.CoreMath;
import com.shanzhu.tourism.utils.recommend.dto.ProductDTO;
import com.shanzhu.tourism.utils.recommend.dto.RelateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysAttractionsServiceImpl extends ServiceImpl<SysAttractionsMapper, SysAttractions> implements SysAttractionsService {

    @Value("${useRecommend}")
    private Boolean userRecommend;

    @Resource
    private SysCommentsMapper sysCommentsMapper;

    @Resource
    private SysAttractionsMapper sysAttractionsMapper;
    
    // 默认推荐的景点数量
    private static final int DEFAULT_RECOMMEND_COUNT = 4;

    @Override
    public List<SysAttractions> searchAttractions(String userId) {
        log.info("开始获取推荐景点，用户ID: {}, 是否启用推荐算法: {}", userId, userRecommend);
        
        try {
            //判断是否需要走推荐算法
            if(BooleanUtil.isFalse(userRecommend)) {
                log.info("推荐算法未启用，返回热门景点");
                return getPopularAttractions();
            } else {
                log.info("推荐算法已启用，开始计算个性化推荐");
                return recommendAttractions(userId);
            }
        } catch (Exception e) {
            log.error("获取推荐景点时发生错误", e);
            // 发生异常时返回热门景点
            return getPopularAttractions();
        }
    }

    /**
     * 获取热门景点列表
     * @return 热门景点列表
     */
    private List<SysAttractions> getPopularAttractions() {
        try {
            QueryWrapper<SysAttractions> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(SysAttractions::getState, 1)
                    .orderByDesc(SysAttractions::getCreateTime)
                    .last("limit " + DEFAULT_RECOMMEND_COUNT);
            List<SysAttractions> attractions = this.list(queryWrapper);
            log.info("获取热门景点成功，数量: {}", attractions.size());
            return attractions;
        } catch (Exception e) {
            log.error("获取热门景点失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 使用推荐算法获取个性化推荐景点
     * @param userId 用户ID
     * @return 推荐景点列表
     */
    private List<SysAttractions> recommendAttractions(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("用户ID为空，返回热门景点");
            return getPopularAttractions();
        }
        
        try {
            CoreMath coreMath = new CoreMath();
            
            // 获取评分数据
            List<RelateDTO> relateDTOList = generateRelates();
            if (CollectionUtils.isEmpty(relateDTOList)) {
                log.warn("没有足够的评分数据用于推荐，返回热门景点");
                return getPopularAttractions();
            }
            
            log.info("开始执行推荐算法，用户ID: {}, 评分数据数量: {}", userId, relateDTOList.size());
            
            // 执行推荐算法
            List<String> recommendations = coreMath.recommend(userId, relateDTOList);
            if (CollectionUtils.isEmpty(recommendations)) {
                log.warn("推荐算法未返回结果，返回热门景点");
                return getPopularAttractions();
            }
            
            log.info("推荐算法返回结果数量: {}", recommendations.size());
            
            // 获取产品数据
            List<ProductDTO> productDTOList = generateProducts().stream()
                    .filter(e -> recommendations.contains(e.getProductId()))
                    .collect(Collectors.toList());
            
            List<SysAttractions> sysAttractions = new ArrayList<>();
            // 获取推荐景点ID列表
            List<String> productIdList = productDTOList.stream()
                    .map(ProductDTO::getProductId)
                    .collect(Collectors.toList());
            
            // 根据推荐的景点ID获取景点详情
            for (String productId : productIdList) {
                SysAttractions attraction = sysAttractionsMapper.selectById(productId);
                if (attraction != null && attraction.getState() == 1) { // 只添加状态为启用的景点
                    sysAttractions.add(attraction);
                }
            }
            
            log.info("成功获取推荐景点，数量: {}", sysAttractions.size());
            
            // 返回推荐结果，不再补充热门景点
            log.info("返回推荐景点，数量: {}", sysAttractions.size());
            
            return sysAttractions;
        } catch (Exception e) {
            log.error("执行推荐算法时发生错误", e);
            return getPopularAttractions();
        }
    }

    /**
     * 获取相关的所有评论数据
     *
     * @return 相关评论数据
     */
    private List<RelateDTO> generateRelates() {
        try {
            //查询所有商品的评论
            List<SysComments> comments = sysCommentsMapper.selectList(Wrappers.emptyWrapper());
            if (CollectionUtils.isEmpty(comments)) {
                log.warn("没有找到评论数据");
                return Collections.emptyList();
            }
            
            log.info("获取到评论数据数量: {}", comments.size());
            
            List<RelateDTO> relateDTOList = new ArrayList<>();
            for (SysComments sysComment : comments) {
                if (sysComment.getUserId() != null && sysComment.getAttractionsId() != null && sysComment.getScore() != null) {
                    RelateDTO relateDTO = new RelateDTO();
                    relateDTO.setUserId(sysComment.getUserId());
                    relateDTO.setProductId(sysComment.getAttractionsId());
                    relateDTO.setIndex(sysComment.getScore());
                    relateDTOList.add(relateDTO);
                }
            }
            
            log.info("转换后的评分数据数量: {}", relateDTOList.size());
            return relateDTOList;
        } catch (Exception e) {
            log.error("获取评论数据时发生错误", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取相关的所有业务数据
     *
     * @return 相关数据
     */
    private List<ProductDTO> generateProducts() {
        try {
            List<ProductDTO> productDTOList = new ArrayList<>();
            List<SysAttractions> sysAttractions = sysAttractionsMapper.selectList(
                    Wrappers.<SysAttractions>lambdaQuery().eq(SysAttractions::getState, 1)
            );
            
            if (CollectionUtils.isEmpty(sysAttractions)) {
                log.warn("没有找到启用状态的景点数据");
                return Collections.emptyList();
            }
            
            log.info("获取到景点数据数量: {}", sysAttractions.size());
            
            for (SysAttractions sysAttraction : sysAttractions) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setProductId(sysAttraction.getId());
                productDTO.setProductName(sysAttraction.getName());
                productDTO.setProductPrice(sysAttraction.getPrice());
                productDTOList.add(productDTO);
            }
            
            log.info("转换后的产品数据数量: {}", productDTOList.size());
            return productDTOList;
        } catch (Exception e) {
            log.error("获取景点数据时发生错误", e);
            return Collections.emptyList();
        }
    }
}
