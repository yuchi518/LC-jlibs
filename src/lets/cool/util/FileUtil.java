/*
 * LC-jlibs, lets.cool java libraries
 * Copyright (C) 2015-2018 Yuchi Chen (yuchi518@gmail.com)

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation. For the terms of this
 * license, see <http://www.gnu.org/licenses>.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package lets.cool.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    public static void pack(String sourceDirPath, String zipFilePath, Boolean keepFirstDir) throws IOException {

        Path p = Files.createFile(Paths.get(zipFilePath));

        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {

            Path pp = Paths.get(sourceDirPath);
            Path rpp = (keepFirstDir && pp.toFile().isDirectory())?pp.getParent():pp;

            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(rpp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
    }

    public static void delete(String deletePath) throws IOException {
        Files.walk(Paths.get(deletePath))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public static long size(String path) throws IOException {
        return Files.walk(Paths.get(path))
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();
    }

    public static String human_size(String path) throws IOException {
        long size = size(path);
        return humanReadableByteCount(size, false);
        /*if (size < 1024)
            return String.format("%dB", size);
        else if (size < 1024*1024)
            return String.format("%5.1fKB", 10*size/1024/10.0);
        else if (size < 1024*1024*1024)
            return String.format("%5.1fMB", 10*size/1024/1024/10.0);
        else
            return String.format("%5.1fGB", 10*size/1024/1024/1024/10.0);*/
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
