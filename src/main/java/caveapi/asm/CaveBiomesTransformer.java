package caveapi.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class CaveBiomesTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "net.minecraft.world.gen.ChunkProviderOverworld";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals(TARGET_CLASS)) {
            // 处理混淆名称映射
            String mappedName = FMLDeobfuscatingRemapper.INSTANCE.map(TARGET_CLASS.replace('.', '/'));
            if (mappedName.equals(name.replace('.', '/'))) {
                System.out.println("正在处理区块生成器: " + name);
                return CaveWorldTransformer.transform(basicClass);
            }
        }
        return basicClass;
    }
}