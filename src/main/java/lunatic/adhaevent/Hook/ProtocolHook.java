package lunatic.adhaevent.Hook;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.lumine.mythic.lib.api.item.NBTItem;
import lunatic.adhaevent.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolHook extends PacketAdapter {
    private final Main adhaEvent;
    public Map<Integer, Location> armorStandLocations = new HashMap<>();
    private ProtocolManager protocolManager;

    public ProtocolHook(Main adhaEvent) {
        super(adhaEvent, ListenerPriority.HIGHEST, PacketType.Play.Client.USE_ENTITY);
        this.adhaEvent = adhaEvent;
        protocolManager = adhaEvent.getProtocolManager();
        initializeListener();
    }

    private void initializeListener() {
        protocolManager.addPacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
            handleUseEntityPacket(event);
        }
    }


    private void handleUseEntityPacket(PacketEvent event) {
        if (adhaEvent.getDataManager().getConfig("config.yml").get().getBoolean("use-packet") == true) {
            PacketContainer packet = event.getPacket();
            if (adhaEvent.getArmorStandName(packet.getIntegers().read(0)) == null) {
                return;
            }
            System.out.println(packet);

            Player player = event.getPlayer();
            int entityId = packet.getIntegers().read(0);
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            NBTItem itemRPG = NBTItem.get(itemInHand);

            if (adhaEvent.mmoItemsHook.isMMOItem(itemInHand)) {
                if (itemRPG.hasType() && itemRPG.getString("MMOITEMS_ITEM_ID").matches("^KARUNG(_\\d+)?$")) {

                    Integer maxHeads = itemRPG.getInteger("MAX_HEAD");
                    Location location = adhaEvent.getArmorStandLocation(entityId);

                    ItemMeta itemMeta = itemInHand.getItemMeta();
                    List<String> lore = itemMeta.getLore();
                    String[] headTypeLine = lore.get(2).split(":");
                    if (headTypeLine.length <= 1) {
                        lore.set(2, "§8Head Type: " + adhaEvent.getArmorStandName(entityId));
                    } else {
                        if (!headTypeLine[1].replaceFirst(" ", "").equalsIgnoreCase(adhaEvent.getArmorStandName(entityId))) {
                            player.sendMessage("§cKarung ini hanya dapat digunakan untuk menyimpan 1 Head saja!");
                            return;
                        }
                    }
                    int storedHeads = Integer.parseInt(lore.get(5).split(": ")[1].replaceAll("§.", ""));

                    if (storedHeads < maxHeads) {
                        storedHeads++;

                        lore.set(5, "§7Stored Head: §8" + storedHeads);
                        itemMeta.setLore(lore);
                        itemInHand.setItemMeta(itemMeta);

                        destroyPacket(player, entityId);
                        player.playSound(location, Sound.ENTITY_ARMOR_STAND_BREAK, 1.0f, 1.0f);

                    } else {
                        player.sendMessage("§cKamu telah mencapai batas maksimal karung!");
                    }

                }
            }
        }
    }

    private Entity getEntityFromId(int entityId, Player player) {
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity.getEntityId() == entityId) {
                return entity;
            }
        }
        return null;
    }



    private void destroyPacket(Player player, int entityId) {
        PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
        protocolManager.sendServerPacket(player, destroyPacket);
    }

    private void spawnParticle(Player player, Location location) {
        PacketContainer particlePacket = protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
        particlePacket.getParticles().write(0, EnumWrappers.Particle.CLOUD);
        particlePacket.getBooleans().write(0, false);
        particlePacket.getDoubles().write(0, location.getX());
        particlePacket.getDoubles().write(1, location.getY());
        particlePacket.getDoubles().write(2, location.getZ());
        particlePacket.getFloat().write(0, 0.5f); // OffsetX
        particlePacket.getFloat().write(1, 0.5f); // OffsetY
        particlePacket.getFloat().write(2, 0.5f); // OffsetZ
        particlePacket.getFloat().write(3, 0.2f); // ParticleData
        particlePacket.getIntegers().write(0, 20); // Count
        protocolManager.sendServerPacket(player, particlePacket);
    }
}
