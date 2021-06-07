package com.hanhai.cloud.params;

import com.hanhai.cloud.base.PageParam;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author wmgx
 * @create 2021-06-05-16:44
 **/
@Data
@Accessors(chain = true)
public class UserSearchParams extends PageParam {
    private String key;
}
