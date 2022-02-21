package com.ejlchina.searcher;

import com.ejlchina.searcher.param.*;
import com.ejlchina.searcher.util.GroupExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 检索参数
 * 
 * @author Troy.Zhou @ 2017-03-21
 */
public class SearchParam {

	// 原始检索参数
	private final Map<String, Object> paraMap;

	// Fetch 类型
	private final FetchType fetchType;

	// 需要 Select 的字段
	private final List<String> fetchFields;

	// 字段参数组
	private final GroupExpr<List<FieldParam>> paramGroup;

	// 排序参数
	private final List<OrderBy> orderBys = new ArrayList<>();

	// 分页参数
	private Paging paging;


	public SearchParam(Map<String, Object> paraMap, FetchType fetchType, List<String> fetchFields, GroupExpr<List<FieldParam>> paramGroup) {
		this.paraMap = paraMap;
		this.fetchType = fetchType;
		this.fetchFields = fetchFields;
		this.paramGroup = paramGroup;
	}

	/**
	 * 获取原始参数
	 * @return 原始参数
	 */
	public Map<String, Object> getParaMap() {
		return paraMap;
	}

	public FetchType getFetchType() {
		return fetchType;
	}

	public List<String> getFetchFields() {
		return fetchFields;
	}

	public GroupExpr<List<FieldParam>> getParamGroup() {
		return paramGroup;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public void addOrderBy(OrderBy orderBy) {
		orderBys.add(orderBy);
	}

	public List<OrderBy> getOrderBys() {
		return orderBys;
	}

}
