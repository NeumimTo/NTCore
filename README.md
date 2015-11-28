# NTCore
Hibernate &amp; more for sponge.

database.properties
Following properties are for PostgreSQL,
each database requires its unique dialect and datasource

    ### Hibernate setup
    hibernate.dialect = org.hibernate.dialect.PostgreSQL9Dialect
    hibernate.hbm2ddl.auto = create-tables
    hibernate.show_sql = false

    ### Hikari Connection pool setup
    hibernate.connection.provider_class = com.zaxxer.hikari.hibernate.HikariConnectionProvider
    hibernate.hikari.minimumIdle = 5
    hibernate.hikari.maximumPoolSize = 10
    hibernate.hikari.idleTimeout = 30000

    ### PostgreSQL datasource *
    hibernate.hikari.dataSourceClassName = org.postgresql.ds.PGSimpleDataSource

    hibernate.hikari.username = usr
    hibernate.hikari.password = pass

    ### PGSimpleDataSource *
    hibernate.hikari.dataSource.serverName=localhost
    hibernate.hikari.dataSource.portNumber=5432
    hibernate.hikari.dataSource.databaseName=test

* - Property keys will be different for other databases

Validating:
If you would like to check that connection pool has been setup successfully simply check active connection on your database
(You may not have a permission to execute this query,it depends on your database hosting)

For postgresql:

    select pid,client_addr from pg_stat_activity where datname = 'test'

    6000;"127.0.0.1"
    1360;"127.0.0.1"
    5548;"127.0.0.1"
    3576;"127.0.0.1"
    7380;"127.0.0.1"
    7284;"::1"
    3408;"::1"

In this example all connections from localhost are connections provided by hikari.


