package org.canoestudio.caveapi.core.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.canoestudio.caveapi.core.HeightConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class WorldProviderTransformer implements IClassTransformer {

    private static final String WORLD_PROVIDER_CLASS = "net.minecraft.world.WorldProvider";
    private static final String WORLD_PROVIDER_SRG = "nu";
    
    private static final String GET_AVERAGE_GROUND_LEVEL_SRG = "func_177495_o";
    private static final String GET_HEIGHT_SRG = "func_177498_l";
    private static final String GET_ACTUAL_HEIGHT_SRG = "func_177493_a";
    private static final String GET_VOID_FOG_Y_FACTOR_SRG = "func_76565_k";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        
        if (!WORLD_PROVIDER_CLASS.equals(transformedName)) {
            return basicClass;
        }
        
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);
            
            transformWorldProviderClass(classNode);
            
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform WorldProvider class", e);
        }
    }
    
    private void transformWorldProviderClass(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            String methodName = method.name;
            
            if (methodName.equals(GET_ACTUAL_HEIGHT_SRG) || methodName.equals("getActualHeight")) {
                transformGetActualHeight(method);
            } else if (methodName.equals(GET_HEIGHT_SRG) || methodName.equals("getHeight")) {
                transformGetHeight(method);
            } else if (methodName.equals(GET_VOID_FOG_Y_FACTOR_SRG) || methodName.equals("getVoidFogYFactor")) {
                transformGetVoidFogYFactor(method);
            }
        }
    }
    
    private void transformGetActualHeight(MethodNode method) {
        InsnList newInstructions = new InsnList();
        newInstructions.add(new LdcInsnNode(HeightConfig.TOTAL_HEIGHT));
        newInstructions.add(new InsnNode(Opcodes.IRETURN));
        
        method.instructions.clear();
        method.instructions.add(newInstructions);
    }
    
    private void transformGetHeight(MethodNode method) {
        InsnList newInstructions = new InsnList();
        newInstructions.add(new LdcInsnNode(HeightConfig.MAX_HEIGHT));
        newInstructions.add(new InsnNode(Opcodes.IRETURN));
        
        method.instructions.clear();
        method.instructions.add(newInstructions);
    }
    
    private void transformGetVoidFogYFactor(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
                }
            }
        }
    }
}
