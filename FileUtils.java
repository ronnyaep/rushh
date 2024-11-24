package rush;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
   public static void copyFolder(File src, File dest) throws IOException {
      int length;
      if (src.isDirectory()) {
         if (!dest.exists()) {
            dest.mkdir();
         }

         String[] files = src.list();
         String[] var6 = files;
         length = files.length;

         for(int var4 = 0; var4 < length; ++var4) {
            String file = var6[var4];
            File srcFile = new File(src, file);
            File destFile = new File(dest, file);
            copyFolder(srcFile, destFile);
         }
      } else {
         InputStream in = new FileInputStream(src);
         OutputStream out = new FileOutputStream(dest);
         byte[] buffer = new byte[1024];

         while((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
         }

         in.close();
         out.close();
      }

   }

   public static boolean delete(File path) {
      if (path.exists()) {
         File[] files = path.listFiles();
         File[] var5 = files;
         int var4 = files.length;

         for(int var3 = 0; var3 < var4; ++var3) {
            File file = var5[var3];
            if (file.isDirectory()) {
               delete(file);
            } else {
               file.delete();
            }
         }
      }

      return path.delete();
   }
}
