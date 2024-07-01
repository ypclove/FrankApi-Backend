package com.frank.apibackstage.controller;

import cn.hutool.core.io.FileUtil;
import com.frank.apibackstage.manager.CosManager;
import com.frank.apibackstage.model.vo.FileVo;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.BaseResponse;
import com.frank.apicommon.common.ResultUtils;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.config.CosClientConfig;
import com.frank.apicommon.enums.FileUploadBizEnum;
import com.frank.apicommon.enums.FileUploadStatusEnum;
import com.frank.apicommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import static com.frank.apicommon.constant.FileConstant.*;

/**
 * @author Frank
 * @date 2024/6/28
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 上传文件
     *
     * @param multipartFile 多部分文件
     * @param biz           业务
     * @param request       HttpServletRequest
     * @return {@link FileVo}
     */
    @PostMapping("/upload")
    public BaseResponse<FileVo> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           String biz,
                                           HttpServletRequest request) {
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        FileVo fileVo = new FileVo();
        if (Objects.isNull(fileUploadBizEnum)) {
            return uploadError(fileVo, multipartFile, "上传失败，请重试");
        }
        boolean result = validFile(multipartFile, fileUploadBizEnum);
        if (!result) {
            return uploadError(fileVo, multipartFile, "该文件无效");
        }
        UserVO loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(UID_LENGTH);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getText(), loginUser.getId(), filename);
        File file = null;

        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            fileVo.setName(multipartFile.getOriginalFilename());
            fileVo.setUid(RandomStringUtils.randomAlphanumeric(8));
            fileVo.setStatus(FileUploadStatusEnum.SUCCESS.getCode());
            fileVo.setUrl(cosClientConfig.getCosHost() + filepath);
            // 返回可访问地址
            return ResultUtils.success(fileVo);
        } catch (Exception e) {
            log.error("文件上传失败，路径：{}", filepath, e);
            return uploadError(fileVo, multipartFile, "上传失败，请重试");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("文件删除失败，路径：{}", filepath);
                }
            }
        }
    }

    /**
     * 上传错误处理
     *
     * @param fileVo        文件上传对象
     * @param multipartFile 文件
     * @param message       错误消息
     * @return FileVo
     */
    private BaseResponse<FileVo> uploadError(FileVo fileVo, MultipartFile multipartFile, String message) {
        fileVo.setName(multipartFile.getOriginalFilename());
        fileVo.setUid(RandomStringUtils.randomAlphanumeric(UID_LENGTH));
        fileVo.setStatus(FileUploadStatusEnum.ERROR.getCode());
        return ResultUtils.error(StatusCode.OPERATION_ERROR.getCode(), fileVo, message);
    }

    /**
     * 校验文件是否有效
     *
     * @param multipartFile     文件
     * @param fileUploadBizEnum 业务类型 {@link FileUploadBizEnum}
     * @return 文件是否有效
     */
    private boolean validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > UPLOAD_SIZE) {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "文件大小不能超过 2M");
            }
            if (!Arrays.asList(JPEG, JPG, SVG, PNG, WEBP, JFIF).contains(fileSuffix)) {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "暂不支持该文件类型");
            }
        }
        return true;
    }
}
