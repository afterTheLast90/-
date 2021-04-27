package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-27-16:14
 **/

@Data
@Accessors(chain = true)
@TableName("system_settings")
public class SystemSetting extends BaseEntity  implements Serializable,Cloneable {
    /** 设置key */
    @TableId
    private String settingKey ;
    /** 设置值 */
    private String settingValue ;
    /** 设置注释 */
    private String settingComment ;
}
