package github.anderrasovazquez.FastTravel;

import github.anderrasovazquez.FastTravel.commands.CommandFastTravel;
import github.anderrasovazquez.FastTravel.db.DBManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        new DBManager();
        new CommandFastTravel(this);
    }
}
