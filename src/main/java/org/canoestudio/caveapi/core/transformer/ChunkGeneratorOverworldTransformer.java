package org.canoestudio.caveapi.core.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.canoestudio.caveapi.core.HeightConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ChunkGeneratorOverworldTransformer implements IClassTransformer {

    private static final String CHUNK_GENERATOR_CLASS = "net.minecraft.world.gen.ChunkGeneratorOverworld";
    private static final String CHUNK_GENERATOR_SRG = "boc";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        
        if (!CHUNK_GENERATOR_CLASS.equals(transformedName)) {
            return basicClass;
        }
        
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);
            
            transformChunkGeneratorClass(classNode);
            
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform ChunkGeneratorOverworld class", e);
        }
    }
    
    private void transformChunkGeneratorClass(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            transformMethod(method);
        }
    }
    
    private void transformMethod(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    intInsn.operand = HeightConfig.TOTAL_HEIGHT;
                }
                if (intInsn.operand == 255) {
                    intInsn.operand = HeightConfig.MAX_HEIGHT - 1;
                }
            }
            
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 16) {
                    intInsn.operand = HeightConfig.CUBE_COUNT;
                }
            }
            
            if (insn.getOpcode() == Opcodes.ICONST_0) {
                AbstractInsnNode next = insn.getNext();
                if (next != null && (next.getOpcode() == Opcodes.IF_ICMPGE || next.getOpcode() == Opcodes.IF_ICMPLE)) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MIN_HEIGHT));
                }
            }
        }
    }
}
