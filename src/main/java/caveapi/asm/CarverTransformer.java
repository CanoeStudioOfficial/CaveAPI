package caveapi.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

class CarverTransformer implements net.minecraftforge.fml.common.asm.transformers.IEarlyTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if ("net.minecraft.world.gen.ChunkProviderSettings".equals(transformedName)) {
            return injectCustomCarver(bytes);
        }
        return bytes;
    }

    private byte[] injectCustomCarver(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if ("getObjectFromString".equals(method.name)) {
                // 在方法返回前插入自定义雕刻器
                InsnList inject = new InsnList();
                inject.add(new VarInsnNode(Opcodes.ALOAD, 4)); // 加载List<MapGenBase>参数
                inject.add(new TypeInsnNode(Opcodes.NEW, "com/blackgear/cavebiomes/common/world/gen/NoiseCarver"));
                inject.add(new InsnNode(Opcodes.DUP));
                inject.add(new LdcInsnNode(1.0F)); // 传入probability参数
                inject.add(new MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        "com/blackgear/cavebiomes/common/world/gen/NoiseCarver",
                        "<init>", "(F)V", false
                ));
                inject.add(new MethodInsnNode(
                        Opcodes.INVOKEINTERFACE,
                        "java/util/List",
                        "add", "(Ljava/lang/Object;)Z", true
                ));
                inject.add(new InsnNode(Opcodes.POP)); // 移除add的返回值

                method.instructions.insertBefore(method.instructions.getLast(), inject);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}