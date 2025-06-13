package com.canoestudio.caveapi.core;

import com.canoestudio.caveapi.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockStone;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
import java.util.Optional;

@Config(modid = CaveAPIMod.MODID)
@Config.LangKey("caveapi.config.title")
public class CaveAPIConfig {

    @Config.Comment("是否替换原版洞穴生成")
    @Config.Name("replace_vanilla_caves")
    public static boolean replaceVanillaCaves = true;

    @Config.Comment("噪声缩放参数 (X轴)")
    @Config.Name("noise_scale_x")
    @Config.RangeDouble(min = 0.01, max = 1.0)
    public static float noiseScaleX = 0.05f;

    @Config.Comment("噪声缩放参数 (Y轴)")
    @Config.Name("noise_scale_y")
    @Config.RangeDouble(min = 0.01, max = 1.0)
    public static float noiseScaleY = 0.1f;

    @Config.Comment("噪声缩放参数 (Z轴)")
    @Config.Name("noise_scale_z")
    @Config.RangeDouble(min = 0.01, max = 1.0)
    public static float noiseScaleZ = 0.05f;

    @Config.Comment("最小生成高度 (0 = 基岩层)")
    @Config.Name("min_y")
    @Config.RangeInt(min = 0, max = 255)
    public static int minY = 0;

    @Config.Comment("最大生成高度 (128 = 相当于1.17的320)")
    @Config.Name("max_y")
    @Config.RangeInt(min = 0, max = 255)
    public static int maxY = 128;

    @Config.Comment("世界海平面高度")
    @Config.Name("sea_level")
    @Config.RangeInt(min = 0, max = 255)
    public static int seaLevel = 63;

    @Config.Comment("含水层噪声频率")
    @Config.Name("aquifer_frequency")
    @Config.RangeDouble(min = 0.01, max = 1.0)
    public static float aquiferFrequency = 0.1f;

    @Config.Comment("岩浆生成阈值")
    @Config.Name("lava_level")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    public static float lavaLevel = 0.15f;

    @Config.Comment("洞穴空气方块 (格式: modid:block[:meta] 或 modid:block[property=value,...])")
    @Config.Name("cave_air_block")
    public static String caveAirBlock = "minecraft:air";

    @Config.Comment("含水层水源方块 (格式同上)")
    @Config.Name("water_block")
    public static String waterBlock = "minecraft:flowing_water[level=0]";

    @Config.Comment("岩浆源方块 (格式同上)")
    @Config.Name("lava_block")
    public static String lavaBlock = "minecraft:flowing_lava[level=0]";

    // 缓存解析后的方块状态
    private static IBlockState caveAirState;
    private static IBlockState waterState;
    private static IBlockState lavaState;
    private static long lastUpdateTime = 0;

    public static IBlockState getCaveAirState() {
        updateBlockStates();
        return caveAirState;
    }

    public static IBlockState getWaterState() {
        updateBlockStates();
        return waterState;
    }

    public static IBlockState getLavaState() {
        updateBlockStates();
        return lavaState;
    }

