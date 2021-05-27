package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.DeleteMapping;

@Data
@Accessors(chain = true)
public class GroupUserSearchVO {
    /** 用户id */
    private Long userId;
    /** 用户名 */
    private String userName;
    /** 用户邮箱 */
    private String userEmail;
    /** 用户电话 */
    private String userPhone;

    public GroupUserSearchVO setUserEmail(String email) {
        String[] e = email.split("@");
        if(e[0].length() <=3) {
            this.userEmail = email;
        }
        else
            this.userEmail = e[0].substring(0, 3) + "***@" + e[1];
        return this;
    }

    public GroupUserSearchVO setUserPhone(String phone) {
        this.userPhone = phone.substring(0,3) + "****" + phone.substring(7, 11);
        return this;
    }
}
