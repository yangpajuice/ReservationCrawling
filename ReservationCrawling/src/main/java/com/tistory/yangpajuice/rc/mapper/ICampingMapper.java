package com.tistory.yangpajuice.rc.mapper;

import org.apache.ibatis.annotations.*;

import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;

@Mapper
public interface ICampingMapper {
	public int checkTableExists();
	public void createTable();
	
	public int insertCampingItem(CampingItem item);
	public int getMaxSeq(CampingParam param);
}
