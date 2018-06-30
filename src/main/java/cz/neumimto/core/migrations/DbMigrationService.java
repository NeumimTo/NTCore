package cz.neumimto.core.migrations;

import cz.neumimto.core.ioc.Singleton;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by NeumimTo on 24.6.2018.
 */
@Singleton
public class DbMigrationService {

    private Set<String> migrationScopes = new HashSet<>();

    private List<DbMigration> migrations = new ArrayList<>();

    private Connection connection;

    //todo load from config
    private String createTableStatement = "CREATE TABLE IF NOT EXISTS nt_core_migrations ("
            + "  author VARCHAR(255),"
            + "  ID VARCHAR(255),"
            + "  executed Date"
            + ");";

    private String hasRun = "SELECT count(*) as count from nt_core_migrations WHERE ID='%ID%'";

    public void requestMigration(String id) {
        if (Sponge.getGame().getState() != GameState.CONSTRUCTION) {
            throw new RuntimeException("Not GameState.CONSTRUCTION");
        }
        migrationScopes.add(id);
    }


    public void addMigrations(URL... urls) {

    }

    public void scopeFinished(String id) {
        migrationScopes.remove(id);
        if (migrationScopes.size() == 0) {
            try {
                startMigration();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMigration() throws SQLException {

        Statement statement = connection.createStatement();
        statement.execute(createTableStatement);

        for (DbMigration migration : migrations) {
            if (!hasRun(migration)) {
                run(migration);
            }
        }
    }

    public boolean hasRun(DbMigration migration) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(hasRun.replaceAll("%ID%", migration.getId()));
        ResultSet resultSet = preparedStatement.executeQuery();
        int anInt = resultSet.getInt(0);
        return anInt == 1;
    }

    public void run(DbMigration migration) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(migration.getSql());
        preparedStatement.execute();
    }
}
