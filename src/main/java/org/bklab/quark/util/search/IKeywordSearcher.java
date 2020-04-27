/*
 * Class: org.bklab.util.search.IKeywordSearcher
 * Modify date: 2020/3/20 下午1:13
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.search;

public interface IKeywordSearcher<T> {

    /**
     * 此方法预先检查对关键字、实体类进行空值检测，不需要重写。
     *
     * @param entity  实体类
     * @param keyword 关键字
     * @return 是否包含关键字
     */
    default boolean match(T entity, String keyword) {
        if (entity == null) return false;
        if (keyword == null || keyword.trim().isEmpty()) return true;
        return matchKeyword(entity, keyword);
    }

    /**
     * 如搜索实体类全部内容则不需要创建它，默认包含一个对实体类全部内容进行二级深度搜索的搜索器。
     *
     * @param entity  实体类
     * @param keyword 关键字
     * @return 是否包含关键字
     */
    boolean matchKeyword(T entity, String keyword);

}
