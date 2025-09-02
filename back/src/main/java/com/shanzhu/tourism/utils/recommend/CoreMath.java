package com.shanzhu.tourism.utils.recommend;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.shanzhu.tourism.utils.recommend.dto.RelateDTO;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 推荐算法
 *
 * @author: ShanZhu
 * @date: 2024-07-25
 */
public class CoreMath {

    // 推荐结果数量上限
    private static final int MAX_RECOMMENDATIONS = 4;
    // 相似度阈值，低于此值的用户不纳入推荐计算
    private static final double SIMILARITY_THRESHOLD = 0.2;
    // 最大相似用户数量
    private static final int MAX_SIMILAR_USERS = 3;

    /**
     * 推荐计算
     */
    public List<String> recommend(String userId, List<RelateDTO> list) {
        // 如果用户ID为空或数据列表为空，返回空列表
        if (userId == null || CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        
        // 找到最相似的几个用户
        Map<Double, String> distances = computeNearestNeighbor(userId, list);
        if(MapUtil.isEmpty(distances)){
            return getFallbackRecommendations(list);
        }
        
        // 对用户评分数据按用户ID分组
        Map<String, List<RelateDTO>> userMap = list.stream().collect(Collectors.groupingBy(RelateDTO::getUserId));

        // 当前用户的评分项目列表
        List<String> userItemList = userMap.containsKey(userId) ? 
                userMap.get(userId).stream().map(RelateDTO::getProductId).collect(Collectors.toList()) : 
                new ArrayList<>();
        
        // 创建加权推荐项目Map，键为项目ID，值为该项目的权重得分
        Map<String, Double> weightedRecommendations = new HashMap<>();
        
        // 获取相似用户列表（按相似度排序）
        List<Map.Entry<Double, String>> similarUsers = new ArrayList<>(distances.entrySet());
        // 限制相似用户数量
        int userCount = Math.min(similarUsers.size(), MAX_SIMILAR_USERS);
        
        for (int i = 0; i < userCount; i++) {
            Map.Entry<Double, String> entry = similarUsers.get(i);
            double similarity = Math.abs(entry.getKey()); // 使用相似度的绝对值
            String similarUserId = entry.getValue();
            
            // 如果相似度低于阈值，跳过该用户
            if (similarity < SIMILARITY_THRESHOLD) {
                continue;
            }
            
            // 获取相似用户的评分项目
            List<RelateDTO> similarUserItems = userMap.get(similarUserId);
            if (similarUserItems == null) {
                continue;
            }
            
            // 处理相似用户的每个评分项目
            for (RelateDTO item : similarUserItems) {
                String itemId = item.getProductId();
                Integer score = item.getIndex();
                
                // 如果当前用户已经评价过该项目，跳过
                if (userItemList.contains(itemId)) {
                    continue;
                }
                
                // 计算该项目的加权分数：用户相似度 * 项目评分
                double weightedScore = similarity * score;
                
                // 更新加权推荐Map
                weightedRecommendations.put(itemId, 
                        weightedRecommendations.getOrDefault(itemId, 0.0) + weightedScore);
            }
        }
        
        // 如果没有推荐结果，返回备选推荐
        if (weightedRecommendations.isEmpty()) {
            return getFallbackRecommendations(list);
        }
        
        // 按权重降序排序并获取推荐结果
        return weightedRecommendations.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(MAX_RECOMMENDATIONS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 当没有足够的推荐数据时，提供备选推荐
     */
    private List<String> getFallbackRecommendations(List<RelateDTO> list) {
        // 计算所有项目的平均评分
        Map<String, Double> avgScores = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();
        
        for (RelateDTO item : list) {
            String productId = item.getProductId();
            Integer score = item.getIndex();
            
            avgScores.put(productId, avgScores.getOrDefault(productId, 0.0) + score);
            countMap.put(productId, countMap.getOrDefault(productId, 0) + 1);
        }
        
        // 计算平均分
        for (String productId : avgScores.keySet()) {
            avgScores.put(productId, avgScores.get(productId) / countMap.get(productId));
            }
        
        // 按平均分降序排序并返回前N个
        return avgScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(MAX_RECOMMENDATIONS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 在给定userId的情况下，计算其他用户和它的相关系数并排序
     */
    private Map<Double, String> computeNearestNeighbor(String userId, List<RelateDTO> list) {
        //对同一个用户id数据，做分组
        Map<String, List<RelateDTO>> userMap = list.stream().collect(Collectors.groupingBy(RelateDTO::getUserId));
        
        // 如果当前用户没有评分数据，返回空Map
        if (!userMap.containsKey(userId) || CollUtil.isEmpty(userMap.get(userId))) {
            return new TreeMap<>();
        }
        
        //treemap是从小到大排好序的
        Map<Double, String> distances = new TreeMap<>();

        userMap.forEach((k, v) -> {
            if (!k.equals(userId)) {
                Double distance = pearsonDis(v, userMap.get(userId));
                if(distance != null){
                    //相关系数 ： 用户id
                    distances.put(distance, k);
                }
            }
        });
        return distances;
    }

    /**
     * 计算两个序列间的相关系数
     *
     * @param xList 其他用户的数据集
     * @param yList 当前用户的数据集
     * @return 相关系数
     */
    private Double pearsonDis(List<RelateDTO> xList, List<RelateDTO> yList) {
        if(CollUtil.isEmpty(xList) || CollUtil.isEmpty(yList)){
            return null;
        }

        List<Integer> xs = Lists.newArrayList();
        List<Integer> ys = Lists.newArrayList();
        
        // 找出两个用户共同评价过的项目
        xList.forEach(x -> {
            yList.forEach(y -> {
                if (x.getProductId().equals(y.getProductId())) {
                    xs.add(x.getIndex());
                    ys.add(y.getIndex());
                }
            });
        });
        
        // 如果没有共同评价的项目，返回null
        if (xs.isEmpty() || xs.size() < 2) {
            return null;
        }
        
        return getRelate(xs, ys);
    }

    /**
     * 方法描述: 皮尔森（pearson）相关系数计算
     * (x1,y1) 理解为 a 用户对 x 商品的点击次数和对 y 商品的点击次数
     *
     * @param xs 其他用户数据分布
     * @param ys 当前用户数据分布
     * @Return 相关系数值
     */
    public static Double getRelate(List<Integer> xs, List<Integer> ys) {
        int n = xs.size();
        
        // 如果数据点太少，无法计算有意义的相关系数
        if (n < 2) {
            return 0.0;
        }
        
        double Ex = xs.stream().mapToDouble(x -> x).sum();
        double Ey = ys.stream().mapToDouble(y -> y).sum();
        double Ex2 = xs.stream().mapToDouble(x -> Math.pow(x, 2)).sum();
        double Ey2 = ys.stream().mapToDouble(y -> Math.pow(y, 2)).sum();
        double Exy = IntStream.range(0, n).mapToDouble(i -> xs.get(i) * ys.get(i)).sum();
        double numerator = Exy - Ex * Ey / n;
        double denominator = Math.sqrt((Ex2 - Math.pow(Ex, 2) / n) * (Ey2 - Math.pow(Ey, 2) / n));
        if (Double.isNaN(numerator) || denominator == 0) {
            return 0.0;
        }
        return numerator / denominator; // 注意：移除了负号，使得正相关性更高
    }
}
