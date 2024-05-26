package lunatic.adhaevent.commandlistener;

import lunatic.adhaevent.Main;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class CowSpawnerCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public CowSpawnerCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            Cow cow = (Cow) location.getWorld().spawnEntity(location, EntityType.COW);

            // Retrieve cow's current health and max health
            double currentHealth = cow.getHealth();
            double maxHealth = cow.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            // Set the custom name with health and max health, including color codes
            String customName = String.format("§aAdha Cow §c[§e%.1f§7/§e%.1f§c]", currentHealth, maxHealth);
            cow.setCustomName(customName);
            cow.setCustomNameVisible(true); // Make the custom name always visible

            player.sendMessage("A cow has been spawned at your location!");
            return true;
        } else {
            sender.sendMessage("Only players can execute this command.");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
