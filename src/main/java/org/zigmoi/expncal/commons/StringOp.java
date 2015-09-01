package org.zigmoi.expncal.commons;

import com.google.gson.Gson;
import org.zigmoi.expncal.ext.jsonjavamaster.JSONML;
import static java.lang.Integer.MIN_VALUE;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.zigmoi.expncal.exceptions.BoundaryConditionViolationException;

public class StringOp {

    private final static String symbols = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123"
        + "456789`~!@#$%^&*()_+-=|;:,<.>?*";
    public static final char DEF_ESCAPE_CHAR = '\\';
    private static final char DEF_ENCLOSE_BEGIN_CHAR = '<';
    private static final char DEF_ENCLOSE_END_CHAR = '>';
    private static final String DEF_IGNORE_CHARS = "~`!@#$%^&*()_+-=[]{}|:;\"',<.>/?";

    static Random rnd = new Random();

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean stringsMatch(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 != null) {
            return str1.equals(str2);
        }
        return str2.equals(str1);
    }

    public static String[] split(String str, char delimiter) {
        return split(str, delimiter, false);
    }

    public static String[] split(String str, String delimiter) {
        return str.split(delimiter);
    }

    public static String[] split(String str, char delimiter, boolean removeEmpty) {
        final int len = (str == null) ? 0 : str.length();
        if (len == 0) {
            return new String[0];
        }

        final List<String> result = new ArrayList<>();
        String elem = null;
        int i = 0, j = 0;
        while (j != -1 && j < len) {
            j = str.indexOf(delimiter, i);
            elem = (j != -1) ? str.substring(i, j) : str.substring(i);
            i = j + 1;
            if (!removeEmpty || !(elem == null || elem.length() == 0)) {
                result.add(elem);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public static String join(String[] parts, String delim) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            result.append(part);
            if (delim != null && i < parts.length - 1) {
                result.append(delim);
            }
        }
        return result.toString();
    }

    public static String chop(String aString) {
        if (aString == null) {
            return null;
        }
        if (aString.length() == 0) {
            return "";
        }
        if (aString.length() == 1) {
            return "";
        }
        return aString.substring(0, aString.length() - 1);
    }

    public static String replaceMultiple(String str, Map<String, String> replaceMap) {
        for (String toBeReplaced : replaceMap.keySet()) {
            String replacedWith = replaceMap.get(toBeReplaced);
            str = str.replaceAll(String.valueOf(toBeReplaced), String.valueOf(replacedWith));
        }
        return str;
    }

    public static String getRandomString(int len, String ignoreChars) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(symbols.charAt(rnd.nextInt(symbols.length())));
            if (ignoreChars.indexOf(sb.charAt((sb.length() - 1))) >= 0) {
                sb.deleteCharAt(sb.length() - 1);
                i--;
            }
        }
        return sb.toString();
    }

    public static String getRandomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(symbols.charAt(rnd.nextInt(symbols.length())));
        }
        return sb.toString();
    }

    public static synchronized String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static synchronized String getUniqueId(String seqId) {

        return DateTimeOp.getSysDateCCYYMMDD() + "-"
            + DateTimeOp.getSysTimeHHMMSSMS() + "-"
            + SequenceOp.getNextSequence(seqId);
    }

    public static synchronized String getTimeAsId() {

        return String.valueOf((new Date()).getTime())
            + StringOp.leftPad(
                String.valueOf(
                    SequenceOp.getNextSequence(Constants.SEQ_TIME_ID)
                ), 3, "0");
    }

    public static synchronized String getDataId(String seqId) {

        return String.valueOf((new Date()).getTime())
            + StringOp.leftPad(
                String.valueOf(
                    SequenceOp.getNextSequence(seqId)
                ), 3, "0");
    }

    public static String reverse(String data) {
        return new StringBuilder(data).reverse().toString();
    }

    public static String leftPad(String data, int size, String padString) {
        return StringUtils.leftPad(data, size, padString);
    }

    public static String rightPad(String data, int size, String padString) {
        return StringUtils.rightPad(data, size, padString);
    }

    public static String trimLeading(String data, String trimString) {
        return StringUtils.stripStart(data, trimString);
    }

    public static Map<String, String> getEnclosedStringAndReplaceables(String string)
        throws ParseException {

        return getEnclosedStringAndReplaceables(string, StringOp.DEF_ENCLOSE_BEGIN_CHAR, StringOp.DEF_ENCLOSE_END_CHAR, StringOp.DEF_ESCAPE_CHAR, true, true);
    }

    public static Map<String, String> getEnclosedStringAndReplaceables(String string,
        int encloseBeginChar, int encloseEndChar, int escapeChar, boolean replaceEscapeChar, boolean duplicateKeyCheck) throws ParseException {

        Map<String, String> replaceables = new HashMap<>();
        if (getEnclosedString(string, encloseBeginChar, encloseEndChar, escapeChar) != null) {
            for (String str : getEnclosedString(string, encloseBeginChar, encloseEndChar,
                escapeChar)) {
                char charEncloseBeginChar = (char) encloseBeginChar;
                char charEncloseEndChar = (char) encloseEndChar;
                String val;
                if (replaceEscapeChar) {
                    val = StringOp.replaceEscapeChar(charEncloseBeginChar + str + charEncloseEndChar, escapeChar);
                } else {
                    val = charEncloseBeginChar + str + charEncloseEndChar;
                }
                if (duplicateKeyCheck && replaceables.containsKey(str)) {
                    throw new ParseException("Duplicate values in enclosed string.", 0);
                }
                replaceables.put(str, val);
            }
        }
        return replaceables;
    }

    public static String replaceEscapeChar(String string) {

        return replaceEscapeChar(string, StringOp.DEF_ESCAPE_CHAR);
    }

    public static String replaceEscapeChar(String string, int escapeChar) {
        StringBuilder sb = new StringBuilder(string.length());

        int prevChar = MIN_VALUE;

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == escapeChar) {
                if (prevChar == escapeChar) {
                    sb.append(string.charAt(i));
                }
            } else {
                sb.append(string.charAt(i));
            }
            prevChar = string.charAt(i);
        }

        return sb.toString();
    }

    public static String[] getEnclosedString(String string)
        throws ParseException {

        return getEnclosedString(string, StringOp.DEF_ENCLOSE_BEGIN_CHAR, StringOp.DEF_ENCLOSE_END_CHAR, StringOp.DEF_ESCAPE_CHAR);
    }

    public static String[] getEnclosedString(String string, int encloseBeginChar, int encloseEndChar,
        int escapeChar) throws ParseException {

        if (string == null) {
            throw new ParseException("String is null.", 0);
        }

        String[] list = null;

        int requiredEncloseCharCount = 0;

        int[] posStart = new int[string.length()], posEnd = new int[string.length()];
        int indxPosStart = 0, indxPosEnd = 0;

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == encloseBeginChar) {
                if (i != 0) {
                    if ((string.charAt(i - 1)) != escapeChar) {
                        posStart[indxPosStart++] = i;
                        requiredEncloseCharCount++;
                    }
                } else {
                    posStart[indxPosStart++] = i;
                    requiredEncloseCharCount++;
                }
            }
            if (string.charAt(i) == encloseEndChar) {
                if (i != 0) {
                    if (string.charAt(i - 1) != escapeChar) {
                        posEnd[indxPosEnd++] = i;
                        requiredEncloseCharCount--;
                    }
                } else {
                    posEnd[indxPosEnd++] = i;
                    requiredEncloseCharCount--;
                }
            }
            if (requiredEncloseCharCount < 0 || requiredEncloseCharCount > 1) {
                throw new ParseException(string, i);
            }
        }

        if (indxPosStart != indxPosEnd) {
            throw new ParseException(string, indxPosEnd);
        } else if (indxPosStart > 0) {
            list = new String[indxPosStart];
            for (int i = 0; i < indxPosStart; i++) {
                int startIndx = posStart[i];
                int endIndx = posEnd[i];
                list[i] = string.substring(startIndx + 1, endIndx);
            }
        }

        return list;
    }

    public static String getIdSeparator() {
        return ".";
    }

    public static class Formatter {

        public static String jsonToXml(String reqStr) {
            return JSONML.toString(JSONML.toJSONObject(reqStr));
        }

        public static String objToJson(Object obj) {
            return new Gson().toJson(obj);
        }
    }

    public static class Appender {

        private final static int DEF_DELIM_SIZE = 10;
        private final static int MAX_DELIM_SIZE = 17;
        private final static int MIN_DELIM_SIZE = 1;

        private final static String DEF_DELIM = "\t";
        private final static int DEF_DELIM_SIZE_IND = 3;

        private final ArrayList<String> strAl = new ArrayList<>();

        private String delimiter = DEF_DELIM;
        private int delimSize = DEF_DELIM_SIZE;

        private String strToBeDecapitated = null;

        public Appender() {
            this.init();
        }

        public Appender(int delimSize) {

            if (delimSize < MAX_DELIM_SIZE && delimSize > MIN_DELIM_SIZE) {
                this.delimSize = delimSize;
                this.init();
            } else {
                throw new BoundaryConditionViolationException("Delim size exceeds limit : " + delimSize);
            }
        }

        public Appender(String strToBeDecapitated) {
            this.strToBeDecapitated = strToBeDecapitated;
        }

        private void init() {

            this.delimiter = this.generateDelim();
            strAl.add(StringOp.leftPad(String.valueOf(delimiter.length()), DEF_DELIM_SIZE_IND, "0"));
            strAl.add(delimiter);
        }

        private String generateDelim() {
            return StringOp.getRandomString(this.delimSize, StringOp.DEF_IGNORE_CHARS);
        }

        public Appender addWithDelim(String str) {

            while (true) {
                if (str != null && !str.contains(this.delimiter)) {
                    strAl.add(str);
                    strAl.add(delimiter);
                    break;
                } else {
                    this.delimiter = this.generateDelim();
                    strAl.add(1, this.delimiter);
                }
            }
            return this;
        }

        public String capitate() {
            return StringOp.listToString(strAl);
        }

        public ArrayList<String> decapitate() {

            String[] strArr;
            ArrayList<String> alStr = new ArrayList<>();

            if (this.strToBeDecapitated == null) {
                throw new NullPointerException("String to be decapitated cannot be null");
            } else {
                int delimSz = Integer.parseInt(
                    this.strToBeDecapitated.substring(0, DEF_DELIM_SIZE_IND)
                );
                String delim = this.strToBeDecapitated.substring(
                    DEF_DELIM_SIZE_IND,
                    delimSz + DEF_DELIM_SIZE_IND
                );
                strArr = StringOp.split(
                    this.strToBeDecapitated.substring(DEF_DELIM_SIZE_IND),
                    delim
                );
            }
            for (int i = 0; i < strArr.length; i++) {

                String string = strArr[i];
                if (i > 0) {
                    alStr.add(string);
                }
            }
            return alStr;
        }
    }

    public static String listToString(ArrayList<String> sls) {

        StringBuilder sb = new StringBuilder();
        for (String s : sls) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws ParseException {
        //throws null pointer exception indicating null string
//		for(String string : StringOp.getEnclosedString("a{asas}{df\\}dsfbdf}", '{', '}', '\\')) {
//		    System.out.println(string);
//		}

//		for(Map.Entry<String, String> entry : StringOp.getEnclosedStringAndReplaceables("a{asas}{df--}ds{fbdf}", '{', '}', '\\', true, true).entrySet()) {
//			System.out.println(entry.getKey() + " ::: " + entry.getValue());
//			System.out.println(entry.getKey() + " ::: " + StringOp.replaceEscapeChar(entry.getValue(), StringOp.DEF_ESCAPE_CHAR));
//		}
//		System.out.println("a{asas}{dfb\\}dsfbdf}".replaceAll("\\\\}", "a"));
        //replace all \escapeChar\Beginchar and \escapeChar\EndChar combination with the actual chars
        System.out.println(StringOp.getTimeAsId());
        System.out.println(StringOp.getDataId("fh"));

        String cap = new StringOp.Appender()
            .addWithDelim("aaaaa")
            .addWithDelim("bbbbb")
            .capitate();
        System.out.println(cap);

        System.out.println("===================");
        for (String str : new StringOp.Appender(cap).decapitate()) {
            System.out.println(str);
        }
    }
}
