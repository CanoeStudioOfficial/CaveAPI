package org.canoestudio.caveapi.core.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.canoestudio.caveapi.core.HeightConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.Type;

public class EntityTransformer implements IClassTransformer {

    private static final String ENTITY_CLASS = "net.minecraft.entity.Entity";
    private static final String ENTITY_SRG = "vg";
    
    private static final String ON_ENTITY_UPDATE_SRG = "func_70071_h_";
    private static final String SET_POSITION_SRG = "func_70107_b";
    private static final String GET_EYE_HEIGHT_SRG = "func_70047_e";
    private static final String MOVE_ENTITY_SRG = "func_70091_a";
    private static final String SET_LOCATION_AND_ANGLES_SRG = "func_70080_a";
    
    private static final String WORLD_FIELD_SRG = "field_70170_p";
    
    private static final double VANILLA_VOID_Y = -64.0D;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        
        if (!ENTITY_CLASS.equals(transformedName)) {
            return basicClass;
        }
        
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);
            
            transformEntityClass(classNode);
            
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform Entity class", e);
        }
    }
    
    private void transformEntityClass(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            String methodName = method.name;
            
            if (methodName.equals(ON_ENTITY_UPDATE_SRG) || methodName.equals("onEntityUpdate")) {
                transformOnEntityUpdate(method);
            } else if (methodName.equals(SET_POSITION_SRG) || methodName.equals("setPosition")) {
                transformSetPosition(method);
            } else if (methodName.equals(MOVE_ENTITY_SRG) || methodName.equals("moveEntity")) {
                transformMoveEntity(method);
            }
        }
    }
    
    private void transformOnEntityUpdate(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.DCONST_1) {
                AbstractInsnNode prev = insn.getPrevious();
                if (prev != null && prev.getOpcode() == Opcodes.DNEG) {
                    continue;
                }
                AbstractInsnNode next = insn.getNext();
                if (next != null && next.getOpcode() == Opcodes.DNEG) {
                    method.instructions.remove(next);
                    method.instructions.set(insn, new LdcInsnNode((double) HeightConfig.MIN_HEIGHT));
                }
            }
            
            if (insn.getOpcode() == Opcodes.LDC) {
                LdcInsnNode ldcInsn = (LdcInsnNode) insn;
                if (ldcInsn.cst instanceof Double) {
                    double value = (Double) ldcInsn.cst;
                    if (value == VANILLA_VOID_Y) {
                        ldcInsn.cst = (double) HeightConfig.MIN_HEIGHT;
                    } else if (value == -VANILLA_VOID_Y) {
                        ldcInsn.cst = (double) HeightConfig.MIN_HEIGHT;
                    }
                }
            }
        }
    }
    
    private void transformSetPosition(MethodNode method) {
        AbstractInsnNode[] insns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (insn.getOpcode() == Opcodes.DCONST_0) {
                AbstractInsnNode next = insn.getNext();
                if (next != null && next.getOpcode() == Opcodes.DCONST_0) {
                    continue;
                }
            }
            
            if (insn.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode intInsn = (IntInsnNode) insn;
                if (intInsn.operand == 256) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MAX_HEIGHT));
                }
            }
        }
    }
    
    private void transformMoveEntity(MethodNode method) {
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
                if (next != null && next.getOpcode() == Opcodes.IF_ICMPLT) {
                    method.instructions.set(insn, new LdcInsnNode(HeightConfig.MIN_HEIGHT));
                }
            }
        }
    }
}
