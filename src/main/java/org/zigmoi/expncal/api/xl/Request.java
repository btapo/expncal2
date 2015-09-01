package org.zigmoi.expncal.api.xl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import org.zigmoi.expncal.core.Amount;
import org.zigmoi.expncal.core.Calculate;
import org.zigmoi.expncal.core.DataIdKeeper;
import org.zigmoi.expncal.core.MCalculation;
import org.zigmoi.expncal.core.MPreCalculation;
import org.zigmoi.expncal.loader.xl.XlDataLoader;

public class Request {

    String dataFileName;
    String paramFileName;

    boolean error = false;

    public Request(String dataFileName, String paramFileName) {
        this.dataFileName = dataFileName;
        this.paramFileName = paramFileName;
    }

    public Response getResponse() {
        
        try {
            XlDataLoader dataLoader = new XlDataLoader(new File(dataFileName), new File(paramFileName));
            dataLoader.load();
            ArrayList<MPreCalculation> mPreCalcList = dataLoader.getPreCalculationModel();
            dataLoader.printPreCalcModelList();
            ArrayList<MCalculation> mCalcList = convertPreCalcToCalcModel(mPreCalcList);

            Calculate calc = new Calculate(mCalcList);
            calc.process();
            System.out.println();
            System.out.println("============= General Expenditure report ===============");
            calc.printExpenditureReport();
            System.out.println();
            System.out.println("============= Expenditure item wise summary report ===============");
            calc.printExpenditureItemWiseSummaryReport();
            System.out.println();
            System.out.println("============= Who pays whom report ===============");
            calc.printIndividualLinkReport();
            System.out.println();
            System.out.println("============= Individual balance report ===============");
            calc.printBalanceReport();
            System.out.println();
            System.out.println("============= Summarised transfer report ===============");
            calc.printSummarisedTransferReport();
            
        } catch (Exception e) {
            error = true;
            System.err.println("Err : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasError() {
        return error;
    }
    
    private ArrayList<MCalculation> convertPreCalcToCalcModel(ArrayList<MPreCalculation> mPreCalcList) {

        ArrayList<MCalculation> mCalcList = new ArrayList<>();

        for (MPreCalculation mPreCalc : mPreCalcList) {
        
            MCalculation mCalc = new MCalculation();
            mCalc.setEntryId(Integer.parseInt(mPreCalc.getEntryId()));
            mCalc.setExpenditureTypeId(DataIdKeeper.getExpenditureId(mPreCalc.getExpenditureType()));
            mCalc.setPaidById(DataIdKeeper.getIndividualId(mPreCalc.getPaidBy()));
            mCalc.setPaidForId(DataIdKeeper.getIndividualId(mPreCalc.getPaidFor()));
            mCalc.setAmount(new Amount(mPreCalc.getAmount()));

            mCalcList.add(mCalc);
        }
        return mCalcList;
    }

    public static void main(String... args) throws FileNotFoundException, ParseException {
//        Request req = new Request("C:\\Users\\Zigmoi-Code\\Downloads\\Expenditure_May_2015.xlsx", "C:\\Users\\Zigmoi-Code\\Google Drive\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\xl_param_file.config");
//        Request req = new Request("C:\\Users\\Zigmoi-Code\\Google Drive\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\Expenditure_June_2015.xlsx", "C:\\Users\\Zigmoi-Code\\Google Drive\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\xl_param_file.config");
      
//        String paramF = "E:\\google_drive\\chawads\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\xl_param_file.config";
//        String dataF = "E:\\google_drive\\chawads\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\1_Expenditure_June_2015.xls";
//        Request req = new Request(dataF, paramF);
//        req.getResponse();
        
        String dataF = null;
        if (args == null || args.length == 0) {
//            dataF = "E:\\google_drive\\chawads\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\1_Expenditure_July_august_2015.xls";
//            dataF = "E:\\google_drive\\chawads\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\1_Expenditure_June_2015.xls";
            System.err.println("Err. Enter the file name as argument to process.");
            System.exit(1);
        } else {
           dataF = args[0];
        }
        
        String basePath = new File("").getAbsolutePath();
        String paramF = basePath + File.separator + "xl_param_file.config";
        
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