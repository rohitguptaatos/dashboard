/**
 * 
 */
package uk.co.aegon.commons.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;

/**
 * CsvServiceImpl service which acts as a facade to opencvs
 * objects CSVReader and CSVWriter and offers a cleaner interface
 * allowing future tailoring as needed.
 * 
 * @author Simon Preston
 * 
 */
@Service("csvService")
public class CsvService  {

	public static final char TAB_SEPARATOR = '\t';
	public static final char COMMA_SEPARATOR = ',';
	public static final char SINGLE_QUOTE_FIELDS = '\'';
	public static final char DOUBLE_QUOTE_FIELDS = '"';

	public static final char DEFAULT_SEPARATOR_CHARACTER = CSVParser.DEFAULT_SEPARATOR;
	public static final char DEFAULT_QUOTE_CHARACTER = CSVParser.DEFAULT_QUOTE_CHARACTER;
	public static final char DEFAULT_ESACPE_CHARACTER = CSVParser.DEFAULT_ESCAPE_CHARACTER;
	public static final char DEFAULT_SKIP_LINES = CSVReader.DEFAULT_SKIP_LINES;
	
	/** 
	 * Imports a CSV file, string etc.. and imports it into a String[]
	 * @param reader A File or String reader which implements the Reader interface
	 * @param beanFields A string array of the column names
	 * @throws CsvException 
	 */
	public List importCsvToStringArrayList(Reader reader, String[] beanFields) throws IOException, CsvException {
		List list = importCsvToStringArrayList(reader, beanFields, DEFAULT_SEPARATOR_CHARACTER, DEFAULT_QUOTE_CHARACTER, CSVReader.DEFAULT_SKIP_LINES);
		return list;
	}
	/** 
	 * Imports a CSV file, string etc.. and imports it into a String[]
	 * @param reader A File or String reader which implements the Reader interface
	 * @param beanFields A string array of the column names
	 * @param fieldSeparatorType The character which separates columns in the CSV row
	 * @param fieldQuoteType The character used to surround columns in the CSV row
	 * @param skipLines The number of lines to skip at the top of the CSV file, i.e.e header lines
	 * @throws CsvException 
	 */
	public List<String[]> importCsvToStringArrayList(Reader reader, String[] beanFields, char fieldSeparatorType, char fieldQuoteType, int skipLines) throws IOException, CsvException {
		List<String[]> resultList = null;
		CSVReader csvReader = new CSVReader(reader);
		resultList = csvReader.readAll();
		
		csvReader.close();

		return resultList;
	
	}

	/** 
	 * Imports a CSV file, string etc.. and imports it into a JavaBean for the supplied class
	 * @param reader A File or String reader which implements the Reader interface
	 * @param beanClass The class of the Bean to map to, e.g use MyBean.class
	 * @param beanFields A String Array containing the column names to be mapped
	*/
	public List importCsvToBean(Reader reader, Class beanClass, String[] beanFields) {
		List list = importCsvToBean(reader, beanClass, beanFields, DEFAULT_SEPARATOR_CHARACTER, DEFAULT_QUOTE_CHARACTER, CSVReader.DEFAULT_SKIP_LINES);
		return list;
	}

