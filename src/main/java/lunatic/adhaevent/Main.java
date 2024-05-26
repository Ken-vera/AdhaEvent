package lunatic.adhaevent;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lunatic.adhaevent.commandlistener.CowSpawnerCommand;
import lunatic.adhaevent.eventlistener.CowListener;
import lunatic.adhaevent.headlist.Heads;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {
    public List<ArmorStand> armorStandList;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new CowListener(this), this);
        getCommand("adhacow").setExecutor(new CowSpawnerCommand(this));
        getCommand("adhacow").setTabCompleter(new CowSpawnerCommand(this));

        armorStandList = new ArrayList<>();

    }

    @Override
    public void onDisable(){
        for (ArmorStand armorStand : armorStandList) {
            armorStand.remove();
        }
        armorStandList.clear();
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
}
