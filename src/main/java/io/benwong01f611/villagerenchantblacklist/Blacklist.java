package io.benwong01f611.villagerenchantblacklist;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;


public class Blacklist implements Listener {

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        // Get Villager trading recipe
        MerchantRecipe recipe = event.getRecipe();
        // Get trade result item
        ItemStack result = recipe.getResult();

        // New trading recipe
        MerchantRecipe newRecipe;
        // New ItemStack for recipe
        ItemStack newItem = new ItemStack(Material.ENCHANTED_BOOK);
        // New enchantment meta
        EnchantmentStorageMeta newEnchantmentMeta = (EnchantmentStorageMeta) newItem.getItemMeta();

        boolean modified = false;

        // If the trade result item is NOT enchanted book, return
        if(result.getType() != Material.ENCHANTED_BOOK){ return; }

        // If the trade result item is enchanted book, check its enchantment level
        // Get item meta
        if(result.getItemMeta() instanceof EnchantmentStorageMeta bookMeta){
            // Get all enchantments (just in case there is multiple enchantment but will not happen)
            Map<Enchantment, Integer> enchantments = bookMeta.getStoredEnchants();
            // For each enchantment
            for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()){
                // Get its enchantment and level
                Enchantment enchantment = entry.getKey();
                Integer level = entry.getValue();
                // Get maximum level of that enchantment
                Integer maxLevel = VEBConfig.getMaxEnchantmentLevel(enchantment);

                // Check is the enchantment disabled
                if (VEBConfig.isEnchantmentDisabled(enchantment)) {
                    VEBConfig.EnchantmentRoll newRoll = VEBConfig.getRandomEnchantment();
                    enchantment = newRoll.enchantment();
                    level = newRoll.maxLevel();
                    maxLevel = level;
                    modified = true;
                    newEnchantmentMeta.addStoredEnchant(enchantment, maxLevel, true);
                    continue;
                }

                if(level < maxLevel){
                    // Basically copy the enchantments and set its level to max level
                    newEnchantmentMeta.addStoredEnchant(enchantment, maxLevel, true);
                    modified = true;
                }
                else{
                    newEnchantmentMeta.addStoredEnchant(enchantment, level, true);
                }
            }
        }
        if(!modified) { return; }

        // Set enchantment metadata
        newItem.setItemMeta(newEnchantmentMeta);
        // Create new recipe
        newRecipe = new MerchantRecipe(newItem,
                recipe.getUses(),
                recipe.getMaxUses(),
                recipe.hasExperienceReward(),
                recipe.getVillagerExperience(),
                recipe.getPriceMultiplier()
        );
        // Get ingredient (cost)
        List<ItemStack> originalIngredient = recipe.getIngredients();
        // Set ingredient (cost)
        for(ItemStack ingredient : originalIngredient){
            newRecipe.addIngredient(ingredient);
        }
        event.setRecipe(newRecipe);
    }
}
