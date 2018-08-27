/*
 * Copyright 2010 James Pether Sörling
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *	$Id$
 *  $HeadURL$
*/
package com.hack23.cia.service.external.esv.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.Charsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.client.fluent.Request;
import org.springframework.stereotype.Component;

import com.hack23.cia.service.external.esv.api.GovernmentBodyAnnualOutcomeSummary;

/**
 * The Class EsvGovernmentBodyOperationOutcomeReaderImpl.
 */
@Component
final class EsvGovernmentBodyOperationOutcomeReaderImpl implements EsvGovernmentBodyOperationOutcomeReader {

	/** The Constant ORGANISATIONSNUMMER. */
	private static final String ORGANISATIONSNUMMER = "Organisationsnummer";

	/** The Constant MYNDIGHET. */
	private static final String MYNDIGHET = "Myndighet";

	/** The Constant ÅR. */
	private static final String YEAR = "År";

	/** The Constant UTFALL_DECEMBER. */
	private static final String UTFALL_DECEMBER = "Utfall december";

	/** The Constant UTFALL_NOVEMBER. */
	private static final String UTFALL_NOVEMBER = "Utfall november";

	/** The Constant UTFALL_OKTOBER. */
	private static final String UTFALL_OKTOBER = "Utfall oktober";

	/** The Constant UTFALL_SEPTEMBER. */
	private static final String UTFALL_SEPTEMBER = "Utfall september";

	/** The Constant UTFALL_AUGUSTI. */
	private static final String UTFALL_AUGUSTI = "Utfall augusti";

	/** The Constant UTFALL_JULI. */
	private static final String UTFALL_JULI = "Utfall juli";

	/** The Constant UTFALL_JUNI. */
	private static final String UTFALL_JUNI = "Utfall juni";

	/** The Constant UTFALL_MAJ. */
	private static final String UTFALL_MAJ = "Utfall maj";

	/** The Constant UTFALL_APRIL. */
	private static final String UTFALL_APRIL = "Utfall april";

	/** The Constant UTFALL_MARS. */
	private static final String UTFALL_MARS = "Utfall mars";

	/** The Constant UTFALL_FEBRUARI. */
	private static final String UTFALL_FEBRUARI = "Utfall februari";

	/** The Constant UTFALL_JANUARI. */
	private static final String UTFALL_JANUARI = "Utfall januari";

	/** The Constant SPECIFIC_OUTGOING_FIELDS. */
	private static final String[] SPECIFIC_OUTGOING_FIELDS = new String[] { "Inkomsttyp", "Inkomsttypsnamn", "Inkomsthuvudgrupp", "Inkomsthuvudgruppsnamn", "Inkomsttitelgrupp", "Inkomsttitelgruppsnamn", "Inkomsttitel", "Inkomsttitelsnamn", "Inkomstundertitel", "Inkomstundertitelsnamn"};

	/** The Constant SPECIFIC_INCOMING_FIELDS. */
	private static final String[] SPECIFIC_INCOMING_FIELDS = new String[] { "Utgiftsområde", "Utgiftsområdesnamn", "Anslag", "Anslagsnamn", "Anslagspost", "Anslagspostsnamn", "Anslagsdelpost", "Anslagsdelpostsnamn"};

	private List<GovernmentBodyAnnualOutcomeSummary> incomeCsvValues;

	private List<GovernmentBodyAnnualOutcomeSummary> outGoingCsvValues;

	/**
	 * Instantiates a new esv government body operation outcome reader impl.
	 */
	public EsvGovernmentBodyOperationOutcomeReaderImpl() {
		super();
	}

	@Override
	public synchronized List<GovernmentBodyAnnualOutcomeSummary> readIncomeCsv() throws IOException {
		if (incomeCsvValues == null) {
			incomeCsvValues = readUsingZipInputStream(Request.Get(
				"https://www.esv.se/psidata/manadsutfall/GetFile/?documentType=Inkomst&fileType=Zip&fileName=M%C3%A5nadsutfall%20inkomster%20januari%202006%20-%20juni%202018,%20definitivt.zip&year=2018&month=6&status=Definitiv")
				.execute().returnContent().asStream(),SPECIFIC_OUTGOING_FIELDS);
		}
		return Collections.unmodifiableList(incomeCsvValues);
	}
	
