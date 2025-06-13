package caveapi.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class CaveASMTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "net.minecraft.world.gen.ChunkGeneratorOverworld";
    private static final String HOOK_METHOD = "generateCaves";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals(TARGET_CLASS))
            return basicClass;

        System.out.println("[CaveAPI] Patching ChunkGeneratorOverworld");

        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.EXPAND_FRAMES);

        // 查找原版洞穴生成方法
        for (MethodNode mn : cn.methods) {
            if ("generate".equals(mn.name) || "func_180518_a".equals(mn.name)) {
                injectCaveHook(mn);
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private void injectCaveHook(MethodNode method) {
        AbstractInsnNode target = null;

        // 定位原版洞穴生成调用点
        for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
            AbstractInsnNode insn = it.next();
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                if (methodInsn.name.equals("generate") &&
                        methodInsn.owner.equals("net/minecraft/world/gen/MapGenCaves")) {
                    target = insn;
                    break;
                }
            }
        }

        if (target == null) {
            System.err.println("[CaveAPI] MapGenCaves.generate() not found!");
            return;
        }

        InsnList hook = new InsnList();
        // 替换为：CaveGenerator.generateCaves(...)
        hook.add(new MethodInsnNode(
                INVOKESTATIC,
                "caveapi/world/gen/CaveGenerator",
                HOOK_METHOD,
                "(Lnet/minecraft/world/World;IILnet/minecraft/world/chunk/ChunkPrimer;)V",
                false
        ));

        method.instructions.insert(target, hook);
        method.instructions.remove(target);
        System.out.println("[CaveAPI] Successfully injected cave generation hook");
    }
}