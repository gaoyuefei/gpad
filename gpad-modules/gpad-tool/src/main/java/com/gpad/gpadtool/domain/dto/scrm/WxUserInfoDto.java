package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName WxUserInfoDto.java
 * @Description accessToken返回Dto
 * @createTime 2023年09月21日 17:35:00
 */
@Data
@ApiModel(value = "OrderDetailDto", description = "accessToken返回Dto")
public class WxUserInfoDto {
    @ApiModelProperty(value = "成员UserID")
    private String userid;
    @ApiModelProperty(value = "成员票据，最大为512字节，有效期为1800s")
    private String userTicket;
    @ApiModelProperty(value = "非企业成员的标识，对当前企业唯一。不超过64字节")
    private String openid;
    @ApiModelProperty(value = "外部联系人id，当且仅当用户是企业的客户，且跟进人在应用的可见范围内时返回。" +
            "如果是第三方应用调用，针对同一个客户，同一个服务商不同应用获取到的id相同")
    private String externalUserid;

}
