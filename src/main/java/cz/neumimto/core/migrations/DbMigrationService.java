package cz.neumimto.core.migrations;

import cz.neumimto.core.PluginCore;
import cz.neumimto.core.ioc.Singleton;
import org.lwjgl.Sys;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by NeumimTo on 24.6.2018.
 */
@Singleton
public class DbMigrationService {

    private List<DbMigration> migrations = new ArrayList<>();

    private Connection connection;

    private String databaseProductName;

    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection;
        DatabaseMetaData metaData = connection.getMetaData();
        this.databaseProductName = metaData.getDatabaseProductName();
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    private void startMigration() throws SQLException, IOException {
        Statement statement = connection.createStatement();
        String s = Sponge.getAssetManager().getAsset(PluginCore.Instance, "generic/create-migration-table.sql").get().readString();
        statement.execute(s);
        Collections.sort(migrations);
        connection.setAutoCommit(false);
        try {
            for (DbMigration migration : migrations) {
                if (!hasRun(migration)) {
                    run(migration);
                }
            }
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            migrations = null;
            connection.close();
            connection = null;
        }
    }

    public void addMigration(DbMigration migration) {
        migrations.add(migration);
    }

    public void addMigration(String migration) {
        migrations.addAll(DbMigration.from(migration));
    }

    private boolean hasRun(DbMigration migration) {
        try {
            String s = Sponge.getAssetManager().getAsset(PluginCore.Instance, "generic/check.sql").get().readString();
            PreparedStatement preparedStatement = connection.prepareStatement(s.replaceAll("%s", migration.getId()));
            ResultSet resultSet = null;

            resultSet = preparedStatement.executeQuery();
            int anInt = resultSet.getInt(0);
            return anInt == 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void run(DbMigration migration) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(migration.getSql());
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
