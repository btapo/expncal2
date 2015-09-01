package org.zigmoi.expncal.commons;

import java.text.ParseException;
import org.zigmoi.expncal.ext.jsonjavamaster.JSONArray;
import org.zigmoi.expncal.ext.jsonjavamaster.JSONObject;

public class JsonParser {

	private static final int DT_INT = 1;
	private static final int DT_STRING = 2;
	private static final int DT_LONG = 3;
	private static final int DT_DOUBLE = 4;
	private static final int DT_BOOLEAN = 5;

	private final String jsonStr;
	private final JSONObject jObj;

	private String[] tagArray;
	private int tagIndx = 0;

	private String tag;

	private int tagDataType = 0;

	public JsonParser(String jsonStr) {

		this.jsonStr = jsonStr;
		this.jObj = new JSONObject(this.jsonStr);
	}

	private boolean tagExists(JSONArray jArr) throws ParseException {

		for (int i = this.tagIndx; i < tagArray.length; i++) {
			if (this.isLastElement()) {
				return false;
			} else {
				++this.tagIndx;
				if (this.isLastElement()) {
					if (this.elementIsArray()) {
						return false;
					} else {
						--this.tagIndx;
						return this.tagExists((JSONObject) jArr.getJSONObject(this.getArrayElementIndex()));
					}
				} else {
					if (this.elementIsArray()) {
						return this.tagExists((JSONArray) jArr.getJSONArray(this.getArrayElementIndex()));
					} else {
						--this.tagIndx;
						return this.tagExists((JSONObject) jArr.getJSONObject(this.getArrayElementIndex()));
					}
				}
			}
		}
		return false;
	}

	private boolean tagExists(JSONObject jObjArg) throws ParseException {

		for (int i = ++this.tagIndx; i < tagArray.length; i++) {
			if (this.isLastElement()) {
				if (this.elementIsArray()) {
                    return jObjArg.getJSONArray(this.elementName()).get(this.getArrayElementIndex()) != null;
				} else {
					return jObjArg.has(tagArray[tagIndx]);
				}
			} else {
				if (this.elementIsArray()) {
					return this.tagExists((JSONArray) jObjArg.getJSONArray(this.elementName()));
				} else {
					return this.tagExists((JSONObject) jObjArg.getJSONObject(tagArray[tagIndx]));
				}
			}
		}
		return false;
	}

	public boolean tagExists(String tag) throws ParseException {

		this.tagArray = StringOp.split(tag, '/', true);

		this.tagIndx = 0;
		for (int i = this.tagIndx; i < tagArray.length; i++) {
			if (this.isLastElement()) {
				return jObj.has(tagArray[tagIndx]);
			} else {
				if (this.elementIsArray()) {
					return this.tagExists((JSONArray) jObj.getJSONArray(tagArray[tagIndx]));
				} else {
					return this.tagExists((JSONObject) jObj.getJSONObject(tagArray[tagIndx]));
				}
			}
		}
		return false;
	}

	private String getTagData(JSONArray jArr) throws ParseException {

		for (int i = this.tagIndx; i < tagArray.length; i++) {
			if (this.isLastElement()) {
				throw new ParseException("Tag not found : " + tag, this.tagIndx);
			} else {
				++this.tagIndx;
				if (this.isLastElement()) {
					if (this.elementIsArray()) {
						throw new ParseException("Tag not found : " + tag, this.tagIndx);
					} else {
						--this.tagIndx;
						return this.getTagData((JSONObject) jArr.getJSONObject(this.getArrayElementIndex()));
					}
				} else {
					if (this.elementIsArray()) {
						return this.getTagData((JSONArray) jArr.getJSONArray(this.getArrayElementIndex()));
					} else {
						--this.tagIndx;
						return this.getTagData((JSONObject) jArr.getJSONObject(this.getArrayElementIndex()));
					}
				}
			}
		}
		throw new ParseException("Tag not found : " + tag, this.tagIndx);
	}

