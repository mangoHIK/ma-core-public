/*
 *  Copyright (C) 2013 Deltamation Software. All rights reserved.
 *  @author Jared Wiltshire
 */
package com.serotonin.m2m2.db.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.logging.Log;
import org.springframework.jdbc.core.RowMapper;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.DeltamationCommon;

/**
 * Provides an API to retrieve, update and save
 * VO objects from and to the database.
 * 
 * Copyright (C) 2013 Deltamation Software. All Rights Reserved.
 * @author Jared Wiltshire
 */
public abstract class AbstractBasicDao<T> extends BaseDao {
    protected Log LOG;
    
    protected final List<String> properties = getProperties();
    protected final Map<String, String> propertiesMap = getPropertiesMap();
    protected final Map<String, Comparator<T>> comparatorMap = getComparatorMap();
    protected final Map<String, IFilter<T>> filterMap = getFilterMap();
    
    protected final Map<String,PropertyArguments> propertyArgumentsMap = getPropertyArgumentsMap();
    
    public final String tablePrefix;  //Select * from table as tablePrefix
    
    public AbstractBasicDao(){
    	tablePrefix = "";
    }

    /**
     * Provide a table prefix to use for complex queries.  Ie. Joins
     * Do not include the . at the end of the prefix
     * @param tablePrefix
     */
    public AbstractBasicDao(String tablePrefix){
    		this.tablePrefix = tablePrefix + ".";
    }
    
    /**
     * Gets a list of properties/db column names for the Dao object
     * First property should always be "id"
     * 
     * TODO can this be implemented automatically by using
     * BeanInfo info = Introspector.getBeanInfo(MachineVO.class);
     * 
     * @return list of properties
     */
    protected abstract List<String> getProperties();

    /**
     * Override to add a mapping for properties that are not 
     * directly accessible via a database column.
     * 
	 * @return
	 */
	protected Map<String, Comparator<T>> getComparatorMap() {
		return new HashMap<String,Comparator<T>>();
	}
	
	/**
	 * Override to add mappings for properties that are not
	 * directly accessible via a database column.
	 * @return
	 */
	protected Map<String, IFilter<T>> getFilterMap(){
		return new HashMap<String,IFilter<T>>();
	}
	
	protected Map<String, PropertyArguments> getPropertyArgumentsMap(){
		return new HashMap<String,PropertyArguments>();
	}

	interface PropertyArguments{
		public Object[] getArguments();
	}
	
	/**
     * Returns a map which maps a virtual property to a real one used
     * for sorting/filtering from the database
     * e.g. dateFormatted -> timestamp
     * @return map of properties
     */
    protected abstract Map<String, String> getPropertiesMap();
    
    /**
     * Gets the row mapper for converting the retrieved database
     * values into a VO object
     * @return row mapper
     */
    public abstract RowMapper<T> getRowMapper();

    
    /**
     * Get a vo by its ID
     * @param id ID of vo to retrieve
     * @return vo if found, otherwise null
     */
    public abstract T get(int id);
    
    /**
     * Return a VO with FKs populated
     * @param id
     * @return
     */
    public T getFull(int id) {
        return get(id);
    }
    
    /**
     * Count the number of rows in a table
     * @return number of rows in table
     */
    public abstract int count();
    
    /**
     * Get all vo in the system
     * @return List of all vo
     */
    public abstract List<T> getAll();
    
    /**
     * Return all VOs with FKs Populated
     * @return
     */
    public List<T> getAllFull() {
        return getAll();
    }

    protected String applyRange(String sql, List<Object> args, Integer offset, Integer limit) {
        if (offset == null || limit == null) {
            return sql;
        }
        
        switch (Common.databaseProxy.getType()) {
        case MYSQL:
        case POSTGRES:
            args.add(limit);
            args.add(offset);
            return sql + " LIMIT ? OFFSET ?";
        case DERBY:
        case MSSQL:
            args.add(offset);
            args.add(limit);
            return sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        default:
            return sql;
        }
    }
    
