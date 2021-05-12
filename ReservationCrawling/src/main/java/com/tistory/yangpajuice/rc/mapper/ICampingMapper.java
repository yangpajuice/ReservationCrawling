package com.tistory.yangpajuice.rc.mapper;

import java.util.*;

import org.apache.ibatis.annotations.*;

import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;

@Mapper
public interface ICampingMapper {
	public int checkTableExists();
	public void createTable();
	
	public List<CampingItem> getCampingItemList(CampingParam param);
	public int insertCampingItem(CampingItem item);
	public int getMaxSeq(CampingParam param);
	public int increaseSeqCampingItem(CampingParam param);
}
