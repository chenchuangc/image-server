package com.weinong.images.service;


import com.weinong.base.IDPage;
import com.weinong.images.bean.Images;
import com.weinong.images.core.DB_Images;
import java.sql.SQLException;
import java.util.List;

public class ImagesService {
    public static boolean save(Images images) throws SQLException {
        if (null == images.getId()) {
            Number id = DB_Images.IT.getUpdate().insertAndGetGeneratedKeys(images);
            images.setId(id.intValue());
            return true;
        } else {
            return DB_Images.IT.getUpdate().updateSingle(images) > 0;
        }
    }

	public static List<Images> queryImages(IDPage page) throws SQLException {
		String sql = " select id, name from images  ";
		if (page != null){
			sql += " where id < " + page.getLastId() + " order by id desc limit 0," + page.getPageSize();
		}else{
			sql += " order by id desc ";
		}
		return DB_Images.IT.getSelect().select(Images.class, sql);
		
	}
}