    protected String applyConditions(String sql, List<Object> args, Map<String, String> query, boolean or) {
        if (query != null && !query.isEmpty()) {
            int i = 0;
            
            for (String prop : query.keySet()) {
                boolean mapped = false;
                String dbProp = prop;
                //Don't allow filtering on properties with a filter
                //this will be done after the query
                if(!filterMap.containsKey(prop)){ 
	                if (propertiesMap.containsKey(prop)) {
	                    dbProp = propertiesMap.get(prop);
	                    mapped = true;
	                }
	                
	                if (mapped || properties.contains(prop)) {
	                    String tempSql = (i == 0) ? " WHERE " : (or ? " OR " : " AND ");
	                    
	                    String condition = query.get(prop);
	                    if (condition.startsWith("RegExp:")) {
	                        condition = condition.substring(7, condition.length());
	                        // simple RegExp handling
	                        if (condition.startsWith("^") && condition.endsWith("$")) {
	                            condition = condition.substring(1, condition.length() - 1);
	                            condition = condition.replace(".*.*", "%");
	                            condition = condition.replace(".*", "%");
	                            if(mapped)
	                            	tempSql += dbProp + " LIKE '" + condition + "'";
	                            else
	                            	tempSql += this.tablePrefix  + dbProp + " LIKE '" + condition + "'";
	                        }
	                        else {
	                            // all other cases, add condition which will ensure no results are returned
	                            tempSql += this.tablePrefix + "id = '-1'";
	                        }
	                    }else if(condition.startsWith("Long:")){
	                    	//Parse the value as Long:operatorvalue - Long:>10000
	                    	String ms = condition.substring(6,condition.length());
	                    	String compare = condition.substring(5, 6);
	                    	if(mapped)
	                    		tempSql += dbProp + " " + compare + " " + ms;
	                    	else
	                    		tempSql += this.tablePrefix + dbProp + " " + compare + " " + ms;
	                    }else if(condition.startsWith("LongRange:")){
	                    	//Parse the value as LongRange:>startValue:<EndValue
	                    	String[] parts = condition.split(":");
	                    	String startCompare = parts[1].substring(0,1);
	                    	String startMs = parts[1].substring(1,parts[1].length());
	                    	String endCompare = parts[2].substring(0,1);
	                    	String endMs = parts[2].substring(1,parts[2].length());	                    	
	                    	if(mapped)
	                    		tempSql += dbProp + startCompare + startMs + " AND " + dbProp + endCompare + endMs;
	                    	else
	                       		tempSql += this.tablePrefix + dbProp + startCompare + startMs + " AND " + this.tablePrefix + dbProp + endCompare + endMs;
	       	                    	
	                    }else if(condition.startsWith("Duration:")){
	                    	//Parse the value as Duration:operatorvalue - Duration:>1:00:00
	                    	String durationString = condition.substring(10,condition.length());
	                    	String compare = condition.substring(9, 10);
	                    	Long longValue = DeltamationCommon.unformatDuration(durationString);
	                    	if(mapped)
	                    		tempSql += dbProp + " " + compare + " " + longValue;
	                    	else
	                    		tempSql += this.tablePrefix + dbProp + " " + compare + " " + longValue;
	                    	
	                    }
	                    else {
	                        //if (condition.isEmpty()) // occurs when empty array is set in query
	                        //    continue;
	                        
	                        String[] parts = condition.split(",");
	                        String qMarks = "";
	                        for (int j = 0; j < parts.length; j++) {
	                            args.add(parts[j]);
	                            qMarks += j == 0 ? "?" : ",?";
	                        }
	                        // TODO not sure if IN will work with string values
	                        if(mapped)
	                        	tempSql += dbProp + " IN (" + qMarks + ")";
	                        else
	                        	tempSql += this.tablePrefix + dbProp + " IN (" + qMarks + ")";
	                    }
	                    sql += tempSql;
	                    i++;
	                }
                }//end if in filter map
            }
        }
        return sql;
    }
    
    protected String applySort(String sql, List<SortOption> sort, List<Object> selectArgs) {
        // always sort so that the offset/limit work as intended
        if (sort == null)
            sort = new ArrayList<SortOption>();
        if (sort.isEmpty())
            sort.add(new SortOption("id", false));
        
        int i = 0;
        for (SortOption option : sort) {
            String prop = option.getAttribute();
            boolean mapped = false;
            if(!comparatorMap.containsKey(prop)){ //Don't allow sorting on values that have a comparator
	            if (propertiesMap.containsKey(prop)) {
	                prop = propertiesMap.get(prop);
	                PropertyArguments args = propertyArgumentsMap.get(option.getAttribute());
	                if(args != null){
	                	Collections.addAll(selectArgs, args.getArguments());
	                }
	                mapped = true;
	            }
	            
	            if (mapped || properties.contains(prop)) {
	                sql += i++ == 0 ? " ORDER BY " : ", ";
	                if(mapped)
	                	sql += prop;
	                else
	                	sql += this.tablePrefix + prop;
	                if (option.isDesc()) {
	                    sql += " DESC";
	                }
	            }
	        }
        }
        return sql;
    }
    

    
    public abstract ResultsWithTotal dojoQuery(
            Map<String, String> query, List<SortOption> sort, Integer offset, Integer limit, boolean or);

