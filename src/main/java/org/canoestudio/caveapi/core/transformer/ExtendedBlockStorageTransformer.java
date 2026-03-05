package org.canoestudio.caveapi.core.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.canoestudio.caveapi.core.HeightConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ExtendedBlockStorageTransformer implements IClassTransformer {

    private static final String EBS_CLASS = "net.minecraft.world.chunk.storage.ExtendedBlockStorage";
    private static final String EBS_SRG = "arc";
    
    private static final String CONSTRUCTOR = "<init>";
    private static final String GET_Y_LOCATION_SRG = "func_76662_d";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        
        if (!EBS_CLASS.equals(transformedName)) {
            return basicClass;
        }
        
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);
            
            transformEBSClass(classNode);
            
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform ExtendedBlockStorage class", e);
        }
    }
    
    private void transformEBSClass(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            String methodName = method.name;
            
            if (methodName.equals(CONSTRUCTOR)) {
                transformConstructor(method);
            }
        }
    }
    
    private void transformConstructor(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.TOTAL_HEIGHT));
                }
            }
        }
    }
}
