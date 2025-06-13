package com.canoestudio.caveapi.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;




public class CaveASMTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "net.minecraft.world.gen.ChunkProviderOverworld";
    private static final String TARGET_METHOD = "func_185931_b"; // generate

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals(TARGET_CLASS)) return basicClass;

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if ((method.name.equals(TARGET_METHOD) || method.name.equals("generate")) &&
                    method.desc.equals("(IILnet/minecraft/world/chunk/ChunkPrimer;)V")) {
                injectCaveHook(method);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void injectCaveHook(MethodNode method) {
        InsnList insns = method.instructions;
        AbstractInsnNode targetNode = null;

        // 寻找洞穴生成位置（在原版洞穴生成后）
        for (AbstractInsnNode node : insns) {
            if (node.getOpcode() == RETURN) {
                targetNode = node;
                break;
            }
        }


        if (targetNode == null) return;

        InsnList hooks = new InsnList();
        hooks.add(new VarInsnNode(ALOAD, 0));  // this
        hooks.add(new FieldInsnNode(GETFIELD, "net/minecraft/world/gen/ChunkProviderOverworld", "field_73224_f", "Lnet/minecraft/world/World;"));
        hooks.add(new VarInsnNode(ILOAD, 1));   // chunkX
        hooks.add(new VarInsnNode(ILOAD, 2));   // chunkZ
        hooks.add(new VarInsnNode(ALOAD, 3));   // primer
        hooks.add(new MethodInsnNode(INVOKESTATIC,
                "com/canoestudio/caveapi/core/CaveHooks",
                "handleCaveGeneration",
                "(Lnet/minecraft/world/World;IILnet/minecraft/world/chunk/ChunkPrimer;)V",
                false));

        insns.insertBefore(targetNode, hooks);
    }
}