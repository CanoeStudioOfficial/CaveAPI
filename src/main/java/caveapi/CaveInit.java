package caveapi;

import caveapi.config.CavePreset;
import caveapi.config.ConfigFile;
import caveapi.config.PresetReader;
import caveapi.util.HjsonTools;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static caveapi.io.SafeFileIO.ensureDirExists;
import static caveapi.io.SafeFileIO.fileExists;
import static caveapi.util.CommonMethods.*;

@Log4j2
public class CaveInit {

    /** A setting indicating the location where backups will be stored. */
    private static final String BACKUPS = CaveAPI.MODID + "/backup";

    /** A setting indicating the location where example presets will be kept. */
    private static final String EXAMPLES = CaveAPI.MODID + "/examples";

    /** A setting indicating the location where presets will be kept. */
    private static final String PRESETS = CaveAPI.MODID + "/presets";

    /** A setting indicating the location where variable imports will be kept. */
    private static final String IMPORTS = CaveAPI.MODID + "/imports";

    /** A setting indicating the location where generated presets will be kept. */
    private static final String GENERATED = CaveAPI.MODID + "/generated";

    /** A message to display when the preset directory is somehow unavailable. */
    private static final String NO_ACCESS = "Currently unable to access preset directory.";

    /** A message to display when failing to run mkdirs. */
    private static final String CANT_CREATE = "Unable to create directory";

    /** A list of valid extensions to compare against presets. */
    private static final List<String> EXTENSIONS = Arrays.asList("hjson", "json", "cave");

    private static final File CONFIG_DIR = Loader.instance().getConfigDir();

    public static final File CG_DIR = new File(CONFIG_DIR, CaveAPI.MODID);
    public static final File BACKUP_DIR = new File(CONFIG_DIR, BACKUPS);
    public static final File EXAMPLE_DIR = new File(CONFIG_DIR, EXAMPLES);
    public static final File PRESET_DIR = new File(CONFIG_DIR, PRESETS);
    public static final File IMPORT_DIR = new File(CONFIG_DIR, IMPORTS);
    public static final File GENERATED_DIR = new File(CONFIG_DIR, GENERATED);

    /** Initializes the supplied map with presets from the directory. */
    public static void initPresets(final Map<String, CavePreset> presets) {
        // Verify the folders' integrity before proceeding.
        ensureDirExists(PRESET_DIR).expect(CANT_CREATE);
        ensureDirExists(IMPORT_DIR).expect(CANT_CREATE);
        // Go ahead and final File dir = new File(CaveInit.CG_DIR, args[1]);lear this to allow presets to be reloaded.
        presets.clear();
        // Handle all files in the preset directory.
        presets.putAll(PresetReader.getPresets(PRESET_DIR, IMPORT_DIR));
        // If necessary, automatically write the expanded values.
        if (ConfigFile.autoGenerate) {
            ensureDirExists(GENERATED_DIR).expect(CANT_CREATE);
            presets.forEach((name, preset) -> {
                final File file = new File(GENERATED_DIR, name + ".cave");
                HjsonTools.writeJson(preset.raw, file).throwIfPresent();
            });
        }
        // Inform the user of which presets were loaded.
        printLoadedPresets(presets);
    }

    /** Attempts to locate a preset using each of the possible extensions. */
    public static Optional<File> locatePreset(String preset) {
        return locatePreset(PRESET_DIR, preset);
    }

    public static Optional<File> locatePreset(File directory, String preset) {
        final File presetFile = new File(directory,  preset);
        if (fileExists(presetFile, "Error checking file: " + presetFile)) {
            return full(presetFile);
        }
        for (String ext : EXTENSIONS) {
            final Optional<File> found = tryExtension(directory, preset, ext);
            if (found.isPresent()) {
                return found;
            }
        }
        return empty();
    }

    /** Attempts to locate a preset using a specific extension. */
    private static Optional<File> tryExtension(File directory, String preset, String extension) {
        final File presetFile = new File(directory, preset + "." + extension);
        if (fileExists(presetFile, NO_ACCESS)) {
            return full(presetFile);
        }
        return empty();
    }

    public static boolean validExtension(File file) {
        return EXTENSIONS.contains(extension(file));
    }

    /** Prints which presets are currently loaded and whether they are enabled. */
    private static void printLoadedPresets(final Map<String, CavePreset> presets) {
        for (Entry<String, CavePreset> entry : presets.entrySet()) {
            final String enabled = entry.getValue().enabled ? "enabled" : "disabled";
            log.info("{} is {}.", entry.getKey(), enabled);
        }
    }
}