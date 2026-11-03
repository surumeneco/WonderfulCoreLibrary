package co.surumene.wcl.plugin;

import co.surumene.wcl.api.runtime.ConsumerContext;
import org.bukkit.plugin.Plugin;

/** Paper service providing a new, independently closable context for each consumer call. */
public interface WonderfulCoreLibraryService {
    /** Must be invoked on the primary thread by an enabled plugin. */
    ConsumerContext openContext(Plugin owner);
}
