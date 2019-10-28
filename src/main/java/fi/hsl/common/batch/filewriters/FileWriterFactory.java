package fi.hsl.common.batch.filewriters;

public class FileWriterFactory {
    public static FileWriter createFileWriterProvider() {
        return new FileWriter();
    }
}
