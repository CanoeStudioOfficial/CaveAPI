package org.canoestudio.caveapi.core;

import org.objectweb.asm.Type;

public final class SRGNameMappings {
    
    private SRGNameMappings() {}
    
    public static final class Chunk {
        public static final String CLASS = "net/minecraft/world/chunk/Chunk";
        public static final String CLASS_SRG = "aes";
        
        public static final String STORAGE_ARRAYS = "storageArrays";
        public static final String STORAGE_ARRAYS_SRG = "field_76645_j";
        
        public static final String GET_BLOCK_STATE = "getBlockState";
        public static final String GET_BLOCK_STATE_SRG = "func_177435_g";
        public static final String GET_BLOCK_STATE_DESC = "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;";
        
        public static final String SET_BLOCK_STATE = "setBlockState";
        public static final String SET_BLOCK_STATE_SRG = "func_177436_a";
        public static final String SET_BLOCK_STATE_DESC = "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;";
        
        public static final String GET_BLOCK_STATE_INT = "getBlockState";
        public static final String GET_BLOCK_STATE_INT_SRG = "func_76611_b";
        public static final String GET_BLOCK_STATE_INT_DESC = "(III)Lnet/minecraft/block/state/IBlockState;";
        
        public static final String GET_LIGHT_FOR = "getLightFor";
        public static final String GET_LIGHT_FOR_SRG = "func_177413_a";
        public static final String GET_LIGHT_FOR_DESC = "(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)I";
        
        public static final String SET_LIGHT_FOR = "setLightFor";
        public static final String SET_LIGHT_FOR_SRG = "func_177417_a";
        public static final String SET_LIGHT_FOR_DESC = "(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;I)V";
        
        public static final String GET_LIGHT_SUBTRACTED = "getLightSubtracted";
        public static final String GET_LIGHT_SUBTRACTED_SRG = "func_76628_c";
        public static final String GET_LIGHT_SUBTRACTED_DESC = "(Lnet/minecraft/util/math/BlockPos;I)I";
        
        public static final String GENERATE_SKYLIGHT_MAP = "generateSkylightMap";
        public static final String GENERATE_SKYLIGHT_MAP_SRG = "func_76630_e";
        
        public static final String GET_HEIGHT_VALUE = "getHeightValue";
        public static final String GET_HEIGHT_VALUE_SRG = "func_76627_g";
        
        public static final String GET_TOP_FILLED_SEGMENT = "getTopFilledSegment";
        public static final String GET_TOP_FILLED_SEGMENT_SRG = "func_76625_h";
    }
    
    public static final class World {
        public static final String CLASS = "net/minecraft/world/World";
        public static final String CLASS_SRG = "amu";
        
        public static final String IS_OUTSIDE_BUILD_HEIGHT = "isOutsideBuildHeight";
        public static final String IS_OUTSIDE_BUILD_HEIGHT_SRG = "func_175701_a";
        public static final String IS_OUTSIDE_BUILD_HEIGHT_DESC = "(Lnet/minecraft/util/math/BlockPos;)Z";
        
        public static final String GET_LIGHT = "getLight";
        public static final String GET_LIGHT_SRG = "func_175671_l";
        public static final String GET_LIGHT_DESC = "(Lnet/minecraft/util/math/BlockPos;)I";
        
        public static final String GET_LIGHT_FOR = "getLightFor";
        public static final String GET_LIGHT_FOR_SRG = "func_175699_k";
        public static final String GET_LIGHT_FOR_DESC = "(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)I";
        
        public static final String IS_AREA_LOADED = "isAreaLoaded";
        public static final String IS_AREA_LOADED_SRG = "func_175703_a";
        
        public static final String IS_BLOCK_LOADED = "isBlockLoaded";
        public static final String IS_BLOCK_LOADED_SRG = "func_175667_e";
        
        public static final String GET_ACTUAL_HEIGHT = "getActualHeight";
        public static final String GET_ACTUAL_HEIGHT_SRG = "func_72800_K";
        
