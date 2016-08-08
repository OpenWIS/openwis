package org.openwis.dataservice.util;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// WMO-FNC
// pflag_productidentifier_oflag_originator_yyyymmddhhmmss[_freeformat].type[.compression]
// pflag = T --> productidentifier = TTAAii
// pflag = A --> productidentifier = TTAAiiCCCCddhhmm[BBB]
// pflag = W or Z --> ...
// oflag = C
// originator = OOOO
// freeformat ("-",",","A-Z","a-z","0-9")
// "," only in freeformat
// "-" omit
// type (met,tif,gif,png,ps,mpg,jpg,txt,htm,bin,doc,wpd)
// compression (Z,zip,gz,bz2)
public class FileNameParser {

	private static final char DOT = '.';
	private static final char UNDERSCORE = '_';

	private static final int FILENAME_MIN_LENGTH = 28;

   private static final Logger LOG = LoggerFactory.getLogger(FileNameParser.class);

	public static WMOFNC parseFileName(final String input) throws ParseException{
		WMOFNC wmofnc = new WMOFNC();

		assertValid(input); // basic validity test: 1) != null, 2) length of name

		String fileName = input.trim();
		int beginIndex = 0;
		int endIndex = 0;

		// pflag
		endIndex = fileName.indexOf(UNDERSCORE, beginIndex);
		if (endIndex < beginIndex) throw newParseException(input, "pflag", beginIndex);
		String value = fileName.substring(beginIndex, endIndex);
		wmofnc.setPflag(value);
		beginIndex = endIndex + 1;

		// productidentifier
		endIndex = fileName.indexOf(UNDERSCORE, beginIndex);
		if (endIndex < beginIndex) throw newParseException(input, "productidentifier", beginIndex);
		value = fileName.substring(beginIndex, endIndex);
		wmofnc.setProductidentifier(value);
		beginIndex = endIndex + 1;

		if ("T".equals(wmofnc.getPflag())){
			parseProductIdentifierT(value,wmofnc,input);
		} else if ("A".equals(wmofnc.getPflag())){
			parseProductIdentifierA(value,wmofnc,input);
		}

		// oflag
		endIndex = fileName.indexOf(UNDERSCORE, beginIndex);
		value = fileName.substring(beginIndex, endIndex);
		wmofnc.setOflag(value);
		beginIndex = endIndex + 1;

		// originator
		endIndex = fileName.indexOf(UNDERSCORE, beginIndex);
		value = fileName.substring(beginIndex, endIndex);
		wmofnc.setOriginator(value);
		beginIndex = endIndex + 1;

		// date stamp
		endIndex = beginIndex + 14;
		value = fileName.substring(beginIndex, endIndex);
		wmofnc.setDateStamp(value);
		if (WMOFNC.parseDate(value) == null) throw newParseException(input, "date stamp", beginIndex);

		// freeformat
		if (fileName.charAt(endIndex) == UNDERSCORE){
			beginIndex = endIndex + 1;
			endIndex = fileName.indexOf(DOT, beginIndex);
			value = fileName.substring(beginIndex, endIndex);
			wmofnc.setFreeformat(value);
		}
		beginIndex = endIndex + 1;

		// type
		int dotAfterExtensionPosition = fileName.indexOf(DOT, beginIndex);
		if (dotAfterExtensionPosition != -1){
			endIndex = dotAfterExtensionPosition;
		} else {
			endIndex = fileName.length();
		}

		value = fileName.substring(beginIndex, endIndex);
		wmofnc.setType(value);

		// compression
		int fileNameLength = fileName.length();
		if (fileNameLength > endIndex){
			if (fileName.charAt(endIndex) != DOT) throw newParseException(input, "compression", endIndex);
			beginIndex = endIndex + 1;
			value = fileName.substring(beginIndex, fileNameLength);
			wmofnc.setCompression(value);
		}

		return wmofnc;
	}

