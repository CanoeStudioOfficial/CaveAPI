package org.canoestudio.caveapi.core.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.canoestudio.caveapi.core.HeightConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class SPacketChunkDataTransformer implements IClassTransformer {

    private static final String PACKET_CLASS = "net.minecraft.network.play.server.SPacketChunkData";
    private static final String PACKET_SRG = "ix";
    
    private static final String CONSTRUCTOR = "<init>";
    private static final String CALCULATE_CHUNK_SIZE_SRG = "func_189518_d";
    private static final String WRITE_CHUNK_DATA_SRG = "func_189517_a";
    private static final String READ_PACKET_DATA_SRG = "func_148837_a";
    private static final String WRITE_PACKET_DATA_SRG = "func_148840_b";
    
    private static final String CHUNK_CLASS = "net/minecraft/world/chunk/Chunk";
    private static final String EXTENDED_BLOCK_STORAGE = "net/minecraft/world/chunk/storage/ExtendedBlockStorage";
    private static final String PACKET_BUFFER = "net/minecraft/network/PacketBuffer";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        
        if (!PACKET_CLASS.equals(transformedName)) {
            return basicClass;
        }
        
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);
            
            transformPacketClass(classNode);
            
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform SPacketChunkData class", e);
        }
    }
    
    private void transformPacketClass(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            String methodName = method.name;
            
            if (methodName.equals(CALCULATE_CHUNK_SIZE_SRG) || methodName.equals("calculateChunkSize")) {
                transformCalculateChunkSize(method);
            } else if (methodName.equals(WRITE_CHUNK_DATA_SRG) || methodName.equals("writeChunkData")) {
                transformWriteChunkData(method);
            } else if (methodName.equals(CONSTRUCTOR)) {
                transformConstructor(method);
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
            }
        }
    }
    
    private void transformCalculateChunkSize(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    intInsn.operand = HeightConfig.CUBE_COUNT;
                }
            }
            
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    intInsn.operand = HeightConfig.TOTAL_HEIGHT;
                }
            }
        }
    }
    
    private void transformWriteChunkData(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    intInsn.operand = HeightConfig.CUBE_COUNT;
                }
            }
            
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    intInsn.operand = HeightConfig.TOTAL_HEIGHT;
                }
            }
        }
    }
}
