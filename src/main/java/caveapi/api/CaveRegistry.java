package caveapi.api;

import caveapi.api.CaveGenerator;
import caveapi.world.CaveGeneratorManager;

import java.util.*;

public class CaveRegistry {
    private final CaveGeneratorManager manager;

    public CaveRegistry(CaveGeneratorManager manager) {
        this.manager = manager;
    }

    /**
     * 为指定的生物群系注册洞穴生成器
     *
     * @param biomeNames 生物群系列表（注册名格式：modid:biome_name）
     * @param generator  自定义洞穴生成器实现
     */
    public void registerGenerator(String[] biomeNames, CaveGenerator generator) {
        manager.registerGenerator(biomeNames, generator);
    }

    /**
     * 注册条件性后备生成器（当目标群系附近存在特定邻居群系时生效）
     *
     * @param targetBiome   需要后备规则的主生物群系
     * @param neighborBiomes 触发条件所需的邻近生物群系列表
     * @param generator     后备洞穴生成器
     */
    public void registerFallbackRule(String targetBiome, List<String> neighborBiomes, CaveGenerator generator) {
        manager.addFallbackRule(targetBiome, neighborBiomes, generator);
    }
}