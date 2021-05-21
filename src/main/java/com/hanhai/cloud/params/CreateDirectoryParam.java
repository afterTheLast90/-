package com.hanhai.cloud.params;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class CreateDirectoryParam {

    @NotBlank(message = "父路径不能为空")
    @NotNull(message = "父路径不能为空")
    private String path;

    @NotBlank(message = "文件夹名不能为空")
    @NotNull(message = "文件夹名不能为空")
    private String fileName;
}
