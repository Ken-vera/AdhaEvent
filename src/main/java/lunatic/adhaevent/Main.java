package lunatic.adhaevent;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lunatic.adhaevent.Hook.DatabaseHook;
import lunatic.adhaevent.Hook.MMOItemsHook;
import lunatic.adhaevent.Hook.ProtocolHook;
import lunatic.adhaevent.Object.ArmorStand;
import lunatic.adhaevent.commandlistener.CowSpawnerCommand;
import lunatic.adhaevent.commandlistener.ShopCommand;
import lunatic.adhaevent.eventlistener.CowListener;
import lunatic.adhaevent.eventlistener.GuiListener;
import lunatic.adhaevent.headlist.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Main extends JavaPlugin {
    private final ConcurrentMap<Integer, ArmorStand> armorStandData = new ConcurrentHashMap<>();
    public ProtocolHook protocolHook;
    public ProtocolManager protocolManager;
    public MMOItemsHook mmoItemsHook;
    public DatabaseHook databaseHook;
    public GuiListener guiListener;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ChronoCore") == null) {
            getLogger().severe("ChronoCore not found, Disabling!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("Found and hooked into ChronoCore!");
        }

        protocolManager = ProtocolLibrary.getProtocolManager();
        // Plugin startup logic
        protocolHook = new ProtocolHook(this);
        mmoItemsHook = new MMOItemsHook();
        databaseHook = new DatabaseHook();
        guiListener = new GuiListener(this);

        getServer().getPluginManager().registerEvents(new CowListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getCommand("adhacow").setExecutor(new CowSpawnerCommand(this));
        getCommand("adhacow").setTabCompleter(new CowSpawnerCommand(this));
        getCommand("adhashop").setExecutor(new ShopCommand(this));
    }

    @Override
    public void onDisable(){
    }
    public static ItemStack createSkull(String url, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
        if (url.isEmpty()) {
            return head;
        } else {
            SkullMeta headMeta = (SkullMeta)head.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), (String)null);
            profile.getProperties().put("textures", new Property("textures", url));

            try {
                assert headMeta != null;

                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException var6) {
                var6.printStackTrace();
            }

            head.setItemMeta(headMeta);
            return head;
        }
    }


    public static ItemStack getHead(String name) {
        Heads[] var1 = Heads.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Heads head = var1[var3];
            if (head.getName().equals(name)) {
                return head.getItemStack();
            }
        }

        return null;
    }

    public static String getUrl(String name, int num) {
        Heads[] var2 = Heads.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Heads head = var2[var4];
            if (head.getName().equalsIgnoreCase(name + num)) {
                return head.getUrl();
            }
        }

        return null;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public Location getArmorStandLocation(Integer entityId) {
        ArmorStand data = armorStandData.get(entityId);
        return (data != null) ? data.getLocation() : null;
    }

    public String getArmorStandName(Integer entityId) {
        ArmorStand data = armorStandData.get(entityId);
        return (data != null) ? data.getCustomName() : null;
    }

    public void storeArmorStandLocation(Integer entityId, Location location, String customName) {
        armorStandData.put(entityId, new ArmorStand(location, customName));
    }

    public MMOItemsHook getMmoItemsHook() {
        return mmoItemsHook;
    }

    public DatabaseHook getDatabaseHook() {
        return databaseHook;
    }

    public GuiListener getGuiListener() {
        return guiListener;
    }
}
