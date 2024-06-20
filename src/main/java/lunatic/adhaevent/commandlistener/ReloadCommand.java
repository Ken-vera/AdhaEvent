package lunatic.adhaevent.commandlistener;

import lunatic.adhaevent.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    private final Main plugin;
    public ReloadCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (sender instanceof Player) {
            plugin.getDataManager().reloadConfig("config.yml");
            sender.sendMessage("§aReloaded config file!");
        }
        return false;
    }
}
