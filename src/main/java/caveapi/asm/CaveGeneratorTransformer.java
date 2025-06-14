package caveapi.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class CaveGeneratorTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "net.minecraft.world.gen.ChunkProviderOverworld";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals(TARGET_CLASS)) {
            return basicClass;
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if ("func_185931_b".equals(method.name) || "generateTerrain".equals(method.name)) {
                injectCaveGeneration(method);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void injectCaveGeneration(MethodNode method) {
        InsnList instructions = method.instructions;
        AbstractInsnNode targetNode = null;

        // 定位地形生成完成后的位置
        for (AbstractInsnNode node : instructions) {
            if (node.getOpcode() == Opcodes.RETURN) {
                targetNode = node;
                break;
            }
        }

        if (targetNode != null) {
            InsnList injection = new InsnList();
            injection.add(new VarInsnNode(Opcodes.ALOAD, 0));  // this
            injection.add(new VarInsnNode(Opcodes.ALOAD, 1));  // chunkX
            injection.add(new VarInsnNode(Opcodes.ALOAD, 2));  // chunkZ
            injection.add(new VarInsnNode(Opcodes.ALOAD, 3));  // chunkPrimer
            injection.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/blackgear/cavebiomes/core/api/CaveGeneratorHooks",
                    "generateCaves",
                    "(Lnet/minecraft/world/gen/ChunkProviderOverworld;IILnet/minecraft/world/chunk/ChunkPrimer;)V",
                    false
            ));

            instructions.insertBefore(targetNode, injection);
        }
    }
}