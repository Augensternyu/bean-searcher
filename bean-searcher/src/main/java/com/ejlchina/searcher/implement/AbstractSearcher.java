package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.Metadata;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.implement.pagination.Pagination;
import com.ejlchina.searcher.param.SearchParam;
import com.ejlchina.searcher.util.StringUtils;
import com.ejlchina.searcher.virtual.DefaultVirtualParamProcessor;
import com.ejlchina.searcher.virtual.VirtualParamProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 自动检索器 根据 Bean 的 Class 和请求参数，自动检索 Bean
 * 
 */
public abstract class AbstractSearcher implements Searcher {

	private SearchSqlExecutor searchSqlExecutor;

	private SearchParamResolver searchParamResolver = new MainSearchParamResolver();

	private SearchSqlResolver searchSqlResolver = new MainSearchSqlResolver();

	private MetadataResolver metadataResolver = new DefaultMetadataResolver();

	private VirtualParamProcessor virtualParamProcessor = new DefaultVirtualParamProcessor();

	private final Map<Class<?>, Metadata> cache = new ConcurrentHashMap<>();

	@Override
	public <T> Number searchCount(Class<T> beanClass, Map<String, Object> paraMap) {
		SqlResult sqlResult = doSearch(beanClass, paraMap, null, true, false, true);
		try {
			return getCountFromSqlResult(sqlResult);
		} catch (SQLException e) {
			throw new SearcherException("A exception occurred when collect sql result!", e);
		} finally {
			sqlResult.closeResultSet();
		}
	}

	@Override
	public <T> Number searchSum(Class<T> beanClass, Map<String, Object> paraMap, String field) {
		Number[] results = searchSum(beanClass, paraMap, new String[] { field });
		if (results != null && results.length > 0) {
			return results[0];
		}
		return null;
	}

	@Override
	public <T> Number[] searchSum(Class<T> beanClass, Map<String, Object> paraMap, String[] fields) {
		if (fields == null || fields.length == 0) {
			throw new SearcherException("检索该 Bean【" + beanClass.getName() 
			+ "】的统计信息时，必须要指定需要统计的属性！");
		}
		SqlResult sqlResult = doSearch(beanClass, paraMap, fields, false, false, true);
		try {
			return getSummaryFromSqlResult(sqlResult);
		} catch (SQLException e) {
			throw new SearcherException("A exception occurred when collect sql result!", e);
		} finally {
			sqlResult.closeResultSet();
		}
	}

	protected Number getCountFromSqlResult(SqlResult sqlResult) throws SQLException {
		return (Number) sqlResult.getClusterResult().getObject(sqlResult.getSearchSql().getCountAlias());
	}

	protected Number[] getSummaryFromSqlResult(SqlResult sqlResult) throws SQLException {
		List<String> summaryAliases = sqlResult.getSearchSql().getSummaryAliases();
		ResultSet countResultSet = sqlResult.getClusterResult();
		Number[] summaries = new Number[summaryAliases.size()];
		for (int i = 0; i < summaries.length; i++) {
			String summaryAlias = summaryAliases.get(i);
			summaries[i] = (Number) countResultSet.getObject(summaryAlias);
		}
		return summaries;
	}

	protected <T> SqlResult doSearch(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields,
								   boolean shouldQueryTotal, boolean shouldQueryList, boolean needNotLimit) {
		Metadata metadata = getMetadata(beanClass);
		List<String> fieldList = metadata.getFieldList();
		SearchParam searchParam = searchParamResolver.resolve(fieldList, paraMap);
		searchParam.setSummaryFields(summaryFields);
		searchParam.setShouldQueryTotal(shouldQueryTotal);
		searchParam.setShouldQueryList(shouldQueryList);
		if (needNotLimit) {
			searchParam.setMax(null);
		}
		SearchSql searchSql = searchSqlResolver.resolve(metadata, searchParam);
		searchSql.setShouldQueryCluster(shouldQueryTotal || (summaryFields != null && summaryFields.length > 0));
		searchSql.setShouldQueryList(shouldQueryList);
		return searchSqlExecutor.execute(searchSql);
	}

	public Metadata getMetadata(Class<?> beanClass) {
		return metadataResolver.resolve(beanClass);
	}

	public Pagination getPagination() {
		return searchParamResolver.getPagination();
	}
	
	public SearchParamResolver getSearchParamResolver() {
		return searchParamResolver;
	}

	public void setSearchParamResolver(SearchParamResolver searchParamResolver) {
		this.searchParamResolver = searchParamResolver;
	}

	public SearchSqlResolver getSearchSqlResolver() {
		return searchSqlResolver;
	}

	public void setSearchSqlResolver(SearchSqlResolver searchSqlResolver) {
		this.searchSqlResolver = searchSqlResolver;
	}

	public SearchSqlExecutor getSearchSqlExecutor() {
		return searchSqlExecutor;
	}

	public void setSearchSqlExecutor(SearchSqlExecutor searchSqlExecutor) {
		this.searchSqlExecutor = searchSqlExecutor;
	}

	public VirtualParamProcessor getVirtualParamProcessor() {
		return virtualParamProcessor;
	}

	public void setVirtualParamProcessor(VirtualParamProcessor virtualParamProcessor) {
		this.virtualParamProcessor = virtualParamProcessor;
	}

}