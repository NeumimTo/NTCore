package cz.neumimto.core;

import com.google.inject.Inject;
import cz.neumimto.core.ioc.IoC;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
//todo make possible more than one persistence context
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

        properties.put("hibernate.mapping.precedence","class ,hbm");
        FindPersistenceContextEvent ev = new FindPersistenceContextEvent();
        Sponge.getEventManager().post(ev);
        try {
            ev.getClasses().add(Class.forName("cz.neumimto.players.CharacterBase"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Configuration configuration = new Configuration();
        configuration.addProperties(properties);
        ev.getClasses().stream().forEach(configuration::addAnnotatedClass);
        ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        SessionFactory factory = configuration.buildSessionFactory(registry);
        IoC.get().registerInterfaceImplementation(SessionFactory.class,factory);
        SessionFactoryCreatedEvent e = new SessionFactoryCreatedEvent(factory);
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

    @Listener
    public void close(GameStoppedServerEvent event) {
       // IoC.get().build(SessionFactory.class).close();
    }
}
