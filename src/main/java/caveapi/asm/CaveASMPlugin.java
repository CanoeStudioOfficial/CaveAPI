package caveapi.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import javax.annotation.Nullable;
import java.util.Map;

@Name("CaveASMPlugin")
@MCVersion("1.12.2")
@TransformerExclusions("caveapi.asm")
public class CaveASMPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"caveapi.asm.CaveASMTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return null; // 使用标准@Mod容器
    }

    @Override
    public String getSetupClass() {
        return null; // 不需要额外初始化
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // 可获取runtime属性如coremodLocation
    }

    @Override
    public String getAccessTransformerClass() {
        return null; // 不需要访问修饰符修改
    }
}