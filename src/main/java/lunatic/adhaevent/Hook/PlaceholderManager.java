package lunatic.adhaevent.Hook;

import com.google.common.base.Joiner;
import lunatic.adhaevent.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlaceholderManager extends PlaceholderExpansion {
    private final Main plugin;
    public PlaceholderManager(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return Joiner.on(", ").join(this.plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("point")) {
            UUID uuid = player.getUniqueId();
            int point = plugin.getDatabaseHook().getpoints(uuid);
            return String.valueOf(point);
        }
        return null;
    }
}

