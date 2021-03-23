package com.tistory.yangpajuice.rc.mapper;

import org.apache.ibatis.annotations.*;

import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;

@Mapper
public interface IWebPageMapper {
	public int checkTableExists();
	public void createTable();
	
	public int insertWebPageItem(WebPageItem item);
	public String getMaxIdWebPageItem(WebPageParam param);
}
