package psidev.psi.pi.validator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Class for unpacking a zipped file.
 * @author Gerhard Mayer, MPC, Ruhr-University of Bochum
 */
public class ArchiveUnpacker {
    /**
     * Constants
     */
    private static final int BUF_SIZE = 4096;

    /**
     * GunZip a file.
     * @param gzipFile      the .gzip file
     * @param unzippedFile  the unzipped file
     */
    public static void gunzipFile(String gzipFile, String unzippedFile) {
        byte[] buffer = new byte[BUF_SIZE];

        try {
            FileOutputStream out;
            try (GZIPInputStream gzipIS = new GZIPInputStream(new FileInputStream(gzipFile))) {
                out = new FileOutputStream(unzippedFile);
                int len;
                while ((len = gzipIS.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
           out.close();
        }
        catch(IOException exC) {
            exC.printStackTrace(System.err);   
        }
    }
}
