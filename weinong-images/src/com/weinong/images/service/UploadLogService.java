package com.weinong.images.service;


import com.weinong.images.bean.UploadLog;
import com.weinong.images.core.DB_Images;

import java.sql.SQLException;

public class UploadLogService {
    public static boolean save(UploadLog uploadLog) throws SQLException {
        if (null == uploadLog.getId()) {
            Number id = DB_Images.IT.getUpdate().insertAndGetGeneratedKeys(uploadLog);
            uploadLog.setId(id.intValue());
            return true;
        } else {
            return DB_Images.IT.getUpdate().updateSingle(uploadLog) > 0;
        }
    }
}
