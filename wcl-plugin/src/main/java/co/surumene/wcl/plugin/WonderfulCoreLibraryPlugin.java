package co.surumene.wcl.plugin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/** No config, timer, world scan, or game-side initialization. */
public final class WonderfulCoreLibraryPlugin extends JavaPlugin implements Listener {
    private DefaultWonderfulCoreLibraryService service;

    @Override public void onEnable() {
        service = new DefaultWonderfulCoreLibraryService();
        Bukkit.getServicesManager().register(
                WonderfulCoreLibraryService.class, service, this, ServicePriority.Normal);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override public void onDisable() {
        Bukkit.getServicesManager().unregisterAll(this);
        if (service != null) service.shutdown();
    }

    @EventHandler public void onConsumerDisable(PluginDisableEvent event) {
        if (service != null && event.getPlugin() != this) service.closeOwner(event.getPlugin());
    }
}
