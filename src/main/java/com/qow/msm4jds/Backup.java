package com.qow.msms4j;

import com.github.luben.zstd.ZstdOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Backup {
    public Backup(List<Path> sources, Path outputTarZst) throws IOException {
        try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(new ZstdOutputStream(new BufferedOutputStream(Files.newOutputStream(outputTarZst))))) {
            tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

            for (Path src : sources) {
                Path base = src.getParent();
                addToTar(tarOut, src, base);
            }

            tarOut.finish();
        }
    }

    private static void addToTar(TarArchiveOutputStream tarOut, Path path, Path base) throws IOException {
        String entryName = (base == null) ? path.getFileName().toString() : base.relativize(path).toString();
        entryName = entryName.replace("\\", "/");
        if (Files.isDirectory(path)) {
            TarArchiveEntry entry = new TarArchiveEntry(path.toFile(), entryName + "/");
            tarOut.putArchiveEntry(entry);
            tarOut.closeArchiveEntry();
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
                for (Path child : ds) {
                    addToTar(tarOut, child, base);
                }
            }
        } else {
            TarArchiveEntry entry = new TarArchiveEntry(path.toFile(), entryName);
            entry.setSize(Files.size(path));
            tarOut.putArchiveEntry(entry);
            Files.copy(path, tarOut);
            tarOut.closeArchiveEntry();
        }
    }
}
