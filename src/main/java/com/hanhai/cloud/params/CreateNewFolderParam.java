package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author wmgx
 * @create 2021-05-05-10:43
 **/
@Data
@Accessors(chain = true)
public class CreateNewFolderParam {


    @Pattern(regexp = " [0-9a-zA-Z_]+",message = "文件夹名称必须由字母数字下划线组成")
    @NotBlank(message = "目录名不能为空")
    @NotNull(message = "目录名不能为空")
    private String folderName;


    @NotBlank(message = "路径不能为空")
    @NotNull(message = "路径不能为空")
    private String path;
}