        public static final String CAN_SNOW_AT = "canSnowAt";
        public static final String CAN_SNOW_AT_SRG = "func_175670_a";
        
        public static final String CAN_BLOCK_FREEZE = "canBlockFreeze";
        public static final String CAN_BLOCK_FREEZE_SRG = "func_175665_U";
    }
    
    public static final class Entity {
        public static final String CLASS = "net/minecraft/entity/Entity";
        public static final String CLASS_SRG = "vg";
        
        public static final String ON_ENTITY_UPDATE = "onEntityUpdate";
        public static final String ON_ENTITY_UPDATE_SRG = "func_70071_h_";
        
        public static final String SET_POSITION = "setPosition";
        public static final String SET_POSITION_SRG = "func_70107_b";
        
        public static final String MOVE_ENTITY = "moveEntity";
        public static final String MOVE_ENTITY_SRG = "func_70091_a";
        
        public static final String WORLD_FIELD = "world";
        public static final String WORLD_FIELD_SRG = "field_70170_p";
    }
    
    public static final class SPacketChunkData {
        public static final String CLASS = "net/minecraft/network/play/server/SPacketChunkData";
        public static final String CLASS_SRG = "ix";
        
        public static final String CALCULATE_CHUNK_SIZE = "calculateChunkSize";
        public static final String CALCULATE_CHUNK_SIZE_SRG = "func_189518_d";
        
        public static final String WRITE_CHUNK_DATA = "writeChunkData";
        public static final String WRITE_CHUNK_DATA_SRG = "func_189517_a";
    }
    
    public static final class WorldProvider {
        public static final String CLASS = "net/minecraft/world/WorldProvider";
        public static final String CLASS_SRG = "nu";
        
        public static final String GET_AVERAGE_GROUND_LEVEL = "getAverageGroundLevel";
        public static final String GET_AVERAGE_GROUND_LEVEL_SRG = "func_177495_o";
        
        public static final String GET_HEIGHT = "getHeight";
        public static final String GET_HEIGHT_SRG = "func_177498_l";
        
        public static final String GET_ACTUAL_HEIGHT = "getActualHeight";
        public static final String GET_ACTUAL_HEIGHT_SRG = "func_177493_a";
        
        public static final String GET_VOID_FOG_Y_FACTOR = "getVoidFogYFactor";
        public static final String GET_VOID_FOG_Y_FACTOR_SRG = "func_76565_k";
    }
    
    public static final class ExtendedBlockStorage {
        public static final String CLASS = "net/minecraft/world/chunk/storage/ExtendedBlockStorage";
        public static final String CLASS_SRG = "arc";
        
        public static final String GET_Y_LOCATION = "getYLocation";
        public static final String GET_Y_LOCATION_SRG = "func_76662_d";
    }
    
    public static final class PlayerChunkMap {
        public static final String CLASS = "net/minecraft/server/management/PlayerChunkMap";
        public static final String CLASS_SRG = "nm";
    }
    
    public static final class ChunkProviderClient {
        public static final String CLASS = "net/minecraft/client/multiplayer/ChunkProviderClient";
        public static final String CLASS_SRG = "brx";
    }
    
    public static final class BlockPos {
        public static final String CLASS = "net/minecraft/util/math/BlockPos";
        public static final String CLASS_SRG = "et";
        
        public static final String GET_Y = "getY";
        public static final String GET_Y_SRG = "func_177956_o";
    }
    
    public static final class EnumSkyBlock {
        public static final String CLASS = "net/minecraft/world/EnumSkyBlock";
        public static final String CLASS_SRG = "bgk";
    }
    
    public static final class IBlockState {
        public static final String CLASS = "net/minecraft/block/state/IBlockState";
        public static final String CLASS_SRG = "awr";
    }
    
    public static final class PacketBuffer {
        public static final String CLASS = "net/minecraft/network/PacketBuffer";
        public static final String CLASS_SRG = "gy";
    }
}
