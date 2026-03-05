package org.canoestudio.caveapi.core.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.canoestudio.caveapi.core.HeightConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ChunkPrimerTransformer implements IClassTransformer {

    private static final String CHUNK_PRIMER_CLASS = "net.minecraft.world.chunk.ChunkPrimer";
    private static final String CHUNK_PRIMER_SRG = "bft";
    
    private static final String GET_BLOCK_STATE_SRG = "func_177856_a";
    private static final String SET_BLOCK_STATE_SRG = "func_177855_a";
    private static final String FIND_GROUND_BLOCK_IDX_SRG = "func_177858_a";
    
    private static final int OLD_DATA_SIZE = 65536;
    private static final int NEW_DATA_SIZE = 16 * HeightConfig.TOTAL_HEIGHT * 16;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        
        if (!CHUNK_PRIMER_CLASS.equals(transformedName)) {
            return basicClass;
        }
        
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);
            
            transformChunkPrimerClass(classNode);
            
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform ChunkPrimer class", e);
        }
    }
    
    private void transformChunkPrimerClass(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            String methodName = method.name;
            
            if (methodName.equals("<init>")) {
                transformConstructor(method);
            } else if (methodName.equals(GET_BLOCK_STATE_SRG) || methodName.equals("getBlockState")) {
                transformGetBlockState(method);
            } else if (methodName.equals(SET_BLOCK_STATE_SRG) || methodName.equals("setBlockState")) {
                transformSetBlockState(method);
            } else if (methodName.equals(FIND_GROUND_BLOCK_IDX_SRG) || methodName.equals("findGroundBlockIdx")) {
                transformFindGroundBlockIdx(method);
            }
        }
    }
    
    private void transformConstructor(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.LDC) {
                LdcInsnNode ldcInsn = (LdcInsnNode) insn;
                if (ldcInsn.cst instanceof Integer) {
                    int value = (Integer) ldcInsn.cst;
                    if (value == OLD_DATA_SIZE) {
                        ldcInsn.cst = NEW_DATA_SIZE;
                    }
                }
            }
        }
    }
    
    private void transformGetBlockState(MethodNode method) {
        InsnList newInstructions = new InsnList();
        LabelNode labelValid = new LabelNode();
        LabelNode labelReturnAir = new LabelNode();
        
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/chunk/ChunkPrimer", "field_177860_a", "[C"));
        
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        newInstructions.add(new LdcInsnNode(-HeightConfig.MIN_HEIGHT));
        newInstructions.add(new InsnNode(Opcodes.IADD));
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
        newInstructions.add(new LdcInsnNode(HeightConfig.TOTAL_HEIGHT));
        newInstructions.add(new InsnNode(Opcodes.IMUL));
        newInstructions.add(new InsnNode(Opcodes.IADD));
        newInstructions.add(new LdcInsnNode(16));
        newInstructions.add(new InsnNode(Opcodes.IMUL));
        newInstructions.add(new InsnNode(Opcodes.IADD));
        
        newInstructions.add(new InsnNode(Opcodes.CALOAD));
        newInstructions.add(new VarInsnNode(Opcodes.ISTORE, 4));
        
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
        newInstructions.add(new JumpInsnNode(Opcodes.IFNE, labelValid));
        
        newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/init/Blocks", "field_150350_a", "Lnet/minecraft/block/Block;"));
        newInstructions.add(new InsnNode(Opcodes.ARETURN));
        
        newInstructions.add(labelValid);
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/block/Block", "func_176220_d", "(I)Lnet/minecraft/block/state/IBlockState;", false));
        newInstructions.add(new InsnNode(Opcodes.ARETURN));
        
        method.instructions.clear();
        method.instructions.add(newInstructions);
    }
    
    private void transformSetBlockState(MethodNode method) {
        InsnList newInstructions = new InsnList();
        
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/chunk/ChunkPrimer", "field_177860_a", "[C"));
        
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        newInstructions.add(new LdcInsnNode(-HeightConfig.MIN_HEIGHT));
        newInstructions.add(new InsnNode(Opcodes.IADD));
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
        newInstructions.add(new LdcInsnNode(HeightConfig.TOTAL_HEIGHT));
        newInstructions.add(new InsnNode(Opcodes.IMUL));
        newInstructions.add(new InsnNode(Opcodes.IADD));
        newInstructions.add(new LdcInsnNode(16));
        newInstructions.add(new InsnNode(Opcodes.IMUL));
        newInstructions.add(new InsnNode(Opcodes.IADD));
        
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/block/Block", "func_176201_c", "(Lnet/minecraft/block/state/IBlockState;)I", false));
        newInstructions.add(new InsnNode(Opcodes.I2C));
        newInstructions.add(new InsnNode(Opcodes.CASTORE));
        
        newInstructions.add(new InsnNode(Opcodes.RETURN));
        
        method.instructions.clear();
        method.instructions.add(newInstructions);
    }
    
    private void transformFindGroundBlockIdx(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    intInsn.operand = HeightConfig.TOTAL_HEIGHT;
                }
            }
            
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    intInsn.operand = HeightConfig.CUBE_SIZE;
                }
            }
        }
    }
}