	private static void parseProductIdentifierT(final String productIdentifier, final WMOFNC wmofnc, final String input)  throws ParseException{
		int beginIndex = 0;
		int endIndex = 2;

		// date type designator: T1T2
		int productIdentifierLength = productIdentifier.length();
		if (productIdentifierLength < endIndex) throw newParseException(input, "T1T2", beginIndex);
		wmofnc.setDateTypeDesignator(productIdentifier.substring(beginIndex, endIndex));

		// geographical type designator: A1A2
		beginIndex = endIndex;
		endIndex = beginIndex + 2;
		if (productIdentifierLength < endIndex) throw newParseException(input, "A1A2", beginIndex);
		wmofnc.setGeographicalTypeDesignator(productIdentifier.substring(beginIndex,endIndex));

		// level designator: ii
		beginIndex = endIndex;
		endIndex = beginIndex + 2;
		if (productIdentifierLength < endIndex) throw newParseException(input, "ii", beginIndex);
		wmofnc.setLevelDesignator(productIdentifier.substring(beginIndex, endIndex));
	}

	public static Bulletin parseAHL(final String ahl, final String filename){
		WMOFNC wmofnc = new WMOFNC();
		Bulletin bulletin = new Bulletin();
		try {
			parseProductIdentifierA(ahl, wmofnc, filename);
		}
		catch (ParseException e) {
         LOG.error(e.getMessage(), e);
		}
		bulletin.setDateTypeDesignator(wmofnc.getDateTypeDesignator());
		bulletin.setGeographicalTypeDesignator(wmofnc.getGeographicalTypeDesignator());
		bulletin.setLevelDesignator(wmofnc.getLevelDesignator());
		bulletin.setLocationIndicator(wmofnc.getLocationIndicator());
		bulletin.setReferenceTime(wmofnc.getReferenceTime());
		bulletin.setAmendments(wmofnc.getAmendments());
		return bulletin;
	}

	private static void parseProductIdentifierA(final String productIdentifier, final WMOFNC wmofnc, final String input)  throws ParseException{
		int beginIndex = 0;
		int endIndex = 2;
		int productIdentifierLength = productIdentifier.length();

		// date type designator: T1T2
		if (productIdentifierLength < endIndex) throw newParseException(input, "T1T2", beginIndex);
		wmofnc.setDateTypeDesignator(productIdentifier.substring(beginIndex, endIndex));

		// geographical type designator: A1A2
		beginIndex = endIndex;
		endIndex = beginIndex + 2;
		if (productIdentifierLength < endIndex) throw newParseException(input, "A1A2", beginIndex);
		wmofnc.setGeographicalTypeDesignator(productIdentifier.substring(beginIndex,endIndex));

		// level designator: ii
		beginIndex = endIndex;
		endIndex = beginIndex + 2;
		if (productIdentifierLength < endIndex) throw newParseException(input, "ii", beginIndex);
		wmofnc.setLevelDesignator(productIdentifier.substring(beginIndex, endIndex));

		// location designator: CCCC
		beginIndex = endIndex;
		endIndex = beginIndex + 4;
		if (productIdentifierLength < endIndex) throw newParseException(input, "CCCC", beginIndex);
		wmofnc.setLocationIndicator(productIdentifier.substring(beginIndex, endIndex));

		// reference time: ddhhmm
		beginIndex = endIndex;
		endIndex = beginIndex + 6;
		if (productIdentifierLength < endIndex) throw newParseException(input, "ddhhmm", beginIndex);
		wmofnc.setReferenceTime(productIdentifier.substring(beginIndex, endIndex));

		// amendments: [BBB]
		beginIndex = endIndex;
		endIndex = beginIndex + 3;
		if (endIndex <= productIdentifierLength) wmofnc.setAmendments(productIdentifier.substring(beginIndex, endIndex));
	}

	private static void assertValid(final String fileName) throws ParseException{
	      if(fileName == null) {
	          throw new ParseException("Input source argument cannot be null!", -1);
	       }
	       if("".equals(fileName.trim())) {
	          throw new ParseException("Input source argument cannot be empty", 0);
	       }
	       if(fileName.trim().length() < FILENAME_MIN_LENGTH) {
	          throw new ParseException(String.format("Input source size does not match: actual=%d less than %d",fileName.trim().length(), FILENAME_MIN_LENGTH), fileName.trim().length());
	       }
	}

	private static ParseException newParseException(final String input, final String field, final int errorOffset) {
		return new ParseException(String.format("Unable to locate mandatory filed '%s' in input: %s", field, input), errorOffset);
	}
}