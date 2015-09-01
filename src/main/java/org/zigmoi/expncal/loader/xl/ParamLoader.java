package org.zigmoi.expncal.loader.xl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.zigmoi.expncal.commons.FileOp;

public class ParamLoader {

    File file;

    MParamLoader params = new MParamLoader();
    HashMap<String, String> individualCodeMap = new HashMap<>();
    HashMap<String, String> specialCodeMap = new HashMap<>();

    public ParamLoader(File file) {
        this.file = file;
    }

    MParamLoader load() throws FileNotFoundException, IOException, IllegalArgumentException {

        for (String fileData : FileOp.convertToUnix(file).split("\n")) {
            String key = fileData.split(":")[0].trim().toLowerCase();
            String value = fileData.split(":")[1].trim();
            switch (key) {
                case "ignore_first_line":
                    if (value.equalsIgnoreCase("yes")) {
                        params.setIgnoreFirstLine(true);
                    } else {
                        params.setIgnoreFirstLine(false);
                    }
                    break;
                case "headers_ordered":
                    params.setHeaderOrdered(value.split(","));
                    break;
                case "individual_encode_size":
                    params.setIndividualEncodeSize(Integer.parseInt(value));
                    break;
                case "individual_code":
                    individualCodeMap.put(value.split("=")[0].trim(), value.split("=")[1].trim());
                    params.setIndividualCodeMap(individualCodeMap);
                    break;
                case "special_code":
                    specialCodeMap.put(value.split("=")[0].trim(), value.split("=")[1].trim());
                    params.setSpecialCodeHandleMap(specialCodeMap);
                    break;
                default:
                    System.err.println("Err invalid param file key: " + key);
                    throw new IllegalArgumentException("Err invalid param file key: " + key);
            }
        }
        return params;
    }

    public static void main(String... args) throws IOException {
        
//        ParamLoader pl = new ParamLoader(new File("C:\\Users\\Zigmoi-Code\\Google Drive\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\xl_param_file.config"));
        ParamLoader pl = new ParamLoader(new File("E:\\google_drive\\chawads\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\xl_param_file.config"));
        
        pl.load();
    }
}
