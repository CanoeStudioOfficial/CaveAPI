package org.canoestudio.caveapi.core.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.canoestudio.caveapi.core.HeightConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ChunkTransformer implements IClassTransformer {

    private static final String CHUNK_CLASS = "net.minecraft.world.chunk.Chunk";
    private static final String CHUNK_SRG = "aes";
    
    private static final String STORAGE_ARRAYS_SRG = "field_76645_j";
    private static final String STORAGE_ARRAYS_MCP = "storageArrays";
    
    private static final String GET_BLOCK_STATE_SRG = "func_177435_g";
    private static final String SET_BLOCK_STATE_SRG = "func_177436_a";
    private static final String GET_BLOCK_STATE_INT_SRG = "func_76611_b";
    private static final String GET_LIGHT_FOR_SRG = "func_177413_a";
    private static final String SET_LIGHT_FOR_SRG = "func_177417_a";
    private static final String GET_LIGHT_SUBTRACTED_SRG = "func_76628_c";
    private static final String GENERATE_SKYLIGHT_MAP_SRG = "func_76630_e";
    private static final String GET_HEIGHT_VALUE_SRG = "func_76627_g";
    private static final String GET_TOP_FILLED_SEGMENT_SRG = "func_76625_h";
    
    private static final String EXTENDED_BLOCK_STORAGE = "net/minecraft/world/chunk/storage/ExtendedBlockStorage";
    private static final String BLOCK_POS = "net/minecraft/util/math/BlockPos";
    private static final String I_BLOCK_STATE = "net/minecraft/block/state/IBlockState";
    private static final String ENUM_SKY_BLOCK = "net/minecraft/world/EnumSkyBlock";
    private static final String CHUNK_PRIMER = "net/minecraft/world/chunk/ChunkPrimer";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        
        if (!CHUNK_CLASS.equals(transformedName)) {
            return basicClass;
        }
        
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);
            
            transformChunkClass(classNode);
            
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform Chunk class", e);
        }
    }
    
    private void transformChunkClass(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            String methodName = method.name;
            String methodDesc = method.desc;
            
            if (methodName.equals(GET_BLOCK_STATE_INT_SRG) || methodName.equals("getBlockState")) {
                if (methodDesc.equals("(III)Lnet/minecraft/block/state/IBlockState;")) {
                    transformGetBlockStateInt(method);
                }
            } else if (methodName.equals(GET_BLOCK_STATE_SRG) || methodName.equals("getBlockState")) {
                if (methodDesc.equals("(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;")) {
                    transformGetBlockState(method);
                }
            } else if (methodName.equals(SET_BLOCK_STATE_SRG) || methodName.equals("setBlockState")) {
                transformSetBlockState(method);
            } else if (methodName.equals(GET_LIGHT_FOR_SRG) || methodName.equals("getLightFor")) {
                transformGetLightFor(method);
            } else if (methodName.equals(SET_LIGHT_FOR_SRG) || methodName.equals("setLightFor")) {
                transformSetLightFor(method);
            } else if (methodName.equals(GET_LIGHT_SUBTRACTED_SRG) || methodName.equals("getLightSubtracted")) {
                transformGetLightSubtracted(method);
            } else if (methodName.equals(GENERATE_SKYLIGHT_MAP_SRG) || methodName.equals("generateSkylightMap")) {
                transformGenerateSkylightMap(method);
            } else if (methodName.equals("<init>")) {
                if (methodDesc.contains("ChunkPrimer")) {
                    transformChunkPrimerConstructor(method);
                } else {
                    transformConstructor(method);
                }
            }
        }
    }
    
    private void transformConstructor(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    intInsn.operand = HeightConfig.CUBE_COUNT;
                }
            } else if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    intInsn.operand = HeightConfig.TOTAL_HEIGHT;
                }
            }
        }
    }
    
    private void transformChunkPrimerConstructor(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (int i = 0; i < insns.length; i++) {
            AbstractInsnNode insn = insns[i];
            
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    AbstractInsnNode next = insn.getNext();
                    if (next != null && next.getOpcode() == Opcodes.ISTORE) {
                        intInsn.operand = HeightConfig.CUBE_COUNT;
                    }
                }
            } else if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    intInsn.operand = HeightConfig.TOTAL_HEIGHT;
                }
            } else if (insn.getOpcode() == Opcodes.ICONST_0) {
                AbstractInsnNode prev = insn.getPrevious();
                if (prev != null && prev.getOpcode() == Opcodes.ISTORE) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MIN_HEIGHT));
                }
            }
        }
    }
    
    private void transformGetBlockStateInt(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (int i = 0; i < insns.length; i++) {
            AbstractInsnNode insn = insns[i];
            
            if (insn.getOpcode() == Opcodes.ILOAD) {
                VarInsnNode varInsn = (VarInsnNode) insn;
                if (varInsn.var == 2) {
                    AbstractInsnNode next = insn.getNext();
                    if (next != null && next.getOpcode() == Opcodes.IFGE) {
                        JumpInsnNode jump = (JumpInsnNode) next;
                        method.instructions.insertBefore(insn, new VarInsnNode(Opcodes.ILOAD, 2));
                        method.instructions.insertBefore(insn, new LdcInsnNode(HeightConfig.MIN_HEIGHT));
                        LabelNode newLabel = new LabelNode();
                        method.instructions.insertBefore(insn, new JumpInsnNode(Opcodes.IF_ICMPLT, newLabel));
                        method.instructions.insertBefore(insn, newLabel);
                    }
                }
            }
            
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.CUBE_COUNT));
                }
            }
            
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
                }
            }
            
            if (insn.getOpcode() == Opcodes.ICONST_0) {
                AbstractInsnNode prev = insn.getPrevious();
                if (prev != null && prev.getOpcode() == Opcodes.IF_ICMPGE) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MIN_HEIGHT));
                }
            }
        }
    }
    
    private void transformGetBlockState(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
                }
            }
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.CUBE_COUNT));
                }
            }
        }
    }
    
    private void transformSetBlockState(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
                }
            }
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.CUBE_COUNT));
                }
            }
        }
    }
    
    private void transformGetLightFor(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
                }
            }
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.CUBE_COUNT));
                }
            }
        }
    }
    
    private void transformSetLightFor(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
                }
            }
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.CUBE_COUNT));
                }
            }
        }
    }
    
    private void transformGetLightSubtracted(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
                }
            }
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.CUBE_COUNT));
                }
            }
        }
    }
    
    private void transformGenerateSkylightMap(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.CUBE_COUNT));
                }
            }
        }
    }
}
