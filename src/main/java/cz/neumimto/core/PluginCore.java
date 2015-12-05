package cz.neumimto.core;

import com.google.inject.Inject;
import cz.neumimto.core.ioc.IoC;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.sql.SqlService;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by NeumimTo on 28.11.2015.
 */
@Plugin(id = "NTCORE", name = "NT-Core",version = "1.0")
public class PluginCore {

    @Inject
    public Logger logger;

    @Listener
    public void setup(GameConstructionEvent event) {
        Game game = event.getGame();
        IoC ioC = IoC.get();
        ioC.registerInterfaceImplementation(Game.class,game);
        ioC.registerInterfaceImplementation(Logger.class,logger);
    }

    @Listener
    public void setupHibernate(GamePreInitializationEvent event) {
        Path p = copyDBProperties(event.getGame());
        Properties properties = new Properties();
        try (FileInputStream stream = new FileInputStream(p.toFile())) {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EntityManager entityManager = Persistence.createEntityManagerFactory("NT-Core", properties).createEntityManager();
        IoC.get().registerInterfaceImplementation(EntityManager.class,entityManager);
        EntityManagerCreatedEvent e = new EntityManagerCreatedEvent(entityManager);
        Sponge.getEventManager().post(e);

    }

    protected Path copyDBProperties(Game game) {
        Path path = Paths.get(new File(".")+File.separator+"mods"+File.separator+"database.properties");
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("database.properties");
            try {
                Files.copy(resourceAsStream, path);
                logger.warn("File \"database.properties\" has been copied into the mods-folder, Configure it and start the server again.");
                game.getServer().shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }
}
