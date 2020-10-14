package github.anderrasovazquez.FastTravel.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import github.anderrasovazquez.FastTravel.Main;
import github.anderrasovazquez.FastTravel.db.DBManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;


public class CommandFastTravel implements CommandExecutor {
    private Main plugin;

    public CommandFastTravel(Main plugin) {
            this.plugin = plugin;
            plugin.getCommand("fasttravel").setExecutor(this);
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;

                if (args.length == 0) {
                    listComand(player);
                } else {
                    switch (args[0]) {
                        case "add":
                            addComand(player, args);
                            break;
                        case "rm":
                            rmComand(player, args);
                            break;
                        default:
                            player.sendMessage(ChatColor.RED + "El argumento \"" + args[0] + "\" no existe!");
                    }
                }
            }
            return true;
        }

    private void addComand(Player player, String[] args) {
        DBManager dbManager = new DBManager();
        try {
            Location location = player.getLocation();
            args = Arrays.copyOfRange(args, 1, args.length);
            String location_name = String.join(" ", args);
            dbManager.add(player.getUniqueId().toString(), location_name, location.getX(), location.getY(), location.getZ());
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "Añadida localización: '" + location_name + "'.");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "No se ha podido añadir.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "" + e.getMessage());
        }
    }

    private void rmComand(Player player, String[] args) {
        DBManager dbManager = new DBManager();
        try {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "El comando 'rm' requiere un solo argumento, el 'id' de la localización.");
            } else {
                int id = Integer.parseInt(args[1]);
                if (dbManager.rm(player.getUniqueId().toString(), id)) {
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "Eliminada localización.");
                } else {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Esa localización no existe.");
                }
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "No se ha podido eliminar.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "" + e.getMessage());
        }
    }

    private void listComand(Player player) {
        player.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Tu lista de lugares guardados:");
        DBManager dbManager = new DBManager();
        try {
            String result = dbManager.list(player.getUniqueId().toString());
            if (result == null) {
                return;
            }

            JsonObject json = new JsonParser().parse(result).getAsJsonObject();
            JsonArray jsonArray = json.getAsJsonArray("list");
            for (Object obj: jsonArray ) {
                JsonObject jsonObject = (JsonObject) obj;
                player.spigot().sendMessage(
                        createMessage(
                                jsonObject.get("id").getAsInt(),
                                jsonObject.get("location_name").getAsString(),
                                player.getName(),
                                jsonObject.get("x").getAsDouble(),
                                jsonObject.get("y").getAsDouble(),
                                jsonObject.get("z").getAsDouble())
                );
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "No se ha podido mostrar tu lista de localizaciones.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "" + e.getMessage());
        }
    }

    private TextComponent createMessage(int id, String text, String playerName, double x, double y, double z) {
        String teleportText = Double.toString(x) + " " + Double.toString(y) + " " + Double.toString(z);
        TextComponent message = new TextComponent("+ " + text);
        message.setColor(ChatColor.GOLD);
        message.setBold(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + playerName + " " + teleportText));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click aquí para Teletransportarte a " + teleportText + ".\nElimínalo con: /ft rm " + Integer.toString(id))
        ));
        return message;
    }

}
