package cz.neumimto.core;

import com.google.inject.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.migrations.DbMigrationService;
import net.minecraft.launchwrapper.Launch;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

//todo make possible more than one persistence context

/**
 * Created by NeumimTo on 28.11.2015.
 */
@Plugin(id = "nt-core", name = "NT-Core", version = "1.12")
public class PluginCore {

    protected static PluginCore Instance;

    @Inject
    public Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path config;

    private Path path;

    public static void loadJarFile(File f) {
        try {
            Launch.classLoader.addURL(f.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static URLClassLoader getClassLoader() {
        return Launch.classLoader;
    }

    @Listener
    public void setup(GameConstructionEvent event) {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        Game game = Sponge.getGame();
        IoC ioC = IoC.get();
        ioC.registerInterfaceImplementation(Game.class, game);
        ioC.registerInterfaceImplementation(Logger.class, logger);
        PluginContainer implementation = game.getPlatform().getImplementation();

        if (implementation.getName().equalsIgnoreCase("SpongeVanilla")) {
            File folder = config.getParent().toFile();
            for (File file : folder.listFiles()) {
                if (file.getName().endsWith("jar")) {
                    logger.info(file.getName() + " will be added to the classpath.");
                    loadJarFile(file);
                }
            }
        }
    }

    @Listener
    public void setupHibernate(GamePreInitializationEvent event) {
        Path p = copyDBProperties(Sponge.getGame());
        Properties properties = new Properties();
        try (FileInputStream stream = new FileInputStream(p.toFile())) {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        I dont want these to be changeable from config file, so just set them every time
         */
        properties.put(Environment.ARTIFACT_PROCESSING_ORDER, "class, hbm");
        properties.put(Environment.ENABLE_LAZY_LOAD_NO_TRANS, true);

        /*
        Dont override if setup otherwise
         */
        if (!properties.containsKey(Environment.HBM2DDL_AUTO)) {
            properties.put(Environment.HBM2DDL_AUTO, "create");
        }

        String s = (String) properties.get("hibernate.connection.url");
        if (s == null) {
            throw new RuntimeException("hibernate.connection.url is missing in database.properties file");
        }

        DbMigrationService build = IoC.get().build(DbMigrationService.class);
        try {
            Connection connection = DriverManager.getConnection(s);
            build.setConnection(connection);
            Sponge.getEventManager().post(new FindDbSchemaMigrationsEvent(this));
        } catch (SQLException e) {
            e.printStackTrace();
        }


        FindPersistenceContextEvent ev = new FindPersistenceContextEvent();
        Sponge.getEventManager().post(ev);
        Configuration configuration = new Configuration();
        configuration.addProperties(properties);
        ev.getClasses().stream().forEach(configuration::addAnnotatedClass);
        String className = properties.get("hibernate.connection.driver_class").toString();
        try {

            logger.info("Loading driver class " + className);
            getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            logger.error("====================================================");
            logger.error("Class " + className + " not found on the classpath! ");
            logger.error("Possible causes: ");
            logger.error("       - The database driver is not on the classpath");
            logger.error("       - The classname is miss spelled");
            logger.error("====================================================");
        }
        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();


        SessionFactory factory = configuration.buildSessionFactory(registry);

        IoC.get().registerInterfaceImplementation(SessionFactory.class, factory);
        SessionFactoryCreatedEvent e = new SessionFactoryCreatedEvent(factory);
        Sponge.getEventManager().post(e);
    }

    protected Path copyDBProperties(Game game) {
        Path path = Paths.get(config.getParent().toString() + File.separator + "database.properties");
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("database.properties");
            try {
                Files.copy(resourceAsStream, path);
                logger.info("File \"database.properties\" has been copied into the config/nt-core folder.");
                logger.info("\u001b[1;32mBy default H2 databse will be used");
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