    protected ResultsWithTotal dojoQuery(
            String selectSql, String countSql,
            Map<String, String> query, List<SortOption> sort, Integer offset, Integer limit, boolean or) {
        List<Object> selectArgs = new ArrayList<Object>();
        List<Object> countArgs = new ArrayList<Object>();
      
        String conditions = applyConditions("", selectArgs, query, or);
        countArgs.addAll(selectArgs);
        selectSql += conditions;
        countSql += conditions;
        
        selectSql = applySort(selectSql, sort, selectArgs);
        selectSql = applyRange(selectSql, selectArgs, offset, limit);
        if(LOG !=null){
        	LOG.info("Dojo Query: " + selectSql + " \nArgs: " + selectArgs.toString());
        }
        	// TODO work out how to do this in one transaction
        
        //FilterListCallback<T> filter = new FilterListCallback<T>(createFilters(query));
        //FilterListCallback<T> filter = new FilterListCallback<T>(createFilters(query),createComparatorChain(sort));
      
        //query(selectSql, selectArgs.toArray(), getRowMapper(),filter);
        List<T> results = query(selectSql, selectArgs.toArray(), getRowMapper());
        //TODO modify this ...
        //filter.orderResults();
        //List<T> results = filter.getResults();
        int count = ejt.queryForInt(countSql, countArgs.toArray());
        if(LOG !=null)
        	LOG.info("DB Has: " + count);
        
        //No results, this will mess up the dojo store we need to keep searching
        // until we are sure there are none in the DB or we have found some
//        if(results.size() == 0){
//        	 if(LOG !=null)
//             	LOG.info("All Filtered! ");
//        }

        
        
        //Do Filtering for more complex members that may not be mapped properties
        int removed = filterComplexMembers(results,query);
        count = count - removed;
        
        //count = count - filter.getFilteredResults().size();
        if(LOG !=null)
        	LOG.info("After filter: " + count);
        //Sort the remaining list
        //TODO This doesn't work because we need the whole data set to order properly
        //sortComplexMembers(results,sort);
        
        
        return new ResultsWithTotal(results, count);
    }

    /**
     * Create a list of filters for the complex members
     * @param query
     * @return
     */
    protected List<IFilter<T>> createFilters(Map<String, String> query){
    	
    	List<IFilter<T>> filters = new ArrayList<IFilter<T>>();
    	
		for (String prop : query.keySet()) {
			 if(filterMap.containsKey(prop)){
				IFilter<T> filter = filterMap.get(prop);
				
	            String condition = query.get(prop);
	            if (condition.startsWith("RegExp:")) {
	                condition = condition.substring(7, condition.length());
	                // simple RegExp handling
	                if (condition.startsWith("^") && condition.endsWith("$")) {
	                    condition = condition.substring(1, condition.length() - 1);
	                    filter.setFilter(condition);
	                }
	                filters.add(filter); //Save for use later
	             } //end if is regex
			 }//end if in filterMap
		 }
    	
    	return filters;
    }
    
	/**
	 * @param results
	 * @param query
	 * @return number of items filtered
	 */
	private int filterComplexMembers(List<T> results, Map<String, String> query) {
		
		List<IFilter<T>> filters = this.createFilters(query);
		int count = 0;
		
		//Now do the filter of the list
		if(filters.size() > 0){
			for(Iterator<T> i = results.iterator(); i.hasNext();){
				T vo = i.next();
				for(IFilter<T> filter : filters){
					if(filter.filter(vo)){
						i.remove();
						count++; //Decrement the count
					}
				}
				
			}
			
		}
		return count;

	}

	/**
	 * Sort on any members that are not directly accessible via the database query
	 * 
	 * Members must be mapped to a comparator in the comparatorMap
	 * 
	 * @param results
	 * @param sort
	 */
	private void sortComplexMembers(List<T> results, List<SortOption> sort) {
		
		ComparatorChain chain = this.createComparatorChain(sort);
		
		//Do the sort if we added at least one comparator
		if(chain.size()>0)
			Collections.sort(results,chain);
	}

	protected ComparatorChain createComparatorChain(List<SortOption> sort){
		ComparatorChain chain = new ComparatorChain();
		
		for (SortOption option : sort) {
            String prop = option.getAttribute();
            if(comparatorMap.containsKey(prop)){
            	chain.addComparator(comparatorMap.get(prop),option.isDesc());
            }
		}
		return chain;
	}
	
	

}
