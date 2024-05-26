package lunatic.adhaevent.eventlistener;

import lunatic.adhaevent.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CowListener implements Listener {
    private final Main plugin;

    public CowListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdhaCowInteract(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.COW)) {
            if (event.getDamager() instanceof Player) {
                Entity entity = event.getEntity();
                Cow cow = (Cow) entity;
                Player player = (Player) event.getDamager();
                if (cow.getCustomName() != null && cow.getCustomName().startsWith("§aAdha Cow")) {
                    // Allow the cow to take damage
                    double newHealth = cow.getHealth() - 1;
                    // Check if the cow would die from this hit
                    if (newHealth <= 0) {
                        double maxHealth = cow.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                        String customName = String.format("§aAdha Cow §c[§c0.0/§e%.1f§c]", newHealth, maxHealth);
                        cow.setCustomName(customName);
                        cow.setCustomNameVisible(true);
                        cow.setSilent(true);
                        cow.getWorld().playSound(cow.getLocation(), Sound.ENTITY_COW_DEATH, 15f, 0.6f);
                        cow.setHealth(0);
                        spawnArmorStand(player, cow.getLocation());
                    } else {
                        // Update the cow's health and name
                        cow.setHealth(newHealth);
                        double maxHealth = cow.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                        String customName = String.format("§aAdha Cow §c[§e%.1f§7/§e%.1f§c]", newHealth, maxHealth);
                        cow.setCustomName(customName);
                        cow.setCustomNameVisible(true);
                        cow.getWorld().playSound(cow.getLocation(), Sound.ENTITY_COW_HURT, 15f, 0.6f);
                        // Cancel the original damage event
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAdhaCowShear(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (entity.getType() == EntityType.COW && player.getInventory().getItemInMainHand().getType() == Material.SHEARS) {
            Cow cow = (Cow) entity;
            if (cow.getCustomName() != null && cow.getCustomName().startsWith("§aAdha Cow")) {
                ItemStack specialLeather = new ItemStack(Material.LEATHER);
                ItemMeta meta = specialLeather.getItemMeta();
                meta.setDisplayName("§6Special Leather");
                specialLeather.setItemMeta(meta);
                cow.getWorld().dropItemNaturally(cow.getLocation(), specialLeather);
                cow.getWorld().playSound(cow.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1.0f, 1.0f);
                cow.setHealth(0);
            }
        }
    }

    @EventHandler
    public void onAdhaCowDeath(EntityDeathEvent event) {
        if (event.getEntity().getType().equals(EntityType.COW)) {
            Cow cow = (Cow) event.getEntity();
            if (cow.getCustomName() != null && cow.getCustomName().startsWith("§aAdha Cow")) {
                // Clear drops
                event.getDrops().clear();
            }
        }
    }

    @EventHandler
    public void onArmorStandInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (entity instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) entity;
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (armorStand.getCustomName().equals("§a" + player.getName() + "'s Cow Head")) {
                event.setCancelled(true);
                if (itemInHand.getType() == Material.BUCKET && itemInHand.getItemMeta() != null){
                    // Retrieve lore and check max and stored heads
                    ItemMeta itemMeta = itemInHand.getItemMeta();
                    List<String> lore = itemMeta.getLore();
                    if (lore != null && lore.size() >= 2) {
                        String maxHeadsLine = lore.get(0);
                        String storedHeadsLine = lore.get(1);

                        int maxHeads = Integer.parseInt(maxHeadsLine.split(": ")[1]);
                        int storedHeads = Integer.parseInt(storedHeadsLine.split(": ")[1]);

                        if (storedHeads < maxHeads) {
                            // Increment stored heads
                            storedHeads++;

                            // Update lore
                            lore.set(1, "§4Stored Heads: " + storedHeads);
                            itemMeta.setLore(lore);
                            itemInHand.setItemMeta(itemMeta);

                            // Display cloud particles and remove the armor stand
                            armorStand.getWorld().spawnParticle(Particle.CLOUD, armorStand.getLocation().add(0, 1, 0), 20);
                            armorStand.remove();

                            // Optionally, play a sound
                            armorStand.getWorld().playSound(armorStand.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1.0f, 1.0f);
                        }else{
                            player.sendMessage("§cKamu telah mencapai batas maksimal karung!");
                        }
                    }
                }
            }
        }
    }

    private void spawnArmorStand(Player player, Location location) {
        ArmorStand armorStand = location.getWorld().spawn(location.subtract(0, 0.7, 0), ArmorStand.class);
        armorStand.setCustomName("§a" + player.getName() + "'s Cow Head");
        armorStand.setHelmet(Main.getHead("cowHead"));
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false); // Make the armor stand invisible
        plugin.armorStandList.add(armorStand);

        new BukkitRunnable() {
            public void run() {
                armorStand.remove();
            }
        }.runTaskLater(plugin, 60);
    }
}
