package caveapi.asm;

import net.minecraft.world.chunk.ChunkPrimer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;

public class CaveWorldTransformer implements Opcodes {
    private static final String TARGET_CLASS = "net.minecraft.world.gen.ChunkProviderOverworld";
    private static final String HOOK_CLASS = "com/blackgear/cavebiomes/asm/ASMHooks";

    public static byte[] transform(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        // 查找目标方法
        for (MethodNode method : classNode.methods) {
            if ("provideChunk".equals(method.name) &&
                    "(IILnet/minecraft/world/chunk/ChunkPrimer;)V".equals(method.desc)) {

                // 准备注入指令
                InsnList injection = new InsnList();

                // 加载参数到栈上: (primer, x, z)
                injection.add(new VarInsnNode(ALOAD, 3)); // primer
                injection.add(new VarInsnNode(ILOAD, 1));  // x
                injection.add(new VarInsnNode(ILOAD, 2));  // z

                // 调用钩子方法
                injection.add(new MethodInsnNode(
                        INVOKESTATIC,
                        HOOK_CLASS,
                        "afterChunkPrimed",
                        "(Lnet/minecraft/world/chunk/ChunkPrimer;II)V",
                        false
                ));

                // 查找方法返回点
                AbstractInsnNode returnNode = null;
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction.getOpcode() == RETURN) {
                        returnNode = instruction;
                        break;
                    }
                }

                if (returnNode != null) {
                    // 在返回指令前插入我们的代码
                    method.instructions.insertBefore(returnNode, injection);
                    System.out.println("成功注入洞穴生成器到 ChunkProviderOverworld");
                } else {
                    System.out.println("警告: 未能在 provideChunk 中找到返回指令");
                }
            }
        }

        // 将修改后的类转换为字节数组
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}