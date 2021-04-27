package com.hanhai.cloud.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wmgx
 * @create 2021-04-25-9:34
 **/
@Data
@Accessors(chain = true)
public class BaseEntity {

    /**
     * 创建时间
     */
//    @TableField(fill = FieldFill.INSERT) 做在了数据的部分
    private   LocalDateTime createdTime;
    /**
     * 更新
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedTime;
    /**
     * 删除标记
     */
    @TableLogic(value = "false",delval = "true")
    private Boolean deleted;

}
