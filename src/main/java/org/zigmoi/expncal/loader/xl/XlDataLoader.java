package org.zigmoi.expncal.loader.xl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zigmoi.expncal.commons.HashMapOp;
import org.zigmoi.expncal.core.MPreCalculation;

public class XlDataLoader {

    File dataFile;
    FileInputStream fis;

    MParamLoader params;

    Map<Integer, MDataLoader> mDataLoaderLineNoMap = new HashMap<>();
    ArrayList<MPreCalculation> mPreCalcList = new ArrayList<>();

    int lineCount = 0, columnCount = 0;

    public XlDataLoader(File dataFile, File paramFile) throws IOException {
        this.dataFile = dataFile;
        ParamLoader paramLoader = new ParamLoader(paramFile);
        params = paramLoader.load();
    }

    public void load() throws FileNotFoundException, IOException, IllegalArgumentException {

        fis = new FileInputStream(this.dataFile);

        String[] fileNameParts = this.dataFile.getName().split("\\.");
        String fileNameExt = fileNameParts[fileNameParts.length - 1];
        switch (fileNameExt) {
            case "xlsx":
                this.loadXlsx();
                break;
            case "xls":
                this.loadXls();
                break;
            default:
                System.err.println("Err loading file. incorrect extension: "
                    + this.dataFile.getAbsolutePath());
                throw new IllegalArgumentException("Err loading file. incorrect extension: "+ this.dataFile.getAbsolutePath());
        }
    }

