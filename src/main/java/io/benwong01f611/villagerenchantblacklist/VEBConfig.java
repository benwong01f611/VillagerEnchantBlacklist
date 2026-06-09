package io.benwong01f611.villagerenchantblacklist;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class VEBConfig {
    private static final Set<Enchantment> disabledEnchantments = new HashSet<>();
    private static final Map<Enchantment, Integer> maxLevels = new HashMap<>();

    public record EnchantmentRoll(Enchantment enchantment, int maxLevel) {}

    private static final List<Enchantment> enabledPool = new ArrayList<>();
    private static final Random RANDOM = new Random();

    // Load config file
    public static void load(FileConfiguration config) {
        disabledEnchantments.clear();
        maxLevels.clear();
        enabledPool.clear();

        // 1. Load disabled/banned enchantments
        if (config.contains("config.disable")) {
            for (String key : config.getStringList("config.disable")) {
                Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key.toLowerCase()));
                if (enchantment != null) {
                    disabledEnchantments.add(enchantment);
                }
            }
        }

        // 2. Load custom maximum levels
        ConfigurationSection maxLevelSection = config.getConfigurationSection("config.max_level");
        if (maxLevelSection != null) {
            for (String key : maxLevelSection.getKeys(false)) {
                int maxLevel = maxLevelSection.getInt(key);
                Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key.toLowerCase()));

                if (enchantment != null) {
                    maxLevels.put(enchantment, maxLevel);
                }
            }
        }

        // 3. Build the pool of allowed enchantments for random selection
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (!disabledEnchantments.contains(enchantment)) {
                enabledPool.add(enchantment);
            }
        }
    }

    /**
     * Checks if an enchantment is completely disabled.
     */
    public static boolean isEnchantmentDisabled(Enchantment enchantment) {
        return disabledEnchantments.contains(enchantment);
    }

    /**
     * Gets the max allowed level for an enchantment.
     * Fallbacks safely to the vanilla game max level if it isn't defined in config.yml.
     */
    public static int getMaxEnchantmentLevel(Enchantment enchantment) {
        return maxLevels.getOrDefault(enchantment, enchantment.getMaxLevel());
    }

    /**
     * Randomly selects an enabled enchantment from the server registry
     * and pairs it with its configured maximum level.
     * * @return An EnchantmentRoll object containing the enchantment and max level, or null if the pool is empty.
     */
    public static EnchantmentRoll getRandomEnchantment(){
        if (enabledPool.isEmpty()) {
            return null;
        }

        // Select a random enchantment from our permitted pool
        Enchantment randomEnchantment = enabledPool.get(RANDOM.nextInt(enabledPool.size()));

        // Fetch its max level (custom config value or vanilla default)
        int maxLevel = getMaxEnchantmentLevel(randomEnchantment);

        return new EnchantmentRoll(randomEnchantment, maxLevel);
    }
}
