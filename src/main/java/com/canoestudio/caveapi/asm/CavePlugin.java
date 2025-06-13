package com.canoestudio.caveapi.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("CaveAPI Core")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("com.canoestudio.caveapi.asm")
public class CavePlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{CaveASMTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return "com.canoestudio.caveapi.CaveAPI";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}