package org.canoestudio.caveapi.core.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.canoestudio.caveapi.core.HeightConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class WorldTransformer implements IClassTransformer {

    private static final String WORLD_CLASS = "net.minecraft.world.World";
    private static final String WORLD_SRG = "amu";
    
    private static final String IS_OUTSIDE_BUILD_HEIGHT_SRG = "func_175701_a";
    private static final String GET_LIGHT_SRG = "func_175671_l";
    private static final String GET_LIGHT_FOR_SRG = "func_175699_k";
    private static final String IS_AREA_LOADED_SRG = "func_175703_a";
    private static final String IS_BLOCK_LOADED_SRG = "func_175667_e";
    private static final String GET_ACTUAL_HEIGHT_SRG = "func_72800_K";
    private static final String CAN_SNOW_AT_SRG = "func_175670_a";
    private static final String CAN_BLOCK_FREEZE_SRG = "func_175665_U";
    private static final String GET_HEIGHT_SRG = "func_72800_K";
    
    private static final String BLOCK_POS = "net/minecraft/util/math/BlockPos";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        
        if (!WORLD_CLASS.equals(transformedName)) {
            return basicClass;
        }
        
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);
            
            transformWorldClass(classNode);
            
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform World class", e);
        }
    }
    
    private void transformWorldClass(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            String methodName = method.name;
            
            if (methodName.equals(IS_OUTSIDE_BUILD_HEIGHT_SRG) || methodName.equals("isOutsideBuildHeight")) {
                transformIsOutsideBuildHeight(method);
            } else if (methodName.equals(GET_LIGHT_SRG) || methodName.equals("getLight")) {
                transformGetLight(method);
            } else if (methodName.equals(GET_LIGHT_FOR_SRG) || methodName.equals("getLightFor")) {
                transformGetLightFor(method);
            } else if (methodName.equals(GET_ACTUAL_HEIGHT_SRG) || methodName.equals("getActualHeight")) {
                transformGetActualHeight(method);
            } else if (methodName.equals(CAN_SNOW_AT_SRG) || methodName.equals("canSnowAt")) {
                transformCanSnowAt(method);
            } else if (methodName.equals(CAN_BLOCK_FREEZE_SRG) || methodName.equals("canBlockFreeze")) {
                transformCanBlockFreeze(method);
            } else if (methodName.equals("canSnowAtBody")) {
                transformCanSnowAtBody(method);
            } else if (methodName.equals("canBlockFreezeBody")) {
                transformCanBlockFreezeBody(method);
            }
        }
    }
    
    private void transformIsOutsideBuildHeight(MethodNode method) {
        InsnList newInstructions = new InsnList();
        LabelNode labelTrue = new LabelNode();
        LabelNode labelFalse = new LabelNode();
        
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, BLOCK_POS, "func_177956_o", "()I", false));
        newInstructions.add(new LdcInsnNode(HeightConfig.MAX_HEIGHT));
        newInstructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, labelTrue));
        
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, BLOCK_POS, "func_177956_o", "()I", false));
        newInstructions.add(new LdcInsnNode(HeightConfig.MIN_HEIGHT));
        newInstructions.add(new JumpInsnNode(Opcodes.IF_ICMPLT, labelTrue));
        
        newInstructions.add(new InsnNode(Opcodes.ICONST_0));
        newInstructions.add(new JumpInsnNode(Opcodes.GOTO, labelFalse));
        
        newInstructions.add(labelTrue);
        newInstructions.add(new InsnNode(Opcodes.ICONST_1));
        
        newInstructions.add(labelFalse);
        newInstructions.add(new InsnNode(Opcodes.IRETURN));
        
        method.instructions.clear();
        method.instructions.add(newInstructions);
    }
    
    private void transformGetLight(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
                }
                if (intInsn.operand == 255) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT - 1));
                }
            }
            
            if (insn.getOpcode() == Opcodes.ICONST_0) {
                AbstractInsnNode next = insn.getNext();
                if (next != null && next.getOpcode() == Opcodes.IF_ICMPGE) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MIN_HEIGHT));
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
            
            if (insn.getOpcode() == Opcodes.ICONST_0) {
                AbstractInsnNode next = insn.getNext();
                if (next != null && next.getOpcode() == Opcodes.IF_ICMPGE) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MIN_HEIGHT));
                }
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
    
    private void transformCanSnowAt(MethodNode method) {
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
    
    private void transformCanBlockFreeze(MethodNode method) {
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
    
    private void transformCanSnowAtBody(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
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
    
    private void transformCanBlockFreezeBody(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
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
