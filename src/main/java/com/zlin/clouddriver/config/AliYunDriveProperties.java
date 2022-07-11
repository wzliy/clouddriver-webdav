package com.zlin.clouddriver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aliyundrive")
@Data
public class AliYunDriveProperties {
    private String url = "https://api.aliyundrive.com/adrive/v2";
    private String authorization = "";
    private String refreshToken;
    private String workDir = "/etc/aliyun-driver/";
    private String agent = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E217 MicroMessenger/6.8.0(0x16080000) NetType/WIFI Language/en Branch/Br_trunk MiniProgramEnv/Mac";
    private String driveId;
    private Auth auth;


    public static class Auth {
        private Boolean enable = true;
        private String userName;
        private String password;

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
