package lunatic.adhaevent.eventlistener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import lunatic.adhaevent.Main;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CowListener implements Listener {
    private final Main plugin;
    private final Set<Integer> armorStandIds = new HashSet<>();
    public Map<Integer, Location> armorStandLocations = new HashMap<>();
    public CowListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdhaCowInteract(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.COW) ||
                event.getEntity().getType().equals(EntityType.CAMEL)||
                event.getEntity().getType().equals(EntityType.GOAT) ||
                event.getEntity().getType().equals(EntityType.SHEEP)) {
            if (event.getDamager() instanceof Player) {
                Entity entity = event.getEntity();
                LivingEntity livingEntity = (LivingEntity) entity;
                EntityType type = livingEntity.getType();
                Player player = (Player) event.getDamager();
                ItemStack weapon = player.getInventory().getItemInMainHand();
                double damage = event.getDamage();
                if (livingEntity.getCustomName() != null && livingEntity.getCustomName().startsWith("§aAdha ")) {
                    double newHealth = livingEntity.getHealth() - damage;
                    if (newHealth <= 0) {
                        double maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                        String customName = String.format("§aAdha " + toLowerCaseAndCapitalize(type.toString()) + " §c[§c0.0/§e%.1f§c]", newHealth, maxHealth);
                        livingEntity.setCustomName(customName);
                        livingEntity.setCustomNameVisible(true);
                        livingEntity.setSilent(true);
//                        livingEntity.setHealth(0);
                        if (plugin.getMmoItemsHook().isMMOItem(weapon) && plugin.getMmoItemsHook().getMMOItemId(weapon).equalsIgnoreCase("SACRIFICES_BLADE")) {
                            spawnArmorStandPacket(player, livingEntity.getLocation(), type);
                            if (Math.random() < 0.15) {
                                ItemStack specialLeather = plugin.getMmoItemsHook().getMMOItemsItemStack(Type.get("MATERIAL"), "SACRED_SKIN");
                                int amount = (int) (Math.random() * 2) + 1;
                                for (int i = 0; i < amount; i++) {
                                    livingEntity.getWorld().dropItemNaturally(livingEntity.getLocation(), specialLeather);
                                }
                            }
                        } else {
                            if (Math.random() < 0.3) {
                                spawnArmorStandPacket(player, livingEntity.getLocation(), type);
                            }
                        }
                        switch (type) {
                            case COW:
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_COW_DEATH, 15f, 0.6f);
                                break;
                            case CAMEL:
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_CAMEL_DEATH, 15f, 0.6f);
                                break;
                            case GOAT:
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_GOAT_DEATH, 15f, 0.6f);
                                break;
                            case SHEEP:
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_SHEEP_DEATH, 15f, 0.6f);
                                break;
                        }
                    } else {
                        livingEntity.setHealth(newHealth);
                        double maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                        String customName = String.format("§aAdha " + toLowerCaseAndCapitalize(type.toString()) + " §c[§e%.1f§7/§e%.1f§c]", newHealth, maxHealth);
                        livingEntity.setCustomName(customName);
                        livingEntity.setCustomNameVisible(true);
                        event.setCancelled(true);
                        switch (type) {
                            case COW:
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_COW_HURT, 15f, 0.6f);
                                break;
                            case CAMEL:
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_CAMEL_HURT, 15f, 0.6f);
                                break;
                            case GOAT:
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_GOAT_HURT, 15f, 0.6f);
                                break;
                            case SHEEP:
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_SHEEP_HURT, 15f, 0.6f);
                                break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void collectSkin(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (entity.getType() == EntityType.COW ||
                entity.getType() == EntityType.SHEEP ||
                entity.getType() == EntityType.CAMEL ||
                entity.getType() == EntityType.GOAT) {

            if (plugin.getMmoItemsHook().isMMOItem(weapon)) {
                if (plugin.getMmoItemsHook().getMMOItemId(weapon).equalsIgnoreCase("SACRIFICES_EDGE")) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (livingEntity.getCustomName() != null && livingEntity.getCustomName().startsWith("§aAdha ")) {
                        if (livingEntity.getHealth() > 15) {

                            ItemStack specialLeather = plugin.getMmoItemsHook().getMMOItemsItemStack(Type.get("MATERIAL"), "SACRED_SKIN");
                            int amount = (int) (Math.random() * 2) + 1;
                            for (int i = 0; i < amount; i++) {
                                livingEntity.getWorld().dropItemNaturally(livingEntity.getLocation(), specialLeather);
                            }
                            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1.0f, 1.0f);
                            livingEntity.setHealth(0);

                            DurabilityItem durabilityItem = new DurabilityItem(player, weapon);
                            durabilityItem.decreaseDurability(1);
                            player.getInventory().setItemInMainHand(durabilityItem.toItem());
                        }
                    }
                }
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.COW ||
                event.getEntityType() == EntityType.CAMEL ||
                event.getEntityType() == EntityType.GOAT ||
                event.getEntityType() == EntityType.SHEEP) {
            System.out.println(event.getSpawnReason());
            System.out.println(event.getLocation());
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL ||
                    event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG ||
                    event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN) {

                Location spawnLocation = event.getEntity().getLocation();
                event.setCancelled(true);

                EntityType type = event.getEntityType();
                LivingEntity adhaEntity = (LivingEntity) spawnLocation.getWorld().spawnEntity(spawnLocation, type);
                adhaEntity.setCustomName("§aAdha " + toLowerCaseAndCapitalize(type.toString()) + " §c[§e30.0§7/§e30.0§c]");
                adhaEntity.setCustomNameVisible(true);
                adhaEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
                adhaEntity.setHealth(30);
                adhaEntity.getPersistentDataContainer().getKeys();
                System.out.println("§a" + type + " §aentity spawned at " + spawnLocation);
            }
        }
    }

    private void spawnArmorStand(Player player, Location location) {
        ArmorStand armorStand = location.getWorld().spawn(location.subtract(0, 0.8, 0), ArmorStand.class);
        armorStand.setCustomName("§b" + player.getName() + "§b's §aCow Head");
        armorStand.setCustomNameVisible(true);
        armorStand.setHelmet(Main.getHead("cowHead"));
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false); // Make the armor stand invisible

        new BukkitRunnable() {
            public void run() {
                armorStand.remove();
            }
        }.runTaskLater(plugin, 200);
    }

    private void spawnArmorStandPacket(Player player, Location location, EntityType entityType) {
        int entityId = (int) (Math.random() * Integer.MAX_VALUE);
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        String entityHead = null;
        String entityName = null;
        switch (entityType) {
            case COW:
                entityHead = "cowHead";
                entityName = "Cow Head";
                break;
            case CAMEL:
                entityHead = "camelHead";
                entityName = "Camel Head";
                break;
            case GOAT:
                entityHead = "goatHead";
                entityName = "Goat Head";
                break;
            case SHEEP:
                entityHead = "sheepHead";
                entityName = "Sheep Head";
                break;
        }

        spawnPacket.getIntegers().write(0, entityId);
        spawnPacket.getUUIDs().write(0, UUID.randomUUID());
        spawnPacket.getDoubles().write(0, location.getX());
        spawnPacket.getDoubles().write(1, location.getY() - 0.7);
        spawnPacket.getDoubles().write(2, location.getZ());
        spawnPacket.getBytes().write(0, (byte)0); // Pitch
        spawnPacket.getBytes().write(1, (byte)0); // Yaw
        spawnPacket.getIntegers().write(2, 0);   // Head Yaw
        spawnPacket.getIntegers().write(3, 0); // Entity Data
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);

        PacketContainer metaDataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        List<WrappedDataValue> metadata = List.of(
                new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0x20),
                new WrappedDataValue(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(Component.text(entityName))).getHandle())),
                new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), true),
                new WrappedDataValue(5, WrappedDataWatcher.Registry.get(Boolean.class), true),
                new WrappedDataValue(15, WrappedDataWatcher.Registry.get(Byte.class), (byte) (0x01 | 0x08))
        );
        metaDataPacket.getIntegers().write(0, entityId);
        metaDataPacket.getDataValueCollectionModifier().writeSafely(0, metadata);

        PacketContainer equipmentPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        ItemStack head = Main.getHead(entityHead);
        equipmentPacket.getIntegers().write(0, entityId);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, head));
        equipmentPacket.getSlotStackPairLists().write(0, equipmentList);

        plugin.getProtocolManager().sendServerPacket(player, spawnPacket);
        plugin.getProtocolManager().sendServerPacket(player, metaDataPacket);
        plugin.getProtocolManager().sendServerPacket(player, equipmentPacket);
        plugin.storeArmorStandLocation(entityId, location, entityName);
    }

    private static String toLowerCaseAndCapitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Handle null or empty input
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
