package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileListItem {
    private String name;
    private String type;

    public void setName(String name) {
        this.name = name;
        int i = this.name.lastIndexOf('.');
        if (i!=-1)
            this.type=this.name.substring(i+1).toLowerCase();

    }
}
