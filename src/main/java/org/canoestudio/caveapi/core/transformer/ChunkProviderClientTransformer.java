package org.canoestudio.caveapi.core.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.canoestudio.caveapi.core.HeightConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ChunkProviderClientTransformer implements IClassTransformer {

    private static final String CHUNK_PROVIDER_CLIENT_CLASS = "net.minecraft.client.multiplayer.ChunkProviderClient";
    private static final String CHUNK_PROVIDER_CLIENT_SRG = "brx";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        
        if (!CHUNK_PROVIDER_CLIENT_CLASS.equals(transformedName)) {
            return basicClass;
        }
        
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);
            
            transformChunkProviderClientClass(classNode);
            
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform ChunkProviderClient class", e);
        }
    }
    
    private void transformChunkProviderClientClass(ClassNode classNode) {
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
}
