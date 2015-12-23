# NTCore
Hibernate &amp; more for sponge.

[Hibernate setup](https://github.com/NeumimTo/NT-RPG/wiki/Setting-up-a-database)

Validating:
If you would like to check that connection pool has been setup successfully simply check active connection on your database
(You may not have permissions to execute this query,it depends on your database hosting)

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

How to get an instance of SessionFactory:

    SessionFactory s = Ioc.get().build(SessionFactory.class);

Todo:

    - Add second level cache for Hibernate