    private static synchronized void updateBlockStates() {
        // 每秒最多更新一次
        if (System.currentTimeMillis() - lastUpdateTime < 1000) return;

        caveAirState = parseBlockState(caveAirBlock, Blocks.AIR.getDefaultState());
        waterState = parseBlockState(waterBlock, Blocks.FLOWING_WATER.getDefaultState());
        lavaState = parseBlockState(lavaBlock, Blocks.FLOWING_LAVA.getDefaultState());

        lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 解析方块状态，支持元数据和属性
     */
    public static IBlockState parseBlockState(String str, IBlockState defaultState) {
        try {
            if (str == null || str.trim().isEmpty()) return defaultState;

            // 检查属性格式: [property=value,...]
            if (str.contains("[")) {
                return parseBlockStateWithProperties(str, defaultState);
            }

            // 分割字符串
            String[] parts = str.split(":");

            // 处理元数据格式: modid:block:meta
            if (parts.length >= 3 && NumberUtils.isDigits(parts[2])) {
                return parseBlockStateWithMeta(parts, defaultState);
            }

            // 简单格式: modid:block
            if (parts.length >= 2) {
                Block block = parseBlock(parts[0], parts[1]);
                return block != null ? block.getDefaultState() : defaultState;
            }

            CaveAPIMod.logger.warn("Invalid block format: " + str);
        } catch (Exception e) {
            CaveAPIMod.logger.error("Failed to parse block state: " + str, e);
        }
        return defaultState;
    }

    /**
     * 解析带元数据的方块状态
     */
    private static IBlockState parseBlockStateWithMeta(String[] parts, IBlockState defaultState) {
        String namespace = parts[0];
        String blockName = parts[1];
        int meta = Integer.parseInt(parts[2]);

        Block block = parseBlock(namespace, blockName);
        if (block != null) {
            try {
                return block.getStateFromMeta(meta);
            } catch (Exception e) {
                CaveAPIMod.logger.error("Invalid metadata {} for block {}", meta, block.getRegistryName(), e);
                return block.getDefaultState();
            }
        }
        return defaultState;
    }

    /**
     * 解析带属性的方块状态
     */
    private static IBlockState parseBlockStateWithProperties(String str, IBlockState defaultState) {
        try {
            // 分离方块名称和属性部分
            int bracketIndex = str.indexOf('[');
            int endBracketIndex = str.indexOf(']', bracketIndex);
            if (endBracketIndex == -1) {
                CaveAPIMod.logger.error("Missing closing bracket in block state: " + str);
                return defaultState;
            }

            String blockPart = str.substring(0, bracketIndex);
            String propPart = str.substring(bracketIndex + 1, endBracketIndex);

            // 解析基础方块
            String[] blockParts = blockPart.split(":");
            if (blockParts.length < 2) {
                CaveAPIMod.logger.error("Invalid block format in property specification: " + blockPart);
                return defaultState;
            }

            Block block = parseBlock(blockParts[0], blockParts[1]);
            if (block == null) return defaultState;

            // 获取默认状态
            IBlockState state = block.getDefaultState();

            // 解析属性
            String[] properties = propPart.split(",");
            for (String prop : properties) {
                String[] keyValue = prop.split("=");
                if (keyValue.length != 2) {
                    CaveAPIMod.logger.warn("Invalid property format: " + prop);
                    continue;
                }

                String propName = keyValue[0].trim();
                String propValue = keyValue[1].trim();

                // 查找匹配的属性
                Optional<IProperty<?>> propertyOpt = state.getPropertyNames().stream()
                        .filter(p -> p.getName().equals(propName))
                        .findFirst();

                if (propertyOpt.isPresent()) {
                    state = setPropertyValue(state, propertyOpt.get(), propValue);
                } else {
                    CaveAPIMod.logger.warn("Property {} not found for block {}", propName, block.getRegistryName());
                }
            }

            return state;
        } catch (Exception e) {
            CaveAPIMod.logger.error("Error parsing block properties: " + str, e);
            return defaultState;
        }
    }

    /**
     * 为方块状态设置属性值
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Comparable<T>> IBlockState setPropertyValue(
            IBlockState state, IProperty<T> property, String value) {

        Optional<T> valueOpt = (Optional<T>) property.getAllowedValues().stream()
                .filter(v -> v.toString().equalsIgnoreCase(value))
                .findFirst();

        if (valueOpt.isPresent()) {
            return state.withProperty(property, valueOpt.get());
        } else {
            CaveAPIMod.logger.warn("Invalid value '{}' for property {}", value, property.getName());
            return state;
        }
    }

    /**
     * 从命名空间和名称解析方块
     */
    private static Block parseBlock(String namespace, String name) {
        ResourceLocation res = new ResourceLocation(namespace, name);
        if (ForgeRegistries.BLOCKS.containsKey(res)) {
            return ForgeRegistries.BLOCKS.getValue(res);
        }
        CaveAPIMod.logger.warn("Block not found: {}", res);
        return null;
    }

    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Tags.MOD_ID)) {
                ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
                updateBlockStates();
                CaveAPIMod.logger.info("CaveAPI config reloaded!");

                // 通知生成器更新参数
                CaveGeneratorRegistry.refreshConfig();
            }
        }
    }
}