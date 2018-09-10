package cz.neumimto.core.migrations;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by NeumimTo on 24.6.2018.
 */
public class DbMigration implements Comparable<DbMigration> {

    private static DbMigration cached;
    private String id;
    private String author;
    private String note;
    private Date date;
    private String sql = "";

    public static synchronized List<DbMigration> from(String data) {
        Pattern compile = Pattern.compile("(?<=:).*");

        List<DbMigration> list = new ArrayList<>();
        Arrays.stream(data.split(System.lineSeparator()))
                .forEach(s -> {
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
                    cached.setSql(cached.sql += s);
                }
            });
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

    @Override
    public int compareTo(DbMigration o) {
        return date.after(o.date) ? 1 : -1;
    }
}