	/** 
	 * Imports a CSV file, string etc.. and imports it into a JavaBean for the supplied class
	 * @param reader A File or String reader which implements the Reader interface
	 * @param beanClass The class of the Bean to map to, e.g use MyBean.class
	 * @param beanFields A String Array containing the column names to be mapped
	 * @param fieldSeparatorType The character which separates columns in the CSV row
	 * @param fieldQuoteType The character used to surround columns in the CSV row
	 * @param skipLines The number of lines to skip at the top of the CSV file, i.e.e header lines
	*/
	public List importCsvToBean(Reader reader, Class beanClass, String[] beanFields, char fieldSeparatorType, char fieldQuoteType, int numberOfLinesToSkip) {
		List beanList = null;
		ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
		strat.setType(beanClass);
		//String[] columns = new String[] {"name", "orderNumber", "id"}; // the fields to bind do in your JavaBean
		strat.setColumnMapping(beanFields);

		CSVReader csvReader = new CSVReader(reader);
		try {
			csvReader.skip(numberOfLinesToSkip);

			CsvToBean csv = new CsvToBean();
			csv.setMappingStrategy(strat);

			csv.setCsvReader(csvReader);
			
			// A list of the bean type passed in
			beanList = csv.parse();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beanList;
	
	}
	
	/** 
	 * Not yet implemented, not really needed if using Hibernate objects which are Beans
	*/
	public void importCsvToDatabase(Reader reader, String tableName) {
		// NOT YET IMPLEMENTED - PROBABLY GO VIA HIBERNATE Bean class
		// maybe not needed if so.
		CSVReader csvReader = new CSVReader(reader);		
	}

	/** 
	 * Exports a JavaBean to CSV, either a File or a String depending on Writer type
	 * @param writer A File or String Writer which implements the Writer interface
	 * @param beanRows An array list of the Beans which will be mapped to the CSV rows
	 */ 
	public void exportBeanToCsv(Writer writer, Class beanClass, String[] beanFields, List beanRows) {
		char fieldSeparatorType = CSVWriter.DEFAULT_SEPARATOR;
		char fieldQuoteType = CSVWriter.DEFAULT_QUOTE_CHARACTER;
		char defaultEscapeCharacter = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
		String defaultLineEnd = CSVWriter.DEFAULT_LINE_END;
		
		exportBeanToCsv(writer, beanClass, beanFields, beanRows, fieldSeparatorType, fieldQuoteType, defaultEscapeCharacter, defaultLineEnd);
	}

	/** 
	 * Exports a JavaBean to CSV, either a File or a String depending on Writer type
	 * @param writer A File or String Writer which implements the Writer interface
	 * @param beanRows An array list of the Beans which will be mapped to the CSV rows
	 * @param fieldSeparatorType The character which separates columns in the CSV row
	 * @param fieldQuoteType The character used to surround columns in the CSV row
	 */
	public void exportBeanToCsv(Writer writer, Class beanClass, String[] beanFields, List beanRows, char fieldSeparatorType, char fieldQuoteType, char defaultEscapeCharacter, String defaultLineEnd) {
		
		try {
			
			
		ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy();
	    mappingStrategy.setType(beanClass);
	    mappingStrategy.setColumnMapping(beanFields);

	    StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
	    		.withMappingStrategy(mappingStrategy)
	    		. withSeparator(fieldSeparatorType)
	    		 .withQuotechar(fieldQuoteType)
	    		  .build();
	    
		// .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)

	    beanToCsv.write(beanRows);
	    	    
	    writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Exports a ResultSet to CSV, either a File or a String depending on Writer type
	 * @param resultSet The resultSet to be mapped to CSV
	 * @param writer A File or String Writer which implements the Writer interface
	 * @param beanRows An array list of the Beans which will be mapped to the CSV rows
	 * @param includeHeaders include a header row which has the column names as a header row
	 */
	public void exportResultSetToCsv(java.sql.ResultSet resultSet,Writer writer, List beanRows,boolean includeHeaders) throws SQLException, IOException {

		char fieldSeparatorType = CSVWriter.DEFAULT_SEPARATOR;
		char fieldQuoteType = CSVWriter.DEFAULT_QUOTE_CHARACTER;
		char defaultEscapeCharacter = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
		String defaultLineEnd = CSVWriter.DEFAULT_LINE_END;
		
		CSVWriter csvWriter = new CSVWriter(writer, fieldSeparatorType, fieldQuoteType, defaultEscapeCharacter, defaultLineEnd);
		
		csvWriter.writeAll(resultSet, includeHeaders);
        try {
        	csvWriter.flush();
        } catch (IOException ioe) {
        
        }

	}
	
	/** 
	 * Exports a ResultSet to CSV, either a File or a String depending on Writer type
	 * @param resultSet The resultSet to be mapped to CSV
	 * @param writer A File or String Writer which implements the Writer interface
	 * @param beanRows An array list of the Beans which will be mapped to the CSV rows
	 * @param includeHeaders include a header row which has the column names as a header row
	 */
	public void exportResultSetToCsv(List<String[]> allLines, Writer writer) throws SQLException, IOException {
		char fieldSeparatorType = CSVWriter.DEFAULT_SEPARATOR;
		char fieldQuoteType = CSVWriter.DEFAULT_QUOTE_CHARACTER;
		char defaultEscapeCharacter = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
		String defaultLineEnd = CSVWriter.DEFAULT_LINE_END;
		
		CSVWriter csvWriter = new CSVWriter(writer, fieldSeparatorType, fieldQuoteType, defaultEscapeCharacter, defaultLineEnd);
		csvWriter.writeAll(allLines);
        try {
        	csvWriter.flush();
        } catch (IOException ioe) {
        
        }

	}
	
	
}