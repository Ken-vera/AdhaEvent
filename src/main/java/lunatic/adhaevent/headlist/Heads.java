package lunatic.adhaevent.headlist;

import lunatic.adhaevent.Main;
import org.bukkit.inventory.ItemStack;


public enum Heads {
    dragonLaser("YmU4NDU2MTU1MTQyY2JlNGU2MTM1M2ZmYmFmZjMwNGQzZDljNGJjOTI0N2ZjMjdiOTJlMzNlNmUyNjA2N2VkZCJ9fX0=", "cowHead"),
    sheepHead("ZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19", "sheepHead"),
    camelHead("NzRiOGEzMzNkZmE5MmU3ZTVhOTVhZDRhZTJkODRiMWJhZmEzM2RjMjhjMDU0OTI1Mjc3ZjYwZTc5ZGFmYzhjNCJ9fX0=", "camelHead"),
    goatHead("NDU3YTBkNTM4ZmEwOGE3YWZmZTMxMjkwMzQ2ODg2MTcyMGY5ZmEzNGU4NmQ0NGI4OWRjZWM1NjM5MjY1ZjAzIn19fQ=", "goatHead");

    private final ItemStack item;
    private final String idTag;
    private final String url;
    public String prefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";

    private Heads(String texture, String id) {
        this.item = Main.createSkull(this.prefix + texture, id);
        this.idTag = id;
        this.url = this.prefix + texture;
    }

    public String getUrl() {
        return this.url;
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public String getName() {
        return this.idTag;
    }
}
