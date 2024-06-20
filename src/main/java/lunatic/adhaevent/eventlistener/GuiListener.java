package lunatic.adhaevent.eventlistener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lunatic.adhaevent.Hook.DatabaseHook;
import lunatic.adhaevent.Hook.MMOItemsHook;
import lunatic.adhaevent.Main;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class GuiListener implements Listener {
    private final Main plugin;
    private final MMOItemsHook mmoItemsHook;
    private final DatabaseHook databaseHook;

    public GuiListener(Main plugin) {
        this.plugin = plugin;
        mmoItemsHook = plugin.getMmoItemsHook();
        databaseHook = plugin.getDatabaseHook();
    }

    public void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Head Bank");

        ItemStack karung = new ItemStack(Material.CHEST);
        ItemStack blank = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack button = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta karungMeta = karung.getItemMeta();
        ItemMeta blankMeta = blank.getItemMeta();
        ItemMeta buttonMeta = button.getItemMeta();
        karungMeta.setDisplayName("§aInsert item!");
        buttonMeta.setDisplayName("§cInvalid item!");
        List<String> karungLore = new ArrayList<>();

        karungMeta.setLore(karungLore);
        blankMeta.setCustomModelData(2200002);
        blankMeta.setDisplayName(" ");

        karung.setItemMeta(karungMeta);
        blank.setItemMeta(blankMeta);
        button.setItemMeta(buttonMeta);

        for (int i = 0; i < gui.getSize(); i++) {
            if (i != 11 && i != 15 && i != 22) {
                gui.setItem(i, blank);
            }
        }
        gui.setItem(4, karung);
        gui.setItem(22, button);

        player.openInventory(gui);
    }

    public void openGuiShop(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, "§a§lAdha Shop");

        ItemStack blank = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta blankMeta = blank.getItemMeta();
        blankMeta.setCustomModelData(2200002);
        blankMeta.setDisplayName(" ");
        blank.setItemMeta(blankMeta);

        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, blank);
        }

        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName(player.getName() + " Adha Stats");
        List<String> skullLore = new ArrayList<>();
        skullLore.add("§aYour current points: §e" + plugin.getDatabaseHook().getpoints(player.getUniqueId()));
        skullMeta.setLore(skullLore);
        playerSkull.setItemMeta(skullMeta);

        gui.setItem(4, playerSkull);

        int stockHelmet = plugin.getDataManager().getConfig("config.yml").get().getInt("helmet-stock");
        int stockChestplate = plugin.getDataManager().getConfig("config.yml").get().getInt("chestplate-stock");
        int stockLeggings = plugin.getDataManager().getConfig("config.yml").get().getInt("leggings-stock");
        int stockBoots = plugin.getDataManager().getConfig("config.yml").get().getInt("boots-stock");
        int stockSword = plugin.getDataManager().getConfig("config.yml").get().getInt("sword-stock");
        int stockIngot = plugin.getDataManager().getConfig("config.yml").get().getInt("ingot-stock");

        ItemStack helmet = createItem(Material.DIAMOND_HELMET, "§6§lSacrificial Horn Helmet", Arrays.asList("§cArmor", "", "§c§lLIMITED ITEM", "", "§b5000 §ePoints", "", "§7Stock: " + stockHelmet), 0);
        ItemStack chestplate = createItem(Material.DIAMOND_CHESTPLATE, "§6§lSacrificial Breastplate", Arrays.asList("§cArmor", "", "§c§lLIMITED ITEM", "", "§b10000 §ePoints", "", "§7Stock: " + stockChestplate), 0);
        ItemStack leggings = createItem(Material.DIAMOND_LEGGINGS, "§6§lSacrificial Trouser", Arrays.asList("§cArmor", "", "§c§lLIMITED ITEM", "", "§b7500 §ePoints", "", "§7Stock: " + stockLeggings), 0);
        ItemStack boots = createItem(Material.DIAMOND_BOOTS, "§6§lSacrificial Sabatons", Arrays.asList("§cArmor", "", "§c§lLIMITED ITEM", "", "§b4500 §ePoints", "", "§7Stock: " + stockBoots), 0);
        ItemStack sword = createItem(Material.IRON_SWORD, "§c§lAltar of Celestial Unity §8[§e✦§8]", Arrays.asList("§cKatana", "", "§c§lLIMITED ITEM", "", "§b12000 §ePoints", "", "§7Stock: " + stockSword), 0);
        ItemStack material = createItem(Material.PAPER, "§e§lSongket", Arrays.asList("§cMaterial", "", "§2§lEVENT", "", "§b600 §ePoints"), 1000);
        ItemStack materialIngot = createItem(Material.PAPER, "§6§lSacrifices Ingot", Arrays.asList("§cMaterial", "", "§2§lEVENT", "", "§b500 §ePeso"), 0);
        ItemStack materialNC = createItem(Material.PAPER, "§e§lSongket", Arrays.asList("§cMaterial", "", "§2§lEVENT", "", "§b10 §2N§fC"), 1000);
        ItemStack key = createItem(Material.TRIPWIRE_HOOK, "§c§lAdha Crates Key", Arrays.asList("§cKey", "", "§c§lLIMITED KEY", "", "§b2000 §ePoints"), 0);
        ItemStack barrier = createItem(Material.BARRIER, "§cSoon", null, 0);

        gui.setItem(19, helmet);
        gui.setItem(21, chestplate);
        gui.setItem(23, leggings);
        gui.setItem(25, boots);
        gui.setItem(29, sword);
        gui.setItem(33, key);
        gui.setItem(39, material);
        gui.setItem(31, materialIngot);
        gui.setItem(41, materialNC);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClickShop(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§a§lAdha Shop")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            UUID uuid = player.getUniqueId();
            ItemStack clickedItem = event.getCurrentItem();

            int stockHelmet = plugin.getDataManager().getConfig("config.yml").get().getInt("helmet-stock");
            int stockChestplate = plugin.getDataManager().getConfig("config.yml").get().getInt("chestplate-stock");
            int stockLeggings = plugin.getDataManager().getConfig("config.yml").get().getInt("leggings-stock");
            int stockBoots = plugin.getDataManager().getConfig("config.yml").get().getInt("boots-stock");
            int stockSword = plugin.getDataManager().getConfig("config.yml").get().getInt("sword-stock");
            int stockIngot = plugin.getDataManager().getConfig("config.yml").get().getInt("ingot-stock");

            if (clickedItem != null && clickedItem.getType() != Material.GRAY_STAINED_GLASS_PANE) {
                Material type = clickedItem.getType();
                CommandSender sender = Bukkit.getConsoleSender();

                switch (type) {
                    case DIAMOND_HELMET:
                        if (plugin.getDatabaseHook().getpoints(uuid) >= 5000) {
                            ItemStack itemStack = plugin.getMmoItemsHook().getMMOItemsItemStack(Type.ARMOR, "SACRIFICIAL_HORN");
                            if (stockHelmet > 0) {
                                if (canHoldOneItem(player)) {
                                    plugin.getDatabaseHook().subtractpoints(uuid, 5000);
                                    player.getInventory().addItem(itemStack);
                                    plugin.getDataManager().getConfig("config.yml").set("helmet-stock", stockHelmet - 1).save();
                                    player.sendMessage("§aYou bought §6§lSacrificial Horn Helmet §afor 5000 Points");
                                    openGuiShop(player);
                                } else {
                                    player.sendMessage("§cYour inventory is full!\n§7Try to empty at least 1 slot from your inventory.");
                                }
                            }
                        } else {
                            player.sendMessage("§cYou don't have enough Points to purchase!");
                        }
                        break;
                    case DIAMOND_CHESTPLATE:
                        if (plugin.getDatabaseHook().getpoints(uuid) >= 10000) {
                            ItemStack itemStack = plugin.getMmoItemsHook().getMMOItemsItemStack(Type.ARMOR, "SACRIFICIAL_BREASTPLATE");
                            if (stockChestplate > 0) {
                                if (canHoldOneItem(player)) {
                                    plugin.getDatabaseHook().subtractpoints(uuid, 10000);
                                    player.getInventory().addItem(itemStack);
                                    plugin.getDataManager().getConfig("config.yml").set("chestplate-stock", stockChestplate - 1).save();
                                    player.sendMessage("§aYou bought §6§lSacrificial Breastplate §afor 10000 Points");
                                    openGuiShop(player);
                                } else {
                                    player.sendMessage("§cYour inventory is full!\n§7Try to empty at least 1 slot from your inventory.");
                                }
                            }
                        } else {
                            player.sendMessage("§cYou don't have enough Points to purchase!");
                        }
                        break;
                    case DIAMOND_LEGGINGS:
                        if (plugin.getDatabaseHook().getpoints(uuid) >= 7500) {
                            ItemStack itemStack = plugin.getMmoItemsHook().getMMOItemsItemStack(Type.ARMOR, "SACRIFICIAL_TROUSER");
                            if (stockLeggings > 0) {
                                if (canHoldOneItem(player)) {
                                    plugin.getDatabaseHook().subtractpoints(uuid, 7500);
                                    player.getInventory().addItem(itemStack);
                                    plugin.getDataManager().getConfig("config.yml").set("leggings-stock", stockLeggings - 1).save();
                                    player.sendMessage("§aYou bought §6§lSacrificial Trouser §afor 7500 Points");
                                    openGuiShop(player);
                                } else {
                                    player.sendMessage("§cYour inventory is full!\n§7Try to empty at least 1 slot from your inventory.");
                                }
                            }
                        } else {
                            player.sendMessage("§cYou don't have enough Points to purchase!");
                        }
                        break;
                    case DIAMOND_BOOTS:
                        if (plugin.getDatabaseHook().getpoints(uuid) >= 4500) {
                            ItemStack itemStack = plugin.getMmoItemsHook().getMMOItemsItemStack(Type.ARMOR, "SACRIFICIAL_SABATONS");
                            if (stockBoots > 0) {
                                if (canHoldOneItem(player)) {
                                    plugin.getDatabaseHook().subtractpoints(uuid, 4500);
                                    player.getInventory().addItem(itemStack);
                                    plugin.getDataManager().getConfig("config.yml").set("boots-stock", stockBoots - 1).save();
                                    player.sendMessage("§aYou bought §6§lSacrificial Sabatons §afor 4500 Points");
                                    openGuiShop(player);
                                } else {
                                    player.sendMessage("§cYour inventory is full!\n§7Try to empty at least 1 slot from your inventory.");
                                }
                            }
                        } else {
                            player.sendMessage("§cYou don't have enough Points to purchase!");
                        }
                        break;
                    case IRON_SWORD:
                        if (plugin.getDatabaseHook().getpoints(uuid) >= 12000) {
                            ItemStack itemStack = plugin.getMmoItemsHook().getMMOItemsItemStack(Type.get("KATANA"), "ALTAR_OF_CELESTIAL_UNITY");
                            if (stockSword > 0) {
                                if (canHoldOneItem(player)) {
                                    plugin.getDatabaseHook().subtractpoints(uuid, 12000);
                                    player.getInventory().addItem(itemStack);
                                    plugin.getDataManager().getConfig("config.yml").set("sword-stock", stockSword - 1).save();
                                    player.sendMessage("§aYou bought §c§lAltar of Celestial Unity §8[§e✦§8] §afor 12000 Points");
                                    openGuiShop(player);
                                } else {
                                    player.sendMessage("§cYour inventory is full!\n§7Try to empty at least 1 slot from your inventory.");
                                }
                            }
                        } else {
                            player.sendMessage("§cYou don't have enough Points to purchase!");
                        }
                        break;
                    case TRIPWIRE_HOOK:
                        if (plugin.getDatabaseHook().getpoints(uuid) >= 2000) {
                            if (stockSword > 0) {
                                if (canHoldOneItem(player)) {
                                    plugin.getDatabaseHook().subtractpoints(uuid, 2000);
                                    Bukkit.dispatchCommand(sender, "crates key give " + player.getName() + " adha 1");
                                    player.sendMessage("§aYou bought §c§lAdha Crates Key §afor 2000 Points");
                                    openGuiShop(player);
                                } else {
                                    player.sendMessage("§cYour inventory is full!\n§7Try to empty at least 1 slot from your inventory.");
                                }
                            }
                        } else {
                            player.sendMessage("§cYou don't have enough Points to purchase!");
                        }
                        break;
                    case PAPER:
                        if (event.getSlot() == 39) {
                            if (plugin.getDatabaseHook().getpoints(uuid) >= 600) {
                                ItemStack itemStack = plugin.getMmoItemsHook().getMMOItemsItemStack(Type.get("MATERIAL"), "SONGKET");
                                if (canHoldOneItem(player)) {
                                    plugin.getDatabaseHook().subtractpoints(uuid, 600);
                                    player.getInventory().addItem(itemStack);
                                    player.sendMessage("§aYou bought §e§lSongket §afor 600 Points");
                                    openGuiShop(player);
                                } else {
                                    player.sendMessage("§cYour inventory is full!\n§7Try to empty at least 1 slot from your inventory.");
                                }
                            } else {
                                player.sendMessage("§cYou don't have enough Points to purchase!");
                            }
                        } else if (event.getSlot() == 31) {
                            if (plugin.getEconomy().getBalance(player) >= 500) {
                                ItemStack itemStack = plugin.getMmoItemsHook().getMMOItemsItemStack(Type.get("MATERIAL"), "SACRIFICES_INGOT");
                                if (canHoldOneItem(player)) {
                                    plugin.getEconomy().withdrawPlayer(player, 500);
                                    player.getInventory().addItem(itemStack);
//                                    plugin.getDataManager().getConfig("config.yml").set("ingot-stock", stockIngot - 1).save();
                                    player.sendMessage("§aYou bought §6§lSacrifices Ingot §afor 500 Peso");
                                    openGuiShop(player);
                                } else {
                                    player.sendMessage("§cYour inventory is full!\n§7Try to empty at least 1 slot from your inventory.");
                                }
                            } else {
                                player.sendMessage("§cYou don't have enough Balance to purchase!");
                            }
                        } else if (event.getSlot() == 41) {
                            Bukkit.dispatchCommand(player, "buy songket");
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockInteractKontol(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && clickedBlock.getType() == Material.FURNACE) {
                Player player = event.getPlayer();
                Location location = clickedBlock.getLocation();

                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                ApplicableRegionSet regions = container.get(BukkitAdapter.adapt(location.getWorld())).getApplicableRegions(BukkitAdapter.asBlockVector(location));
                boolean isInRegion = regions.getRegions().stream()
                                .anyMatch(region -> region.getId().equalsIgnoreCase("event"));

                if (isInRegion) {
                    event.setCancelled(true);
                    openGui(player);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Head Bank")) {
            if (event.getSlot() != 11 &&
                    event.getSlot() != 15 &&
                    event.getSlot() != 22 &&
                    event.getClickedInventory().getType() != InventoryType.PLAYER) {
                event.setCancelled(true);
            }
            Player player = (Player) event.getWhoClicked();
            Inventory inventory = event.getClickedInventory();
            ItemStack button = event.getCurrentItem();

            if (event.getRawSlot() == 22) {
                event.setCancelled(true);
                if (button.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                    if (inventory.getItem(15) == null || inventory.getItem(15).getType() == Material.AIR) {
                        ItemStack item = inventory.getItem(11);
                        processPoint(player, inventory, item);
                    }
                }
            }
        }
    }

//    @EventHandler
//    public void onBlockInteract(PlayerInteractEvent event) {
//        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
//            Block clickedBlock = event.getClickedBlock();
//            if (clickedBlock != null && clickedBlock.getType() == Material.REDSTONE_LAMP) {
//                Player player = event.getPlayer();
//                openGui(player);
//            } else if (clickedBlock != null && clickedBlock.getType() == Material.LECTERN) {
//                openGuiShop(event.getPlayer());
//            }
//        }
//    }

    @EventHandler
    public void onInventoryDrag(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Head Bank")) {
            if (event.getClick() == ClickType.SHIFT_LEFT && event.getAction() ==
                    InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                Inventory inventory = event.getClickedInventory();
                Player player = (Player) event.getWhoClicked();
                if (inventory != null && inventory.getType() == InventoryType.PLAYER) {
                    if (event.getRawSlot() != -999) {
                        ItemStack placedItem = event.getCurrentItem();
                        if (isValidItem(placedItem)) {
                            String tier = mmoItemsHook.getMMOItemId(placedItem);
                            String chance = null;
                            ItemMeta itemMeta = placedItem.getItemMeta();
                            List<String> lore = itemMeta.getLore();
                            if (lore.get(2).split(":").length > 1) {
                                String headType = lore.get(2).split(":")[1].replaceFirst(" ", "");
                                String head = null;
                                switch (headType) {
                                    case "Cow Head":
                                        chance = "[5 - 15]";
                                        head = "cowHead";
                                        break;
                                    case "Camel Head":
                                        chance = "[10 - 15]";
                                        head = "camelHead";
                                        break;
                                    case "Sheep Head":
                                        chance = "[5 - 15]";
                                        head = "sheepHead";
                                        break;
                                    case "Goat Head":
                                        chance = "[10 - 15]";
                                        head = "goatHead";
                                        break;
                                }
                                switch (tier) {
                                    case "KARUNG":
                                        guiUpdate(player, 4, Main.getHead(head), "§6" + headType, Arrays.asList("§7Point range " + chance));
                                        break;
                                    case "KARUNG_2":
                                        guiUpdate(player, 4, Main.getHead(head), "§6" + headType, Arrays.asList("§7Point range " + chance, " ", "§eBonus +10%"));
                                        break;
                                    case "KARUNG_3":
                                        guiUpdate(player, 4, Main.getHead(head), "§6" + headType, Arrays.asList("§7Point range " + chance, " ", "§eBonus +15%"));
                                        break;
                                }
                                guiUpdate(player, 22, new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "§aConvert points!", null);
                            } else {
                                guiUpdate(player, 22, new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "§cInvalid item!", null);
                                guiUpdate(player, 4, new ItemStack(Material.CHEST), "§aInsert item!", null);
                            }
                        }
                    }
                } else {
                    guiUpdate(player, 22, new ItemStack(Material.RED_STAINED_GLASS_PANE), "§cInvalid item!", null);
                    guiUpdate(player, 4, new ItemStack(Material.CHEST), "§aInsert item!", null);
                }
            }

            if (event.getAction() == InventoryAction.PLACE_ALL ||
                    event.getAction() == InventoryAction.PLACE_ONE ||
                    event.getAction() == InventoryAction.PLACE_SOME) {
                Player player = (Player) event.getWhoClicked();
                if (event.getRawSlot() == 11) {
                    ItemStack placedItem = event.getCursor();
                    if (placedItem != null) {
                        if (isValidItem(placedItem)) {
                            String tier = mmoItemsHook.getMMOItemId(placedItem);
                            String chance = null;
                            ItemMeta itemMeta = placedItem.getItemMeta();
                            List<String> lore = itemMeta.getLore();
                            if (lore.get(2).split(":").length > 1) {
                                String headType = lore.get(2).split(":")[1].replaceFirst(" ", "");
                                switch (headType) {
                                    case "Cow Head":
                                        chance = "[5 - 15]";
                                        break;
                                    case "Camel Head":
                                        chance = "[10 - 15]";
                                        break;
                                    case "Sheep Head":
                                        chance = "[5 - 15]";
                                        break;
                                    case "Goat Head":
                                        chance = "[10 - 15]";
                                        break;
                                }
                                switch (tier) {
                                    case "KARUNG":
                                        guiUpdate(player, 4, new ItemStack(Material.CHEST), "§6" + headType, Arrays.asList("§7Point range " + chance));
                                        break;
                                    case "KARUNG_2":
                                        guiUpdate(player, 4, new ItemStack(Material.CHEST), "§6" + headType, Arrays.asList("§7Point range " + chance, " ", "§eBonus +10%"));
                                        break;
                                    case "KARUNG_3":
                                        guiUpdate(player, 4, new ItemStack(Material.CHEST), "§6" + headType, Arrays.asList("§7Point range " + chance, " ", "§eBonus +15%"));
                                        break;
                                }
                                guiUpdate(player, 22, new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "§aConvert points!", null);
                            }
                        } else {
                            guiUpdate(player, 22, new ItemStack(Material.RED_STAINED_GLASS_PANE), "§cInvalid item!", null);
                            guiUpdate(player, 4, new ItemStack(Material.CHEST), "§aInsert item!", null);
                        }
                    }
                } else {
                    guiUpdate(player, 22, new ItemStack(Material.RED_STAINED_GLASS_PANE), "§cInvalid item!", null);
                    guiUpdate(player, 4, new ItemStack(Material.CHEST), "§aInsert item!", null);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (event.getView().getTitle().equals("Head Bank")) {
            Player player = (Player) event.getPlayer();
            Location location = player.getLocation();

            dropItemIfNotEmpty(player, inventory, 11, location);
            dropItemIfNotEmpty(player, inventory, 15, location);
        }
    }

    private void dropItemIfNotEmpty(Player player, Inventory inventory, int slot, Location location) {
        ItemStack item = inventory.getItem(slot);
        if (item != null && item.getType() != Material.AIR) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);

            if (!leftover.isEmpty()) {
                player.sendMessage("§cYour inventory is full\n§cthe items are dropped to the ground!");
                for (ItemStack leftoverItem : leftover.values()) {
                    player.getWorld().dropItem(location, leftoverItem);
                }
            }
        }
    }

    private void processPoint(Player player, Inventory inventory, ItemStack itemStack) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID uuid = player.getUniqueId();
            String playerName = player.getName();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = itemMeta.getLore();
            int heads = Integer.parseInt(lore.get(5).split(": ")[1].replaceAll("§.", ""));
            String id = mmoItemsHook.getMMOItemId(itemStack);
            String headType = lore.get(2).split(":")[1].replaceFirst(" ", "");
            for (int i = 0; i < heads; i++) {
                int points;

                switch (headType) {
                    case "Cow Head":
                        points = (int) (Math.random() * 11) + 5;
                        break;
                    case "Camel Head":
                        points = (int) (Math.random() * 6) + 10;
                        break;
                    case "Sheep Head":
                        points = (int) (Math.random() * 11) + 5;
                        break;
                    case "Goat Head":
                        points = (int) (Math.random() * 6) + 10;
                        break;
                    default:
                        points = 0;
                        break;
                }

                if (id.equals("KARUNG_2")) {
                    points += (int) (points * 0.1);
                } else if (id.equals("KARUNG_3")) {
                    points += (int) (points * 0.15);
                }
                databaseHook.addAdhaPoints(uuid, playerName, points);
            }

            ItemStack updatedItemStack = itemStack.clone(); // Clone to avoid async modification issues
            ItemMeta updatedMeta = updatedItemStack.getItemMeta();
            List<String> updatedLore = updatedMeta.getLore();
            updatedLore.set(2, "§8Head Type:");
            updatedLore.set(5, "§7Stored Head: §80");
            updatedMeta.setLore(updatedLore);
            updatedItemStack.setItemMeta(updatedMeta);

            Bukkit.getScheduler().runTask(plugin, () -> {
                inventory.setItem(15, updatedItemStack);
                inventory.setItem(11, null);
            });
            guiUpdate(player, 22, new ItemStack(Material.RED_STAINED_GLASS_PANE), "§cInvalid item!", null);
            guiUpdate(player, 4, new ItemStack(Material.CHEST), "§aInsert item!", null);
        });
    }

    public void guiUpdate(Player player, int slot, ItemStack itemStack, String itemName, List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(itemName);
        if (lore != null) {
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);

        player.getOpenInventory().setItem(slot, itemStack);
    }

    private ItemStack createItem(Material material, String displayName, List<String> lore, int customModelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(displayName);
        if (customModelData != 0) {
            meta.setCustomModelData(customModelData);
        }
        if (lore != null) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public boolean isValidItem(ItemStack itemStack) {
        if (mmoItemsHook.isMMOItem(itemStack)) {
            return mmoItemsHook.getMMOItemId(itemStack).contains("KARUNG");
        }
        return false;
    }

    public boolean canHoldOneItem(Player player) {
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return true; // Found an empty slot
            }
        }
        return false; // No empty slots found
    }
}
