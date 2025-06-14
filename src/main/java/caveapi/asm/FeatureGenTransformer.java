package caveapi.asm;

import org.objectweb.asm.*;
import net.minecraft.launchwrapper.IClassTransformer;

public class FeatureGenTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if ("net.minecraft.world.gen.ChunkProviderOverworld".equals(transformedName)) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cr.accept(new FeatureGenAdapter(cw), 0);
            return cw.toByteArray();
        }
        return bytes;
    }

    private static class FeatureGenAdapter extends ClassVisitor {
        public FeatureGenAdapter(ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if ("func_180520_a".equals(name) ||
                    "(IILnet/minecraft/world/chunk/Chunk;)V".equals(desc)) {
                return new FeatureGenMethodVisitor(mv);
            }
            return mv;
        }
    }

    private static class FeatureGenMethodVisitor extends MethodVisitor {
        public FeatureGenMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name,
                                    String desc, boolean itf) {
            // 跳过结构生成的调用点
            if ("net/minecraft/world/gen/structure/MapGenStructure".equals(owner) &&
                    "func_175794_a".equals(name)) {
                return; // 删除结构生成调用
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}