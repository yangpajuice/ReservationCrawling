package com.tistory.yangpajuice.rc.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface IWebPageMapper {
	public int checkTableExists();
	public void createTable();
}
