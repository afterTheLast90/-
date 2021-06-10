package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateShareVO {
    private String shareId;
    private String sharePassword;
    private String shareUrl;

    public CreateShareVO setShareUrl(String url){
        this.shareUrl = url+"s/"+shareId;
        return this;
    }
}
