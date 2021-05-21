package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wmgx
 * @create 2021-05-20-23:54
 **/
@Data
@Accessors(chain = true)
public class UserDirListVO {
    private Long userFileId;
    private String fileName;
}