    private void loadXls() throws IOException, IllegalArgumentException {

        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet spreadsheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = spreadsheet.iterator();

        lineCount = 0;

        while (rowIterator.hasNext()) {
            HSSFRow row = (HSSFRow) rowIterator.next();
            Iterator< Cell> cellIterator = row.cellIterator();

            columnCount = 0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        prepareDataLoaderList(String.valueOf(cell.getNumericCellValue()));
                        break;
                    case Cell.CELL_TYPE_STRING:
                        prepareDataLoaderList(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        switch (cell.getCachedFormulaResultType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                prepareDataLoaderList(String.valueOf(cell.getNumericCellValue()));
                                break;
                            case Cell.CELL_TYPE_STRING:
                                prepareDataLoaderList(cell.getStringCellValue());
                                break;
                        }
                        break;
                }
                columnCount++;
            }
            lineCount++;
        }
        fis.close();
    }

    private void loadXlsx() throws IOException, IllegalArgumentException {

        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = spreadsheet.iterator();

        lineCount = 0;

        while (rowIterator.hasNext()) {

            XSSFRow row = (XSSFRow) rowIterator.next();
            Iterator< Cell> cellIterator = row.cellIterator();

            columnCount = 0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        prepareDataLoaderList(String.valueOf(cell.getNumericCellValue()));
                        break;
                    case Cell.CELL_TYPE_STRING:
                        prepareDataLoaderList(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        switch (cell.getCachedFormulaResultType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                prepareDataLoaderList(String.valueOf(cell.getNumericCellValue()));
                                break;
                            case Cell.CELL_TYPE_STRING:
                                prepareDataLoaderList(cell.getStringCellValue());
                                break;
                        }
                        break;
                }
                columnCount++;
            }
            lineCount++;
        }
        fis.close();
    }

    private void prepareDataLoaderList(String colData) throws IllegalArgumentException {
        
        MDataLoader mDataLoader;
        if (mDataLoaderLineNoMap.containsKey(lineCount + 1)) {
            mDataLoader = mDataLoaderLineNoMap.get(lineCount + 1);
        } else {
            mDataLoader = new MDataLoader();
            mDataLoader.setLineNo(lineCount + 1);
            mDataLoaderLineNoMap.put(lineCount + 1, mDataLoader);
        }

        if (colData == null || colData.isEmpty()) {
            return;
        }

        colData = colData.trim();
        
        if (columnCount <= params.headerOrdered.length - 1) {
            switch (params.headerOrdered[columnCount].trim().toLowerCase()) {
                case "expenditure_type":
                    mDataLoader.setExpenditureType(colData);
                    break;
                case "amount":
                    mDataLoader.setAmount(colData);
                    break;
                case "paid_by":
                    mDataLoader.setPaidBy(colData);
                    break;
                case "paid_for":
                    mDataLoader.setPaidFor(colData);
                    break;
                case "date":
                    mDataLoader.setDate(colData);
                    break;
                case "remarks":
                    mDataLoader.setRemarks(colData);
                    break;
                default:
                    System.err.println("Err invalid config header : " + params.headerOrdered[columnCount]);
                    throw new IllegalArgumentException("Err invalid config header : " + params.headerOrdered[columnCount]);
            }
        }
    }

    public ArrayList<MPreCalculation> getPreCalculationModel() throws IllegalArgumentException {

        for (Integer lineNo : mDataLoaderLineNoMap.keySet()) {
            
            MDataLoader mDataLoader = mDataLoaderLineNoMap.get(lineNo);
            if (params.ignoreFirstLine && mDataLoader.getLineNo() == 1) {
                continue;
            }
            
            validate(mDataLoader);
            
            String[] decodedPaidBy, decodedPaidFor;
            double amount;
            try {
                decodedPaidBy = decodeIndividual(mDataLoader.getPaidBy());
            } catch(IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to parse 'paid by' field on line : "
                        + mDataLoader.getLineNo() + " and exception msg : " + e.getMessage());
            } 
            try {
                decodedPaidFor = decodeIndividual(mDataLoader.getPaidFor());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to parse 'paid for' field on line : "
                        + mDataLoader.getLineNo() + " and exception msg : " + e.getMessage());
            } 
            
            try {
                amount = Double.parseDouble(mDataLoader.getAmount());
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Failed to parse 'amount' field on line : "
                        + mDataLoader.getLineNo() + " and exception msg : " + e.getMessage());
            }
            
            for (String paidBy : decodedPaidBy) {
            
                for (String paidFor : decodedPaidFor) {
                    
                    MPreCalculation mPreCalc = new MPreCalculation();
                    mPreCalc.setEntryId(String.valueOf(mDataLoader.getLineNo()));
                    mPreCalc.setExpenditureType(mDataLoader.getExpenditureType());
                    mPreCalc.setPaidBy(paidBy);
                    mPreCalc.setPaidFor(paidFor);
                    mPreCalc.setAmount(String.valueOf(
                        amount / (decodedPaidBy.length * decodedPaidFor.length))
                    );
                    mPreCalcList.add(mPreCalc);
                }
            }
        }
        return mPreCalcList;
    }

    private String[] decodeIndividual(String encodedData) throws IllegalArgumentException {
        
        String[] decodedData = null;
        
        if (params.getSpecialCodeHandleMap().containsKey(encodedData)) {
    
            if (encodedData.equalsIgnoreCase("all")) {
                if (params.getSpecialCodeHandleMap().get(encodedData).equalsIgnoreCase("handle_default")) {
                    decodedData = new String[params.getIndividualCodeMap().size()];
                    int i = 0;
                    for (String key : params.getIndividualCodeMap().keySet()) {
                        decodedData[i] = key;
                        i++;
                    }
                } else {
                    System.err.println("Err. no special handle found: " + encodedData);
                    throw new IllegalArgumentException("Err. no special handle found: " + encodedData);
                }
            } else {
                System.err.println("Err. no special handle found: " + encodedData);
                throw new IllegalArgumentException("Err. no special handle found: " + encodedData);
            }
        } else if (params.getIndividualCodeMap().containsKey(encodedData)) {
            decodedData = new String[1];
            decodedData[0] = encodedData;
        } else {
            int encodeSize = params.getIndividualEncodeSize();
            if (encodedData == null || encodedData.trim().length() == 0 || 
                    encodedData.length() % encodeSize != 0) {

                System.err.println("Err. invalid data. encode data should be multiple of encode bits.");
                throw new IllegalArgumentException("Err. invalid data. "
                        + "encode data should be multiple of encode bits. Reveived data : " + encodedData);
            } else {

                decodedData = new String[encodedData.length() / encodeSize];
                int j = 0;
                
                for (int i = 0; i < encodedData.length();) {
                    String bits = encodedData.substring(i, i + encodeSize);
                    String decodedBit = (String) HashMapOp.getKeyFromValue(params.getIndividualCodeMap(), bits);
                    if (decodedBit == null) {
                        throw new IllegalArgumentException("Err. invalid data. encode data should not decode to null. "
                                + "Wrong value received : " + encodedData);
                    }
                    decodedData[j] = decodedBit;
                    
                    i += encodeSize;
                    j++;
                }
            }
        }
        
        if (decodedData == null) {
            System.err.println("Err. failure in decoding. Data matches no parse rules.");
            throw new IllegalArgumentException("Err. failure in decoding. "
                    + "Data matches no parse rules. Received data : " + encodedData);
        }
        
        return decodedData;
    }

    private boolean validate(MDataLoader mDataLoader) throws IllegalArgumentException {
        
        if (mDataLoader.expenditureType == null || mDataLoader.expenditureType.isEmpty()) {
            throw new IllegalArgumentException("Err in line no : " + mDataLoader.getLineNo()
            + ". Invalid 'expenditure type' value: " + mDataLoader.expenditureType);
        }
        if (mDataLoader.paidBy == null || mDataLoader.paidBy.isEmpty()) {
            throw new IllegalArgumentException("Err in line no : " + mDataLoader.getLineNo()
            + ". Invalid 'paid by' value: " + mDataLoader.paidBy);
        }
        if (mDataLoader.paidFor == null || mDataLoader.paidFor.isEmpty()) {
            throw new IllegalArgumentException("Err in line no : " + mDataLoader.getLineNo()
            + ". Invalid 'paid for' value: " + mDataLoader.paidFor);
        }
        if (mDataLoader.amount == null || mDataLoader.amount.isEmpty()) {
            throw new IllegalArgumentException("Err in line no : " + mDataLoader.getLineNo()
            + ". Invalid 'amount' value: " + mDataLoader.amount);
        }
        
        return true;
    }

    void printDataLoaderList() {

        for (Integer lineNo : mDataLoaderLineNoMap.keySet()) {
        
            MDataLoader mDataLoader = mDataLoaderLineNoMap.get(lineNo);
            System.out.print(mDataLoader.lineNo);
            System.out.print(" : ");
            System.out.print(mDataLoader.expenditureType);
            System.out.print(" : ");
            System.out.print(mDataLoader.amount);
            System.out.print(" : ");
            System.out.print(mDataLoader.paidBy);
            System.out.print(" : ");
            System.out.print(mDataLoader.paidFor);
            System.out.print(" : ");
            System.out.print(mDataLoader.date);
            System.out.print(" : ");
            System.out.println(mDataLoader.remarks);
        }
    }

    public void printPreCalcModelList() {
        for (MPreCalculation mPreCalc : mPreCalcList) {
            System.out.print(mPreCalc.getEntryId());
            System.out.print(" : ");
            System.out.print(mPreCalc.getExpenditureType());
            System.out.print(" : ");
            System.out.print(mPreCalc.getAmount());
            System.out.print(" : ");
            System.out.print(mPreCalc.getPaidBy());
            System.out.print(" : ");
            System.out.print(mPreCalc.getPaidFor());
            System.out.print(" : ");
            System.out.println("");
        }
    }

    public static void main(String... args) throws IOException, IllegalArgumentException, ParseException {
//        File paramF = new File("C:\\Users\\Zigmoi-Code\\Google Drive\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\xl_param_file.config");
//        File dataF = new File("C:\\Users\\Zigmoi-Code\\Downloads\\Expenditure_May_2015.xlsx");
     
        File paramF = new File("E:\\google_drive\\chawads\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\xl_param_file.config");
        File dataF = new File("E:\\google_drive\\chawads\\ChawaDs_Zigmoi\\ChawaDs\\MyScripts\\Java\\expncal2\\data\\Expenditure_May_2015.xlsx");
        
        XlDataLoader pl = new XlDataLoader(dataF, paramF);
        pl.load();
//        pl.printDataLoaderList();
        pl.getPreCalculationModel();
        pl.printPreCalcModelList();
    }
}
