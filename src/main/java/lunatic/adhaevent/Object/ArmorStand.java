package lunatic.adhaevent.Object;

import org.bukkit.Location;

public class ArmorStand {
    private final Location location;
    private final String customName;

    public ArmorStand(Location location, String customName) {
        this.location = location;
        this.customName = customName;
    }

    public Location getLocation() {
        return location;
    }

    public String getCustomName() {
        return customName;
    }
}
