package com.zlin.clouddriver.client;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zlin.clouddriver.config.AliYunDriveProperties;
import com.zlin.clouddriver.webdav.exceptions.WebdavException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云云盘客户端
 */
@Slf4j
public class AliYunDriverClient {

    private final OkHttpClient okHttpClient;
    private final AliYunDriveProperties aliYunDriveProperties;

    public AliYunDriverClient(AliYunDriveProperties aliYunDriveProperties) {
        this.okHttpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request();
            request = request.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", aliYunDriveProperties.getAgent())
                    .removeHeader("authorization")
                    .addHeader("authorization", aliYunDriveProperties.getAuthorization())
                    .build();
            return chain.proceed(request);
        }).authenticator((route, response) -> {
            if (response.code() == 401 && response.body() != null
                    && Objects.requireNonNull(response.body()).string().contains("AccessToken")) {
                String refreshTokenResult;
                try {
                    refreshTokenResult = post("https://websv.aliyundrive.com/token/refresh", Collections.singletonMap("refresh_token", readRefreshToken()));
                } catch (Exception e) {
                    // 如果置换token失败，先清空原token文件，再试一次
                    deleteRefreshTokenFile();
                    refreshTokenResult = post("https://websv.aliyundrive.com/token/refresh", Collections.singletonMap("refresh_token", readRefreshToken()));
                }
                JSONObject jsonObject = JSONUtil.parseObj(refreshTokenResult);
                String accessToken = jsonObject.getStr("access_token");
                String refreshToken = jsonObject.getStr("refresh_token");
                Assert.hasLength(accessToken, "获取accessToken失败");
                Assert.hasLength(refreshToken, "获取refreshToken失败");
                aliYunDriveProperties.setAuthorization(accessToken);
                writeRefreshToken(refreshToken);
                return response.request().newBuilder()
                        .removeHeader("authorization")
                        .header("authorization", accessToken)
                        .build();

            }
            return null;
                })
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();

        this.aliYunDriveProperties = aliYunDriveProperties;
        init();

    }

    public void login() {
        //todo 暂未实现登录功能
    }

    /**
     * 初始化
     */
    public void init() {
        login();
        if (aliYunDriveProperties.getDriveId() == null) {
            String personalJson = post("/user/get", Collections.emptyMap());
            JSONObject jsonObject = JSONUtil.parseObj(personalJson);
            String driveId = jsonObject.getStr("default_drive_id");
            aliYunDriveProperties.setDriveId(driveId);
        }
    }

    /**
     * 删除refreshToken文件
     */
    private void deleteRefreshTokenFile() {
        String refreshTokenPath = aliYunDriveProperties.getWorkDir() + "refresh-token";
        Path path = Paths.get(refreshTokenPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("删除文件失败！", e);
        }
    }

    /**
     * 从文件路径中读取文件中到refreshToken值
     * @return refreshToken值
     */
    private String readRefreshToken() {
        String refreshTokenPath = aliYunDriveProperties.getWorkDir() + "refresh-token";
        Path path = Paths.get(refreshTokenPath);
        // 判断文件路径是否存在
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                log.error("create file failed！", e);
            }
        }

        // 读取文件
        try {
            byte[] bytes = Files.readAllBytes(path);
            if (bytes.length != 0) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("读取refreshToken文件 {} 失败：", refreshTokenPath, e);
        }
        // 如果文件为空，将配置文件中的refreshToken写入到文件中
        writeRefreshToken(aliYunDriveProperties.getRefreshToken());
        return aliYunDriveProperties.getRefreshToken();
    }

    /**
     * 将配置文件中的refreshToken写入到文件中
     * @param refreshToken refreshToken值
     */
    private void writeRefreshToken(String refreshToken) {
        String refreshTokenPath = aliYunDriveProperties.getWorkDir() + "refresh-token";
        try {
            Files.write(Paths.get(refreshTokenPath), refreshToken.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("写入refreshToken文件 {} 失败：", refreshTokenPath, e);
        }
    }

    public String post(String url, Object body)  {
        String bodyAsJson = JSONUtil.toJsonStr(body);
        Request request = new Request.Builder()
                .post(RequestBody.create(bodyAsJson, MediaType.parse("application/json; charset=utf-8")))
                .url(getTotalUrl(url))
                .build();
        try (Response response = okHttpClient.newCall(request).execute()){
            String result = Objects.requireNonNull(response.body()).string();
            if (!response.isSuccessful()) {
                log.error("请求失败，url={}, code={}, body={}", url, response.code(),
                        result);
            }
            return result;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new WebdavException(e);
        }
    }

    private String getTotalUrl(String url) {
        if (url.startsWith("http")) {
            return url;
        }
        return aliYunDriveProperties.getUrl() + url;
    }


}
