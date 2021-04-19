package com.tistory.yangpajuice.rc.mapper;

import java.util.*;

import org.apache.ibatis.annotations.*;

import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;

@Mapper
public interface IConfigMapper {
	public int checkTableExists();
	public void createTable();
	public List<ConfigItem> getConfigItemList(ConfigParam param);
	public int insertConfigItem(ConfigItem item);
	public int deleteConfigItem(ConfigItem item);
}
