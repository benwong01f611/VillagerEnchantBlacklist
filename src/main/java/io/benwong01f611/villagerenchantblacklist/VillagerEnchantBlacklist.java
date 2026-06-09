package io.benwong01f611.villagerenchantblacklist;

import org.bukkit.plugin.java.JavaPlugin;


public final class VillagerEnchantBlacklist extends JavaPlugin {
    VEBConfig config;
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        VEBConfig.load(getConfig());
        getServer().getPluginManager().registerEvents(new Blacklist(), this);
        registerCommands();
        getLogger().info("VillagerEnchantBlacklist is enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("VillagerEnchantBlacklist is shutting down, bye!");
    }

    public void registerCommands(){
        // REGISTER THE NEW COMMAND HERE
        VEBCommand vebCommand = new VEBCommand(this);
        if (getCommand("veb") != null) {
            getCommand("veb").setExecutor(vebCommand);
            getCommand("veb").setTabCompleter(vebCommand);
        }
    }
}
