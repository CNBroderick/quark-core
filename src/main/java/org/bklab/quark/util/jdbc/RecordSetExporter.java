package org.bklab.quark.util.jdbc;

import dataq.core.data.schema.Field;
import dataq.core.data.schema.Record;
import dataq.core.data.schema.Recordset;
import org.bklab.quark.service.TemporaryFileService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RecordSetExporter {

    private final List<String> columns = new ArrayList<>();
    private Path path;
    private BufferedWriter writer;
    private Consumer<Exception> exceptionConsumer = Throwable::printStackTrace;

    public RecordSetExporter(String title) {
        try {
            title = (title == null ? "backup" : title) + "-" + nowTime();
            TemporaryFileService service = TemporaryFileService.getInstance();

            this.path = service.createFileForMulti(service.createDirectoryForBackup("RecordSet"), title + ".txt").toPath();
            this.writer = Files.newBufferedWriter(
                    path,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (Exception e) {
            exceptionConsumer.accept(e);
        }
    }

    public RecordSetExporter append(Recordset recordset) {
        if (columns.isEmpty()) {
            initColumns(recordset);
        }
        try {
            for (Record record : recordset.asList()) {
                writer.write(columns.stream()
                        .map(name -> parseObject(record.getObject(name)))
                        .collect(Collectors.joining("|")));
                writer.newLine();
            }
        } catch (IOException e) {
            exceptionConsumer.accept(e);
        }
        return this;
    }

    public RecordSetExporter setExceptionConsumer(Consumer<Exception> exceptionConsumer) {
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    public File getFile() {
        try {
            writer.close();
        } catch (IOException e) {
            exceptionConsumer.accept(e);
        }
        return path.toFile();
    }

    private void initColumns(Recordset recordset) {
        try {
            writer.write("#" + Arrays.stream(recordset.getSchema().fields())
                    .map(Field::getName)
                    .peek(columns::add)
                    .collect(Collectors.joining("|"))
            );
            writer.newLine();
        } catch (Exception e) {
            exceptionConsumer.accept(e);
        }
    }

    private String parseObject(Object o) {
        if (o == null) return "";
        try {

            if (o instanceof String) {
                return (String) o;
            }

            if (o instanceof Number) {
                return "" + o.toString();
            }

            if (o instanceof ChronoLocalDate) {
                return DateTimeFormatter.ofPattern("uuuu-MM-dd").format((Temporal) o);
            }

            if (o instanceof ChronoLocalDateTime) {
                return DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").format((Temporal) o);
            }

            if (o instanceof Date) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) o);
            }
        } catch (Exception ignore) {
        }

        return String.valueOf(o);
    }

    private String nowTime() {
        return DateTimeFormatter.ofPattern("uuuuMMddHHmmss").format(LocalDateTime.now());
    }


}
