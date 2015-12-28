package org.zigmoi.expncal;

import org.zigmoi.expncal.api.xl.Request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 * Created by Ramraj-HP on 29-12-2015.
 */
public class AppMain {

    public static void main(String[] args) throws FileNotFoundException {

        String dataF = null;
        if (args == null || args.length == 0) {
            System.err.println("Err. Enter the file name as argument to process.");
            System.exit(1);
        } else {
            dataF = args[0];
        }

        String basePath = new File("").getAbsolutePath();
        String paramF = basePath + File.separator + "conf/xl_param_file.config";

        String outFile =
                basePath + File.separator + "out" + File.separator
                        + new File(dataF).getName() + "_" +
                        new Date().getTime() + ".txt";

        FileOutputStream fstream3 = new FileOutputStream(outFile);
        PrintStream outPut = new PrintStream(fstream3);
        System.setOut(outPut);

        Request req = new Request(dataF, paramF);
        req.getResponse();
    }
}
