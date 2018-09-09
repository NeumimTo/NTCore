package cz.neumimto.core.migrations;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by NeumimTo on 24.6.2018.
 */
public class DbMigration {

    private static DbMigration cached;
    private String id;
    private String author;
    private String note;
    private Date date;
    private String sql = "";

    private DbMigration() {

    }

    public List<DbMigration> from(URL... sqlFiles) {
        List<DbMigration> list = new ArrayList<>();
        for (URL sqlFile : sqlFiles) {
            list.addAll(from(sqlFile));
        }
        list.sort(Comparator.comparing(o -> o.date));
        return list;
    }

    private List<DbMigration> from(URL fileName) {
        cached = new DbMigration();
        Pattern compile = Pattern.compile("(?<=:).*");

        List<DbMigration> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName.toURI()))) {

            stream.forEachOrdered(s -> {

                if (s.startsWith("--@author:")) {
                    Matcher matcher = compile.matcher(s);
                    matcher.find();
                    cached.author = matcher.group();
                } else if (s.startsWith("--@note:")) {
                    Matcher matcher = compile.matcher(s);
                    matcher.find();
                    cached.note = matcher.group();
                } else if (s.startsWith("--@date:")) {
                    Matcher matcher = compile.matcher(s);
                    matcher.find();
                    DateFormat df = new SimpleDateFormat("dd.MM.YYYY HH:mm");
                    try {
                        cached.date = df.parse(matcher.group());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (s.startsWith("--@id:")) {
                    if (!cached.sql.isEmpty()) {
                        list.add(cached);
                        cached = new DbMigration();
                    }
                    Matcher matcher = compile.matcher(s);
                    matcher.find();
                    cached.id = matcher.group();
                } else {
                    cached.setSql(sql += s);
                }
            });
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Could not read SQL file " + fileName);
        }
        return list;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        if (Objects.isNull(id) || Objects.isNull(date) || Objects.isNull(author)) {
            throw new RuntimeException("Invalid migration, at least one of id, date, author is missing");
        }
        this.sql = sql;
    }
}
