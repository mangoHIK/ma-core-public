package com.serotonin.m2m2.web.mvc.rest.v1.csv;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.serotonin.m2m2.module.ModelDefinition;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.web.mvc.rest.v1.exception.ModelNotFoundException;

/**
 * A CSV reader that will read the contents of a CSV file in to an annotated
 * class
 * 
 * Based on work done by Staffan Friberg
 * 
 * @author Terry Packer
 *
 * @param <T>
 *            Class with CSV annotations
 */
public class CSVPojoReader<T> implements Closeable {

	private CSVReader reader;
	private String[] headers;
	private CSVPojoHandler<T> pojoHandler;

	/**
	 * Constructs CSVPojoReader using a comma for the separator.
	 *
	 * @param clazz
	 *            The pojo class to populate
	 * @param reader
	 *            the reader to an underlying CSV source.
	 */
	public CSVPojoReader(CSVReader reader) {
		this.reader = reader;
		pojoHandler = new CSVPojoHandler<T>();
	}

	/**
	 * Closes the underlying CSVReader.
	 *
	 * @throws IOException
	 *             if the close fails
	 */
	public void close() throws IOException {
		reader.close();
	}

	/**
	 * Read the next pojo from the csv file
	 * 
	 * @return The next pojo from the csv file or null if end of file
	 * @throws IOException
	 */
	public T readNext() throws IOException {

		if (headers == null) {
			headers = reader.readNext();
			if (headers == null)
				return null;
			// TODO Check for headers[0] = 'modelType'
			// Make sure we don't have any accidental whitespaces
			for (int i = 0; i < headers.length; i++) {
				headers[i] = headers[i].trim();
			}
		}
		String[] line = reader.readNext();

		T pojo = null;
		if (line != null) {
			if (!pojoHandler.isInitialized()) {
				String typeName = line[0]; // Always needs to be
				// Find the model
				ModelDefinition definition = null;
				List<ModelDefinition> definitions = ModuleRegistry.getModelDefinitions();
				for (ModelDefinition d : definitions) {
					if (d.getModelTypeName().equalsIgnoreCase(typeName)) {
						definition = d;
						break;
					}
				}
				if (definition == null)
					throw new ModelNotFoundException(typeName);
				else
					pojoHandler.initialize(line, (T) definition.createModel());
			}

			pojo = pojoHandler.newInstance();
			for (int i = 1; i < headers.length; i++) {
				pojoHandler.setField(pojo, headers[i], line[i].trim());
			}
		}
		return pojo;
	}

	/**
	 * Read all lines from the CSV file and return a list of pojos representing
	 * the data
	 * 
	 * @return A list of pojos representing the CSV file
	 * @throws IOException
	 */
	public List<T> readAll() throws IOException {
		List<T> list = new ArrayList<T>();
		for (T pojo = readNext(); pojo != null; pojo = readNext()) {
			list.add(pojo);
		}
		return list;
	}
}