	private String getTagData(JSONObject jObjArg) throws ParseException {

		for (int i = ++this.tagIndx; i < tagArray.length; i++) {
			if (this.isLastElement()) {
				if (this.elementIsArray()) {
                    return (String) jObjArg.getJSONArray(this.elementName()).get(this.getArrayElementIndex());
				} else {
					return this.getTagDataForLastElement(jObjArg);
				}
			} else {
				if (this.elementIsArray()) {
					return this.getTagData((JSONArray) jObjArg.getJSONArray(this.elementName()));
				} else {
					return this.getTagData((JSONObject) jObjArg.getJSONObject(tagArray[tagIndx]));
				}
			}
		}
		throw new ParseException("Tag not found : " + tag, this.tagIndx);
	}

	private String getTagData(String tag) throws ParseException {

		if(!this.tagExists(tag)) {
			throw new ParseException("Tag not found : " + tag, this.tagIndx);
		}
		
		this.tagIndx = 0;
		for (int i = this.tagIndx; i < tagArray.length; i++) {
			if (this.isLastElement()) {
				return this.getTagDataForLastElement(jObj);
			} else {
				if (this.elementIsArray()) {
					return this.getTagData((JSONArray) jObj.getJSONArray(tagArray[tagIndx]));
				} else {
					return this.getTagData((JSONObject) jObj.getJSONObject(tagArray[tagIndx]));
				}
			}
		}
		throw new ParseException("Tag not found : " + tag, this.tagIndx);
		
	}

	public boolean getTagData(String tag, boolean returnTypeIndicator) throws ParseException {

		this.tagDataType = DT_BOOLEAN;
		return Boolean.getBoolean(this.getTagData(tag));
	}

	public int getTagData(String tag, int returnTypeIndicator) throws ParseException {

		this.tagDataType = DT_INT;
		return Integer.parseInt(this.getTagData(tag));
	}
	
	public long getTagData(String tag, long returnTypeIndicator) throws ParseException {

		this.tagDataType = DT_LONG;
		return Long.parseLong(this.getTagData(tag));
	}

	public String getTagData(String tag, String returnTypeIndicator) throws ParseException {

		this.tagDataType = DT_STRING;
		return this.getTagData(tag);
	}

	public JSONObject getJSONObject() {
		return this.jObj;
	}

	private boolean elementIsArray() throws ParseException {
		return StringOp.getEnclosedString(this.tagArray[this.tagIndx], '[', ']', '\\') != null;
	}
	
	private String elementName() throws ParseException {
		if (!this.elementIsArray()) {
			throw new ParseException("Invalid Op. Tag not an array.", this.tagIndx);
		}
		return this.tagArray[this.tagIndx]
			           .substring(0, this.tagArray[this.tagIndx].indexOf('['));
	}

	private int getArrayElementIndex() throws ParseException {

		if (!this.elementIsArray()) {
			throw new ParseException("Invalid Op. Tag not an array.", this.tagIndx);
		}
		return Integer.parseInt(StringOp.getEnclosedString(this.tagArray[this.tagIndx], '[', ']', '\\')[0]);
	}

	private int getTagsLeft() {
		return this.tagArray.length - this.tagIndx;
	}

	private boolean isLastElement() {
		return this.getTagsLeft() == 1;
	}

	private String getTagDataForLastElement(JSONObject jObjArg) throws ParseException {

		if (this.elementIsArray()) {
			throw new ParseException("Last element cannot be an array.", this.tagIndx);
		}

		switch (tagDataType) {
			case DT_INT:
				return String.valueOf(jObjArg.getInt(this.tagArray[this.tagIndx]));
			case DT_STRING:
				return String.valueOf(jObjArg.getString(this.tagArray[this.tagIndx]));
			case DT_BOOLEAN:
				return String.valueOf(jObjArg.getBoolean(this.tagArray[this.tagIndx]));
			default:
				throw new ParseException("Invalid tag data type : " + this.tagDataType, this.tagIndx);
		}
	}

	public static void main(String[] args) throws ParseException {
		JsonParser jp = new JsonParser("{\"menu\": {\n" +
"  \"id\": \"file\",\n" +
"  \"value\": \"File\",\n" +
"  \"popup\": {\n" +
"    \"menuitem\": [\n" +
"      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\n" +
"      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\n" +
"      {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}\n" +
"    ]\n" +
"  }\n" +
"}}");
		System.out.println(jp.tagExists("/menu/id2"));
	}
}
