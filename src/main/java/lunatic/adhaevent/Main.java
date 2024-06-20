package lunatic.adhaevent;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lunatic.adhaevent.Data.DataManager;
import lunatic.adhaevent.Hook.DatabaseHook;
import lunatic.adhaevent.Hook.MMOItemsHook;
import lunatic.adhaevent.Hook.PlaceholderManager;
import lunatic.adhaevent.Hook.ProtocolHook;
import lunatic.adhaevent.Object.ArmorStand;
import lunatic.adhaevent.commandlistener.CowSpawnerCommand;
import lunatic.adhaevent.commandlistener.ReloadCommand;
import lunatic.adhaevent.commandlistener.ShopCommand;
import lunatic.adhaevent.eventlistener.CowListener;
import lunatic.adhaevent.eventlistener.GuiListener;
import lunatic.adhaevent.headlist.Heads;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Main extends JavaPlugin {
    private DataManager dataManager;
    private final ConcurrentMap<Integer, ArmorStand> armorStandData = new ConcurrentHashMap<>();
    public ProtocolHook protocolHook;
    public ProtocolManager protocolManager;
    public MMOItemsHook mmoItemsHook;
    public DatabaseHook databaseHook;
    public GuiListener guiListener;
    private Economy economy;
    public static boolean redProtectCheck;
    private RedProtect redProtect;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ChronoCore") == null) {
            getLogger().severe("ChronoCore not found, Disabling!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("Found and hooked into ChronoCore!");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().severe("PlaceholderAPI not found, Disabling!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            new PlaceholderManager(this).register();
            getLogger().info("Found and hooked into PlaceholderAPI!");
        }

        if (!setupEconomy()) {
            getLogger().severe("Vault not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        redProtectCheck = checkRP();

        if (redProtectCheck != true) {
            getLogger().severe("RedProtect not found, Disabling!");
        } else {
//            new PlaceholderManager(this).register();
            getLogger().info("Found and hooked into RedProtect!");
        }

        dataManager = new DataManager(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        // Plugin startup logic
        protocolHook = new ProtocolHook(this);
        mmoItemsHook = new MMOItemsHook();
        databaseHook = new DatabaseHook();
        guiListener = new GuiListener(this);

        if (dataManager.getConfig("config.yml").get().getBoolean("use-packet") == true) {
            getLogger().info("Client-Side compatibility enabled!");
        } else {
            getLogger().info("Server-Side compatibility enabled!");
        }

        getServer().getPluginManager().registerEvents(new CowListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
//        getCommand("adhacow").setExecutor(new CowSpawnerCommand(this));
//        getCommand("adhacow").setTabCompleter(new CowSpawnerCommand(this));
        getCommand("adhashop").setExecutor(new ShopCommand(this));
        getCommand("adhareload").setExecutor(new ReloadCommand(this));

        saveDefaultConfig();
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

    public DataManager getDataManager() {
        return dataManager;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    private boolean checkRP(){
        Plugin pRP = Bukkit.getPluginManager().getPlugin("RedProtect");
        if (pRP != null && pRP.isEnabled()){
            return true;
        }
        return false;
    }
}