	@Override
	public synchronized List<GovernmentBodyAnnualOutcomeSummary> readOutgoingCsv() throws IOException {		
		if (outGoingCsvValues == null) {
			outGoingCsvValues = readUsingZipInputStream(Request.Get(
				"https://www.esv.se/psidata/manadsutfall/GetFile/?documentType=Utgift&fileType=Zip&fileName=M%C3%A5nadsutfall%20utgifter%20januari%202006%20-%20juni%202018,%20definitivt.zip&year=2018&month=6&status=Definitiv")
				.execute().returnContent().asStream(),SPECIFIC_INCOMING_FIELDS);
		}
		return Collections.unmodifiableList(outGoingCsvValues);
	}

	/**
	 * Read using zip input stream.
	 *
	 * @param inputStream
	 *            the input stream
	 * @param specificFields
	 *            the specific fields
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static List<GovernmentBodyAnnualOutcomeSummary> readUsingZipInputStream(final InputStream inputStream,final String[] specificFields) throws IOException {		
		final BufferedInputStream bis = new BufferedInputStream(inputStream);
		final ZipInputStream is = new ZipInputStream(bis);

		final List<GovernmentBodyAnnualOutcomeSummary> list = new ArrayList<>();
		try {
			ZipEntry entry;
			while ((entry = is.getNextEntry()) != null) {
				list.addAll(readCsvContent(is,specificFields));
			}
		} finally {
			is.close();
		}
		return list;
	}

	/**
	 * Read csv content.
	 *
	 * @param is
	 *            the is
	 * @param specificFields
	 *            the specific fields
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static List<GovernmentBodyAnnualOutcomeSummary> readCsvContent(final InputStream is,final String[] specificFields) throws IOException {
		final CSVParser parser = CSVParser.parse(new InputStreamReader(is,Charsets.UTF_8), CSVFormat.EXCEL.withHeader().withDelimiter(';'));
		final List<CSVRecord> records = parser.getRecords();
		records.remove(0);
		
		final List<GovernmentBodyAnnualOutcomeSummary> list = new ArrayList<>();
		
		for (final CSVRecord csvRecord : records) {
			final GovernmentBodyAnnualOutcomeSummary governmentBodyAnnualOutcomeSummary = new GovernmentBodyAnnualOutcomeSummary(csvRecord.get(MYNDIGHET), csvRecord.get(ORGANISATIONSNUMMER), Integer.parseInt(csvRecord.get(YEAR)));
			
			for (final String field : specificFields) {				
				governmentBodyAnnualOutcomeSummary.addDescriptionField(field,csvRecord.get(field));
			}
			
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.JANUARY.getValue(),csvRecord.get(UTFALL_JANUARI));
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.FEBRUARY.getValue(),csvRecord.get(UTFALL_FEBRUARI));
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.MARCH.getValue(),csvRecord.get(UTFALL_MARS));
			
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.APRIL.getValue(),csvRecord.get(UTFALL_APRIL));
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.MAY.getValue(),csvRecord.get(UTFALL_MAJ));
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.JUNE.getValue(),csvRecord.get(UTFALL_JUNI));
			
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.JULY.getValue(),csvRecord.get(UTFALL_JULI));
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.AUGUST.getValue(),csvRecord.get(UTFALL_AUGUSTI));
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.SEPTEMBER.getValue(),csvRecord.get(UTFALL_SEPTEMBER));
			
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.OCTOBER.getValue(),csvRecord.get(UTFALL_OKTOBER));
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.NOVEMBER.getValue(),csvRecord.get(UTFALL_NOVEMBER));
			addResultForMonth(governmentBodyAnnualOutcomeSummary,Month.DECEMBER.getValue(),csvRecord.get(UTFALL_DECEMBER));
			
			list.add(governmentBodyAnnualOutcomeSummary);
		}
		
		return list;
	}

	/**
	 * Adds the result for month.
	 *
	 * @param governmentBodyAnnualOutcomeSummary
	 *            the government body annual outcome summary
	 * @param month
	 *            the month
	 * @param value
	 *            the value
	 */
	private static void addResultForMonth(final GovernmentBodyAnnualOutcomeSummary governmentBodyAnnualOutcomeSummary, final int month,
			final String value) {
		if (value != null && value.length() >0 ) {
			governmentBodyAnnualOutcomeSummary.addData(month,Double.valueOf(value.replaceAll(",", ".")));
		}
	}

}
