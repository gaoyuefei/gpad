package com.gpad.gpadtool.service.serviceImpl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.bo.input.*;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.common.core.vo.GentlemanSaltingVo;
import com.gpad.common.security.utils.SecurityUtils;
import com.gpad.gpadtool.constant.CommCode;
import com.gpad.gpadtool.domain.dto.FileInfoDto;
import com.gpad.gpadtool.domain.dto.FlowInfoDto;
import com.gpad.gpadtool.domain.dto.UploadFileOutputDto;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.domain.entity.GpadDlrAuthenticationEmailInfo;
import com.gpad.gpadtool.domain.entity.GpadIdentityAuthInfo;
import com.gpad.gpadtool.domain.entity.HandoverCarCheckInfo;
import com.gpad.gpadtool.domain.vo.JzqDataValidSignatureVo;
import com.gpad.gpadtool.domain.vo.JzqOrganizationAuditStatusVo;
import com.gpad.gpadtool.domain.vo.JzqSignApplyVo;
import com.gpad.gpadtool.domain.vo.JzqUserValidSignatureVo;
import com.gpad.gpadtool.repository.*;
import com.gpad.gpadtool.service.AutoSignatureService;
import com.gpad.gpadtool.service.GRTService;
import com.gpad.gpadtool.utils.DateUtil;
import com.gpad.gpadtool.utils.FileUtil;
import com.gpad.gpadtool.utils.RedisLockUtils;
import com.gpad.gpadtool.utils.UuidUtil;
import com.junziqian.sdk.bean.ResultInfo;
import com.junziqian.sdk.bean.req.sign.ext.SignatoryReq;
import com.junziqian.sdk.util.RequestUtils;
import com.junziqian.sdk.util.http.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName AutoSignatureServiceImpl.java
 * @Description TODO
 * @createTime 2023年09月21日 11:52:00
 */
@Slf4j
@Service
public class AutoSignatureServiceImpl  implements AutoSignatureService {

    @Value("${config.file.path}")
    private String FILE_PATH;

    @Value("${environment.config}")
    private String environment;

    private static final String SERVICE_URL = "https://api.sandbox.junziqian.com";
    private static final String APP_SECRET = "70adae25924410c408aea504181c7f80";
    private static final String APP_KEY = "924410c408aea504";

    @Autowired
    private FlowInfoRepository flowInfoRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private GpadIdentityAuthInfoRepository gpadIdentityAuthInfoRepository;

    @Autowired
    private HandoverCarCheckInfoRepository handoverCarCheckInfoRepository;

    @Autowired
    private GpadDlrAuthenticationEmailInfoRepository gpadDlrAuthenticationEmailInfoRepository;

    @Autowired
    private GRTService grtSservice;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> startGentlemanSignature(AutoSignatureInputBO autoSignatureInputBO, MultipartFile file,MultipartFile fileCustomerPng,MultipartFile fileProductPng) {

        GpadDlrAuthenticationEmailInfo gpadDlrAuthenticationEmailInfo = gpadDlrAuthenticationEmailInfoRepository.queryEmail(autoSignatureInputBO.getDealerCode());
        if ("test".equals(environment)){
            if (!ObjectUtil.isNotEmpty(gpadDlrAuthenticationEmailInfo)){
                gpadDlrAuthenticationEmailInfo = new GpadDlrAuthenticationEmailInfo();
                gpadDlrAuthenticationEmailInfo.setCompanyName("广汽传祺汽车销售有限公司");
                gpadDlrAuthenticationEmailInfo.setUscc("914401013275898060");
                gpadDlrAuthenticationEmailInfo.setEmail("gqcq2@bccto.me");
            }
        }

        if (!ObjectUtil.isNotEmpty(gpadDlrAuthenticationEmailInfo)){
          return   R.fail(null,CommCode.DATA_IS_WRONG.getCode(),"检测"+autoSignatureInputBO.getDealerCode()+"店未进行企业实名认证，请联系管理员申请企业认证");
        }
        log.info("快速校验1 method：queryEmail()1--->>> {}",JSON.toJSONString(gpadDlrAuthenticationEmailInfo));

        JzqOrganizationAuditStatusVo jzqOrganizationAuditStatusVo = checkOrganizationStatus(gpadDlrAuthenticationEmailInfo);
        if (!jzqOrganizationAuditStatusVo.getSuccess()){
          return   R.fail(null,CommCode.DATA_IS_WRONG.getCode(),jzqOrganizationAuditStatusVo.getMsg());
        }
        log.info("快速校验2 method：checkOrganizationStatus()1--->>> {}",JSON.toJSONString(jzqOrganizationAuditStatusVo));

        String data = "";
        String bussinessNo = autoSignatureInputBO.getBussinessNo();
        log.info("快速通过校验后：发起裙子签证进入1 method：startGentlemanSignature()1--->>> {}",bussinessNo);

        try {
            RedisLockUtils.lock(bussinessNo);

            //TODO 查询数据
            String account = autoSignatureInputBO.getAccount();
            log.info("发起裙子签证进入2 method：startGentlemanSignature()1--->>> {}", JSON.toJSONString(account));

            GpadIdentityAuthInfo gpadIdentityAuthInfo = gpadIdentityAuthInfoRepository.checkMemorySign(account);
            if(!ObjectUtil.isNotEmpty(gpadIdentityAuthInfo)){
                return R.fail(null, CommCode.UNKNOWN_REAL_NAME.getCode(),"请先通过实名认证");
            }
            log.info("发起裙子签证进入3 method：startGentlemanSignature()1--->>>查询账号返回接口 {}",JSON.toJSONString(gpadIdentityAuthInfo));
            autoSignatureInputBO.setProductName(gpadIdentityAuthInfo.getProductName());
            autoSignatureInputBO.setIdentityType1(1);
            autoSignatureInputBO.setIdentityCard1(gpadIdentityAuthInfo.getIdentityCard());
            autoSignatureInputBO.setProductMobile(gpadIdentityAuthInfo.getProductMobile());
            autoSignatureInputBO.setMemorySignPath(gpadIdentityAuthInfo.getFilePath());

            //安全认证->接口加盐处理
            GentlemanSaltingVo gentlemanSaltingVo = getAuthSafety();

            //校验当前个人是否实名认证 TODO 1.询问前端是否需要跳转不同页面    3.代码优化 参数封装方法
            if (autoSignatureInputBO.getIdentityType() == 1){
                if(!personValid(gentlemanSaltingVo,autoSignatureInputBO.getProductName(),autoSignatureInputBO.getIdentityCard1())){
                    return R.fail(null,"产品专家认证失败，请检查身份证号,手机号");
                }
            }

            AuthUserSignatureInputBO authUserSignatureInputBO = new AuthUserSignatureInputBO();
            BeanUtil.copyProperties(autoSignatureInputBO,authUserSignatureInputBO);
            authUserSignatureInputBO.setAccount(authUserSignatureInputBO.getAccount());
            log.info("发起裙子签证进入4 method：startGentlemanSignature()1--->>>修改参数为{}",JSON.toJSONString(authUserSignatureInputBO));
            //TODO 这里一定要传ID  后端解析账号
            Boolean result1 = gpadIdentityAuthInfoRepository.updateAuthUserSignature(authUserSignatureInputBO);
            if(!result1){
                throw  new ServiceException("产品专家认证失败，请检查身份证号",CommCode.DATA_UPDATE_WRONG.getCode());
            }

            //TODO 上传到文件服务器 存储到数据库
            FileInfoDto fileInfoDto = new FileInfoDto();
            fileInfoDto.setFilePath(autoSignatureInputBO.getMemorySignPath());
            fileInfoDto.setBussinessNo(autoSignatureInputBO.getBussinessNo());
            fileInfoDto.setId(autoSignatureInputBO.getFileId());
            fileInfoDto.setFileType(1);
            fileInfoDto.setLinkType(31);
            Boolean productResult = fileInfoRepository.saveOrUpdateFileInfoDto(fileInfoDto);
            if (!productResult){
                throw new ServiceException("产品专家签名订单绑定失败",CommCode.DATA_UPDATE_WRONG.getCode());
            }
            if (!productResult){
                return R.fail(null,"产品专家签名订单绑定失败号,请求重新上传");
            }
            log.info("发起裙子签证进入5 method：startGentlemanSignature()1--->>>产品专家发起结束");
            //必填校验  TODO 判断文件是否为空
            if (null == file && null == fileCustomerPng){
                return R.ok(null,"专家签名成功(入库成功)");
            }

            if(null != fileCustomerPng){
                //校验当前个人是否实名认证
                if (autoSignatureInputBO.getIdentityType() == 1){
                    if(!personValid(gentlemanSaltingVo,autoSignatureInputBO.getFullName(),autoSignatureInputBO.getIdentityCard())){
                        return R.fail(null,"客户实名认证失败，请检查身份证号");
                    }
                }
            }
            log.info("发起裙子签证进入6 method：turnOnLineSignature()1--->>>开始发起合同调用");
            R result = turnOnLineSignature(autoSignatureInputBO,gentlemanSaltingVo,file,fileCustomerPng,fileProductPng,gpadDlrAuthenticationEmailInfo);
            if (!"200".equals(result.getCode()+"")){
                throw new ServiceException(result.getMsg(),500);
            }
            if (ObjectUtil.isEmpty(result)){
                return R.fail(null,CommCode.INTFR_OUTTER_INVOKE_ERROR.getCode(),"发起线上签失败");
            }
            if (!StringUtils.isNotEmpty(result.getData().toString())){
                return R.fail(null,CommCode.INTFR_OUTTER_INVOKE_ERROR.getCode(),"发起线上签失败");
            }
            log.info("发起裙子签证进入6 method：turnOnLineSignature()1--->>>发起合同结果{}",result);
            RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
            //构建请求参数
            Map<String,Object> params=new HashMap<>();
            String apl = result.getData().toString();
            params.put("applyNo", apl); //TODO *
            ResultInfo<String> ri= requestUtils.doPost("/v2/sign/linkAnonyDetail",params);
            log.info("发起裙子签证进入6.1 method：applyNo()1--->>>{}",JSON.toJSONString(ri));
            if (ObjectUtil.isNotEmpty(ri)){
                data = ri.getData();
            }

            autoSignatureInputBO.setAccountId(gpadIdentityAuthInfo.getId());
            Boolean res = handoverCarCheckInfoRepository.updatecontractInfoById(apl, data, autoSignatureInputBO);
            if (!res){
                throw new ServiceException("合同连接保存失败",CommCode.DATA_UPDATE_WRONG.getCode());
            }
            log.info("保存合同成功 执行method：filtOUTSteam()1--->>{}->>>{}",apl,bussinessNo);
            filtOUTSteam(apl,bussinessNo);

            log.info("发起裙子签证进入7 method：updatecontractInfoById()1--->>>保存合同连接结束{}",res);
            log.info("发起裙子签证进入8 method：updatecontractInfoById()1--->>>结束发起合同");

        } finally {
            RedisLockUtils.unlock(bussinessNo);
        }
        return R.ok(data,"合同发起成功");
    }

    private JzqOrganizationAuditStatusVo checkOrganizationStatus(GpadDlrAuthenticationEmailInfo gpadDlrAuthenticationEmailInfo) {
        RequestUtils re =RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
//构建请求参数
        Map<String,Object> params=new HashMap<>();
        params.put("emailOrMobile",gpadDlrAuthenticationEmailInfo.getEmail());
        ResultInfo<JSONObject> ri= re.doPost("/v2/user/organizationAuditStatus",params);
        log.info("请求君子签结果:"+ JSONObject.toJSONString(ri));
        JzqOrganizationAuditStatusVo jzqOrganizationAuditStatusVo = JSONObject.parseObject(JSON.toJSONString(ri), JzqOrganizationAuditStatusVo.class);
        log.info("请求转换结果:"+ JSONObject.toJSONString(jzqOrganizationAuditStatusVo));
        return jzqOrganizationAuditStatusVo;
    }

    public R turnOnLineSignature(AutoSignatureInputBO autoSignatureInputBO,GentlemanSaltingVo gentlemanSaltingVo, MultipartFile file, MultipartFile fileCustomerPng,MultipartFile fileProductPng,GpadDlrAuthenticationEmailInfo gpadDlrAuthenticationEmailInfo) {
        File localCustomerPng = null;
        //查询合同编号 是否存在
        String bussinessNo = autoSignatureInputBO.getBussinessNo();
        HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoRepository.queryDeliverCarConfirmInfo(bussinessNo);
        log.info("发起裙子签证进入10 method：queryDeliverCarConfirmInfo()1--->>>查询合同表信息{}",JSON.toJSONString(handoverCarCheckInfo));
        String contractAplNo = handoverCarCheckInfo.getContractAplNo();
        Map<String, Object> params = null;

        //开始进行君子签参封装
        //Post请求封装
        File localFile = null;
        File localProductPng = null;
        String suffix = ".pdf";
        String filePngSuffix = ".png";
        String apl = "";
        String url= SERVICE_URL+"/v2/sign/applySign";
        try {
            if (null != file){
                localFile = transferToFile(file,suffix);
            }

            if (null != fileProductPng){
                localProductPng = transferToFile(fileProductPng,filePngSuffix);
            }

            if (null != fileCustomerPng){
                localCustomerPng = transferToFile(fileCustomerPng,filePngSuffix);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Boolean result = false;
        try {
            RedisLockUtils.lock(bussinessNo);
            if (!StringUtils.isNotEmpty(contractAplNo)){
                params = getAuthPostParams(gentlemanSaltingVo);
                //发起
                Integer isArchive = 0;

                //覆盖之前得签名 TODO //提取参数 默认调用 产品专家
                if (null != localProductPng){
                    String identityCard1 = autoSignatureInputBO.getIdentityCard1();
                     result = uploadMemorySignPath(gentlemanSaltingVo,localProductPng, identityCard1);
                    if (!result){
                        throw new ServiceException(CommCode.UPLOAD_SIGN_PNG_WRONG.getMessage(),CommCode.UPLOAD_SIGN_PNG_WRONG.getCode());
                    }
                }

                //覆盖之前得签名 TODO //提取参数 默认调用 客户名字
                if (null != fileCustomerPng){
                    result = uploadMemorySignPath(gentlemanSaltingVo,localCustomerPng,autoSignatureInputBO.getIdentityCard());
                    if (!result){
                        throw new ServiceException("君子签名重传失败",500);
                    }
                }

                JSONArray signatories = putParameter(autoSignatureInputBO);
                params.put("signatories",signatories.toJSONString());
                if (null != localCustomerPng){
                    isArchive = 1;
                    SignatoryReq sReq1 = new SignatoryReq();
                    signatories.add(addCustomerInfo(sReq1,autoSignatureInputBO));
                }
                params.put("isArchive",isArchive);
                params.put("file",localFile);
                params.put("contractName",autoSignatureInputBO.getContractName());
                String chapteJsonFirst = autoSignatureInputBO.getChapteJsonFirst();
                signatories.add(jointEnterprise(chapteJsonFirst,gpadDlrAuthenticationEmailInfo));
                params.put("signatories",signatories.toJSONString());



                log.info("封住结束参数为{}", JSONObject.toJSONString(signatories));
                //这里必须用new String，因为使用的是IdentityHashMap（为了多个file的同name上传）

                //开始调用外部接口
                String str= null;
                try {
                    str = HttpClientUtils.init().getPost(url,null,params,true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (StringUtils.isEmpty(str)){
                    throw new ServiceException("调用裙子签签名网络繁忙，请稍后重试",500);
                }

                JzqSignApplyVo jzqSignatureVo = JSON.parseObject(str, JzqSignApplyVo.class);
                log.info("君子签jzqSignatureVo返回参数为{}", JSONObject.toJSONString(jzqSignatureVo));
                if (ObjectUtil.isEmpty(jzqSignatureVo)){
                    throw new ServiceException("调用裙子签签名网络繁忙，请稍后重试",500);
                }

                if(!jzqSignatureVo.getSuccess()){
                    throw new ServiceException(jzqSignatureVo.getMsg(),CommCode.IDENTITY_WRITEPNG_SIGN_INFO_FAIL.getCode());
                }
                log.info("君子签jzqSignatureVo返回成功为{}", JSONObject.toJSONString(str));
                apl = jzqSignatureVo.getData();

//                log.info("君子签返回获取得apl为{}", JSONObject.toJSONString(str));
//                if (!StringUtils.isEmpty(str)){
//                    apl = JSON.parseObject(str).getString("data");
//                    System.out.println(apl);
//                }
                // 数据入库 TODO 是否归档 在数据落库
                Boolean rel = handoverCarCheckInfoRepository.updatecontractInfoById(apl,null,autoSignatureInputBO);
                if (!rel){
                    throw new ServiceException("合同信息PDF保存失败",CommCode.DATA_UPDATE_WRONG.getCode());
                }
                return R.ok(apl,jzqSignatureVo.getMsg());
            }else {
                //覆盖之前得签名 TODO //提取参数 默认调用 产品专家
                if (null != fileCustomerPng){
                    Boolean result1 = uploadMemorySignPath(gentlemanSaltingVo,localCustomerPng,autoSignatureInputBO.getIdentityCard());
                    if (!result1){
                        throw new ServiceException("追加签名PDF入库失败",CommCode.DATA_UPDATE_WRONG.getCode());
                    }
                }
                //追加 -> 发起签约
                ContinueStartSignatureInputBO continueStartSignatureInputBO = new ContinueStartSignatureInputBO();
                continueStartSignatureInputBO.setFullName(autoSignatureInputBO.getFullName());
                continueStartSignatureInputBO.setIdentityCard(autoSignatureInputBO.getIdentityCard());
                continueStartSignatureInputBO.setMobile(autoSignatureInputBO.getMobile());
                continueStartSignatureInputBO.setIdentityType(autoSignatureInputBO.getIdentityType());
                continueStartSignatureInputBO.setContractName(autoSignatureInputBO.getContractName());
                continueStartSignatureInputBO.setApplyNo(contractAplNo);
                continueStartSignatureInputBO.setIsArchive(1);
                continueStartSignatureInputBO.setEncry_method("md5");
                continueStartSignatureInputBO.setChapteJson(autoSignatureInputBO.getChapteJson());
                R r = continueStartGenManSignatureAddEnd(gentlemanSaltingVo,continueStartSignatureInputBO);
                log.info("追加返回参数为:{}",JSONObject.toJSONString(r));
                // TODO
                return R.ok(r.getData(),r.getMsg());
            }
        } finally {
             //标记GC
             localFile = null;
             localProductPng = null;
            RedisLockUtils.unlock(bussinessNo);
        }
    }

    private SignatoryReq jointEnterprise(String chapteJsonFirst,GpadDlrAuthenticationEmailInfo gpadDlrAuthenticationEmailInfo) {
        log.info("打印企业信息入参{}》》》》{}",chapteJsonFirst,JSON.toJSONString(gpadDlrAuthenticationEmailInfo));
        //拼接企业参数
        SignatoryReq sReq3 =new SignatoryReq();
        sReq3.setFullName(gpadDlrAuthenticationEmailInfo.getCompanyName()); //企业姓名
        sReq3.setIdentityType(11); //证件类型
        sReq3.setIdentityCard(gpadDlrAuthenticationEmailInfo.getUscc());//营业执照号
        sReq3.setEmail(gpadDlrAuthenticationEmailInfo.getEmail()); //在君子签注册认证的邮箱
        sReq3.setChapteJson(chapteJsonFirst);//坐标（X Y）定位签字位置
        // sReq.setSearchKey("服务组件");//关键字定位签字位置
        sReq3.setSignLevel(0);
        sReq3.setNoNeedVerify(1);
        sReq3.setServerCaAuto(1);//当前签约方自动签署
        sReq3.setOrderNum(3);
        log.info("打印企业信息转换结束{}",JSON.toJSONString(sReq3));
        return sReq3;
    }

    private R continueStartGenManSignatureAddEnd(GentlemanSaltingVo gentlemanSaltingVo, ContinueStartSignatureInputBO continueStartSignatureInputBO) {
        Map<String, Object>  params= getCommonValidPostParams(gentlemanSaltingVo);
        String url=SERVICE_URL+"/v2/sign/signatory/add";
        params.put("applyNo",continueStartSignatureInputBO.getApplyNo());
        params.put("isArchive",continueStartSignatureInputBO.getIsArchive());//是否归档，0不归档，1归档，默认值1
        params.put("app_key",APP_KEY);
        params.put("encry_method","md5");

        JSONArray signatories=new JSONArray();
        SignatoryReq sReq=new SignatoryReq();
        sReq.setFullName(continueStartSignatureInputBO.getFullName());          //姓名
        sReq.setIdentityType(continueStartSignatureInputBO.getIdentityType());  //证件类型
        sReq.setIdentityCard(continueStartSignatureInputBO.getIdentityCard());//身份证号
        sReq.setMobile(continueStartSignatureInputBO.getMobile()); //手机号
        sReq.setChapteJson(continueStartSignatureInputBO.getChapteJson());
        sReq.setSignLevel(1);//签字类型，标准图形章或公章:0标准图形章,1公章或手写,2公章手写或手写,3个人方形标准章（用户类型是个人且姓名2-4个字符生效，其他情况默认使用系统标准图形章）
        sReq.setServerCaAuto(1);//0手动签；1自动签
        sReq.setNoNeedVerify(1); //取消签约前短信校验
        sReq.setServerCaAuto(1);//是否使用自动签署完成，0或null不使用，1自动(当且只当合同处理方式为部份自动或收集批量签时有效)
        sReq.setSignLevel(1);//签字类型，标准图形章或公章:0标准图形章,1公章或手写,2公章手写或手写,3个人方形标准章（用户类型是个人且姓名2-4个字符生效，其他情况默认使用系统标准图形章）
        sReq.setOrderNum(2);
        signatories.add(sReq);
        params.put("signatories",signatories.toJSONString());
        System.out.println(signatories.toJSONString());
        String str= HttpClientUtils.init().getPost(url,null,params,true);
        System.out.println(str);
        return R.ok(str);
    }

    private SignatoryReq addCustomerInfo(SignatoryReq sReq1,AutoSignatureInputBO autoSignatureInputBO) {
        sReq1.setFullName(autoSignatureInputBO.getFullName()); //姓名
        sReq1.setIdentityType(autoSignatureInputBO.getIdentityType()); //证件类型
        sReq1.setIdentityCard(autoSignatureInputBO.getIdentityCard());//身份证号
        sReq1.setMobile(autoSignatureInputBO.getMobile()); //手机号
     // sReq1.setChapteJson("[{\"page\":1,\"chaptes\":[{\"offsetX\":0.721,\"offsetY\":0.7941}]}]");
        sReq1.setChapteJson(autoSignatureInputBO.getChapteJson());
        sReq1.setOrderNum(2);
        // sReq.setSearchKey("服务组件");//关键字定位签字位置
        sReq1.setNoNeedVerify(1); //取消签约前短信校验
        sReq1.setServerCaAuto(1);//是否使用自动签署完成，0或null不使用，1自动(当且只当合同处理方式为部份自动或收集批量签时有效)
        sReq1.setSignLevel(1);//签字类型，标准图形章或公章:0标准图形章,1公章或手写,2公章手写或手写,3个人方形标准章（用户类型是个人且姓名2-4个字符生效，其他情况默认使用系统标准图形章）
        return sReq1;
    }

    private Boolean uploadMemorySignPath(GentlemanSaltingVo gentlemanSaltingVo, File localFile1, String identityCard) {
        String success = "";
        RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
        //构建请求参数
        Map<String,Object> params=new HashMap<>();
        params.put("identityCard",identityCard);
        params.put("signImgFile",new FileBody(localFile1));
        ResultInfo<Void> ri= requestUtils.doPost("/v2/user/uploadPersSign",params);
        String str = JSONObject.toJSONString(ri);
        if (!StringUtils.isEmpty(str)){
            success = JSON.parseObject(str).getString("success");
            log.info("上传是否成功{}",success);
        }
        return Boolean.valueOf(true);
    }

    public boolean personValid(GentlemanSaltingVo gentlemanSaltingVo,String name,String identityCard) {

        if (true){
            return true;
        }

        log.info("开始校验 method:personValid{},身份证号    {}",name,identityCard);
        Boolean result = false;
        String msg = "";
        String code = "";
        String message = "";
        RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
        //构建请求参数
        Map<String,Object> params = getCommonValidPostParams(gentlemanSaltingVo);
        params.put("name",name);
        params.put("identityCard",identityCard);
        ResultInfo<Void> ri= requestUtils.doPost("/v2/auth/userValid",params);
        log.info("身份签名认证签名结果 method:personValid{}",JSONObject.toJSONString(ri));

        JzqUserValidSignatureVo jzqUserValidSignatureVo = JSON.parseObject(JSONObject.toJSONString(ri), JzqUserValidSignatureVo.class);

        if (ObjectUtil.isNotEmpty((jzqUserValidSignatureVo))){
            if (jzqUserValidSignatureVo.getSuccess()){
                JzqDataValidSignatureVo data = jzqUserValidSignatureVo.getData();
                if (ObjectUtil.isEmpty(data)){
                    throw new ServiceException(CommCode.IDENTITY_WRITEPNG_SIGN_INFO_WRONG.getMessage(),CommCode.IDENTITY_WRITEPNG_SIGN_INFO_WRONG.getCode());
                }
                 result = data.getValid();
                if (result){
//                   R.ok(data.getValid() + "", data.getMessage());
                    result = data.getValid();

                    log.info("正常返回:jzqUserValidSignatureVo.getSuccess()为true时 result--->{}",JSONObject.toJSONString(result));
                    return true;
                }else {
                    log.info("异常返回时:jzqUserValidSignatureVo.getSuccess()为false时 异常结束执行valid--->{}",JSONObject.toJSONString(result));
//                    R.fail(data.getValid(), Integer.parseInt(data.getCode()), data.getMessage());
                    throw new ServiceException(data.getMessage(),CommCode.IDENTITY_WRITEPNG_SIGN_INFO_WRONG.getCode());
                }

            }else {
//               R.fail(jzqUserValidSignatureVo.getData() + "", CommCode.IDENTITY_CARD_SIGN_WRONG.getCode(), jzqUserValidSignatureVo.getMsg());
//                result = jzqUserValidSignatureVo.getSuccess();
                log.info("jzqUserValidSignatureVo.getSuccess()为false时，--->{}",JSONObject.toJSONString(result));
                throw new ServiceException(jzqUserValidSignatureVo.getMsg(),CommCode.IDENTITY_WRITEPNG_SIGN_INFO_WRONG.getCode());
                // TODO 上线时放开
            }
        }
        // TODO 放开校验
//        result = true;
        return result;
    }


    public Map<String, Object> getCommonValidPostParams(GentlemanSaltingVo gentlemanSaltingVo) {
        Map<String, Object> params = new HashMap<>();
        params.put("ts",gentlemanSaltingVo.getTs());
        params.put("nonce",gentlemanSaltingVo.getNonce());
        params.put("sign",gentlemanSaltingVo.getSign());
        return params;
    }

    public JSONArray putParameter(AutoSignatureInputBO autoSignatureInputBO) {
        // 查询
        JSONArray signatories = new JSONArray();
        SignatoryReq sReq= new SignatoryReq();
        signatories.add(sReq);
        sReq.setFullName(autoSignatureInputBO.getProductName()); //姓名
        sReq.setIdentityType(autoSignatureInputBO.getIdentityType1()); //证件类型
        sReq.setIdentityCard(autoSignatureInputBO.getIdentityCard1());//身份证号
        sReq.setMobile(autoSignatureInputBO.getProductMobile()); //手机号
//        sReq.setChapteJson("[{\"page\":0,\"chaptes\":[{\"offsetX\":0.3047,\"offsetY\":0.2677}]},{\"page\":4,\"chaptes\":[{\"offsetX\":0.2378,\"offsetY\":0.1681}]}]");//坐标（X Y）定位签字位置
        sReq.setChapteJson(autoSignatureInputBO.getChapteJsonFirst());
        sReq.setOrderNum(1);
        // sReq.setSearchKey("服务组件");//关键字定位签字位置
        sReq.setNoNeedVerify(1); //取消签约前短信校验
        sReq.setServerCaAuto(1);//是否使用自动签署完成，0或null不使用，1自动(当且只当合同处理方式为部份自动或收集批量签时有效)
        sReq.setSignLevel(1);//签字类型，标准图形章或公章:0标准图形章,1公章或手写,2公章手写或手写,3个人方形标准章（用户类型是个人且姓名2-4个字符生效，其他情况默认使用系统标准图形章）
        log.info("封装结束参数为{}", JSONObject.toJSONString(signatories));
        return signatories;
    }

    public Map<String, Object> getAuthPostParams(GentlemanSaltingVo gentlemanSaltingVo) {
        Map<String, Object> params = getCommonValidPostParams(gentlemanSaltingVo);
        String url= SERVICE_URL+"/v2/sign/applySign";
//        params.put("ts",gentlemanSaltingVo.getTs());
        params.put("app_key",APP_KEY);
        params.put("encry_method","md5");
//        params.put("nonce",gentlemanSaltingVo.getNonce());
//        params.put("sign",gentlemanSaltingVo.getSign());
        params.put("serverCa",1); //使用云证书
        // params.put("fileSuffix",1);//0或null默认.pdf; 1 ofd;2 word文件（传ofd和word文件发起合同时该参数必传）
        params.put("fileType",0); //指定合同发起方式（0上传PDF或者ofd文件;1 url地址下载;2 tmpl模版生成;3 html文件上传）
        //  params.put("attachFiles",new File("d:\\附件.png"));
        params.put("dealType",1); //指定合同文件签署方式（5为部分自动签，0手动签，1自动签，2只报全，6 HASH只保全，17收集信息批量签）
        params.put("positionType",0); //指定签字位置类型（0签字坐标，1表单域，2关键字）
        params.put("addPage",1);//ofd文件追加内容，noEbqSign需要设置为2
        params.put("noEbqSign",2);//不要保全章
        params.put("orderFlag",1);
        return params;
    }

    public GentlemanSaltingVo getAuthSafety() {
        long ts = System.currentTimeMillis();
        String nonce= DigestUtils.md5Hex(System.currentTimeMillis()+"");
        String signSrc="nonce"+nonce+"ts"+ts+"app_key"+APP_KEY+"app_secret"+APP_SECRET;
        String sign=DigestUtils.md5Hex(signSrc);
        return GentlemanSaltingVo.builder()
                                 .ts(ts)
                                 .sign(sign)
                                 .nonce(nonce)
                                 .build();
    }

    @Override
    public R<String> getStartGentlemanSignatureLink(AutoSignatureGetLinkInputBO autoSignatureGetLinkInputBO) {
        long ts=System.currentTimeMillis();
        System.out.println(ts);
        String nonce= DigestUtils.md5Hex(System.currentTimeMillis()+"");
        System.out.println(nonce);
        String signSrc="nonce"+nonce+"ts"+ts+"app_key"+APP_KEY+"app_secret"+APP_SECRET;
        String sign=DigestUtils.md5Hex(signSrc);
        System.out.println(sign);

//        RequestUtils requestUtils = HttpClientUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
       //构建请求参数
        Map<String,Object> params=new HashMap<>();
        params.put("applyNo",autoSignatureGetLinkInputBO.getApplyNo()); //TODO *
        params.put("fullName",autoSignatureGetLinkInputBO.getFullName());
        params.put("identityCard",autoSignatureGetLinkInputBO.getIdentityCard());
        params.put("identityType",autoSignatureGetLinkInputBO.getIdentityType());
        params.put("sign",sign);
        params.put("ts",ts);
        params.put("nonce",nonce);
        params.put("app_key","924410c408aea504");
        params.put("encry_method","md5");
        System.out.println(JSONObject.toJSONString(params));
        String url = SERVICE_URL + "/v2/sign/link";
        String ri= HttpClientUtils.init().getPost(url,null,params,true);
//        ResultInfo<String> ri= requestUtils.doPost("/v2/sign/link",params);
//        System.out.println(JSONObject.toJSONString(ri.getMsg()));
//        System.out.println(ri.toString());
//        AutoSignatureGetLinkOutBO autoSignatureGetLinkOutBO = JSON.parseObject(ri, AutoSignatureGetLinkOutBO.class);
        return R.ok(ri);
    }

    @Override
    public R continueStartGenManSignature(ContinueStartSignatureInputBO continueStartSignatureInputBO) {
        Integer isArchive = 0 ;
        long ts=System.currentTimeMillis();
        System.out.println(ts);
        String nonce= DigestUtils.md5Hex(System.currentTimeMillis()+"");
        System.out.println(nonce);
        String sign;
        String signSrc="nonce"+nonce+"ts"+ts+"app_key"+APP_KEY+"app_secret"+APP_SECRET;
        sign=DigestUtils.md5Hex(signSrc);
        System.out.println(sign);

        Map<String, Object>  params= new HashMap<>();
        String url=SERVICE_URL+"/v2/sign/signatory/add";
        params.put("applyNo",continueStartSignatureInputBO.getApplyNo());
        params.put("isArchive",continueStartSignatureInputBO.getIsArchive());//是否归档，0不归档，1归档，默认值1
        params.put("sign",sign);
        params.put("ts",ts);
        params.put("orderNum",2);
        params.put("nonce",nonce);
        params.put("app_key","924410c408aea504");
        params.put("encry_method","md5");


        /**
         * 签约方（个人）
         */
        JSONArray signatories=new JSONArray();
        SignatoryReq sReq=new SignatoryReq();
        sReq.setFullName(continueStartSignatureInputBO.getFullName());          //姓名
        sReq.setIdentityType(continueStartSignatureInputBO.getIdentityType());  //证件类型
        sReq.setIdentityCard(continueStartSignatureInputBO.getIdentityCard());//身份证号
        sReq.setMobile(continueStartSignatureInputBO.getMobile()); //手机号
        sReq.setChapteJson(continueStartSignatureInputBO.getChapteJson());
        sReq.setSignLevel(1);//签字类型，标准图形章或公章:0标准图形章,1公章或手写,2公章手写或手写,3个人方形标准章（用户类型是个人且姓名2-4个字符生效，其他情况默认使用系统标准图形章）
        sReq.setServerCaAuto(1);//0手动签；1自动签
        sReq.setNoNeedVerify(1); //取消签约前短信校验
        sReq.setServerCaAuto(0);//是否使用自动签署完成，0或null不使用，1自动(当且只当合同处理方式为部份自动或收集批量签时有效)
        sReq.setSignLevel(1);//签字类型，标准图形章或公章:0标准图形章,1公章或手写,2公章手写或手写,3个人方形标准章（用户类型是个人且姓名2-4个字符生效，其他情况默认使用系统标准图形章）

        signatories.add(sReq);
        params.put("signatories",signatories.toJSONString());
        System.out.println(signatories.toJSONString());
        String str= HttpClientUtils.init().getPost(url,null,params,true);
        System.out.println(str);
        return R.ok(str);
    }

    @Override
    public R authUserValid(AutoSignatureUserInputBO autoSignatureUserInputBO) {
        //安全认证->接口加盐处理
        GentlemanSaltingVo gentlemanSaltingVo = getAuthSafety();
        boolean result = personValid(gentlemanSaltingVo, autoSignatureUserInputBO.getFullName(), autoSignatureUserInputBO.getIdentityCard());
        if (!result){
            throw new ServiceException(CommCode.IDENTITY_CARD_SIGN_WRONG.getMessage(),CommCode.IDENTITY_CARD_SIGN_WRONG.getCode());
        }
        //查询当前账号是不是第一次签名，如果是就添加   不是就修改
        log.info("执行结果{}",result);
        return R.ok(result,"客户实名认证");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> turnOffSignature(SignatureTurnOffSignInputBO signatureTurnOffSignInputBO) {
        String status = signatureTurnOffSignInputBO.getStatus();
        String bussinessNo = signatureTurnOffSignInputBO.getBussinessNo();
        Boolean result = false;
        try {
            RedisLockUtils.lock(bussinessNo);
            FlowInfoDto bybussinessNo = flowInfoRepository.getBybussinessNo(bussinessNo);
            Integer nodeNum = bybussinessNo.getNodeNum();
            int i = Integer.parseInt(status);
            if (!(3 == nodeNum)){
                throw new ServiceException("该订单实际流程订单与实际不符，请重新加载页面",CommCode.DATA_IS_WRONG.getCode());
            }
            HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoRepository.queryDeliverCarConfirmInfo(bussinessNo);
            if ("1".equals(handoverCarCheckInfo.getSignType()+"")){
                throw new ServiceException("该订单已发起线下签署，请重新加载页面",CommCode.DATA_IS_WRONG.getCode());
            }
            String uiid = signatureTurnOffSignInputBO.getId();
            Long id = handoverCarCheckInfo.getId();
            HandoverCarCheckInfo handoverCarInfor = new HandoverCarCheckInfo();
            BeanUtil.copyProperties(handoverCarCheckInfo,handoverCarInfor);
            handoverCarInfor.setBussinessNo(bussinessNo);
            handoverCarInfor.setSignType(1);
            handoverCarInfor.setConfirmUserName("无");
            handoverCarInfor.setConfirmUserPhone("无");
            handoverCarInfor.setOrderType(1);
            handoverCarInfor.setSignStatus(1);
            handoverCarInfor.setId(id);
            handoverCarInfor.setDelFlag(0);
            handoverCarInfor.setVersion(0);
            if (StringUtils.isNotEmpty(uiid)){
                handoverCarInfor.setUpdateTime(new Date());
            }else {
                handoverCarInfor.setCreateTime(new Date());
            }
            result = handoverCarCheckInfoRepository.saveOrUpdateHandoverCarInfo(handoverCarInfor);
            if (!result){
                throw new ServiceException("该订单发起线下签署状态修改失败，请检查是否签署",CommCode.DATA_IS_WRONG.getCode());
            }

//            FlowInfoDto flow = new FlowInfoDto();
//            flow.setBussinessNo(bussinessNo);
//            flow.setNodeNum(FlowNodeNum.HAND_OVER_CAR_GUIDE.getCode());
//            result = flowInfoRepository.updateDeliverCarReadyToConfirm(flow);
//            if (!result){
//                throw new ServiceException("流程接口扭转失败",500);
//            }
        } finally {
            RedisLockUtils.unlock(bussinessNo);
        }
        return R.ok(result,"发起线下签署成功");
    }

    @Override
    public R authUserSignatureValid(AuthUserSignatureInputBO authUserSignatureInputBO) {
        log.info("进入方法method:authUserSignatureValid{}",JSON.toJSONString(authUserSignatureInputBO));
        String username = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(username)){
            return R.fail(false,"请求令牌身份信息不合法");
        }
        String account = username;
        Boolean result = false;
        authUserSignatureInputBO.setAccount(username);
        String bussinessNo = authUserSignatureInputBO.getBussinessNo();
        try {
            RedisLockUtils.lock(bussinessNo);
            //安全认证->接口加盐处理
            GentlemanSaltingVo gentlemanSaltingVo = getAuthSafety();
            result = personValid(gentlemanSaltingVo, authUserSignatureInputBO.getProductName(), authUserSignatureInputBO.getIdentityCard1());
            if (!result){
                throw new ServiceException(CommCode.IDENTITY_CARD_SIGN_WRONG.getMessage(),CommCode.IDENTITY_CARD_SIGN_WRONG.getCode());
            }

            GpadIdentityAuthInfo gpadIdentityAuthInfo = gpadIdentityAuthInfoRepository.selectByAccount(account);
            if (ObjectUtil.isEmpty(gpadIdentityAuthInfo)){
                //查询当前账号是不是第一次签名，如果是就添加   不是就修改
                result = gpadIdentityAuthInfoRepository.saveAuthUserSignatureValid(authUserSignatureInputBO);
                if (!result){
                    throw new ServiceException(CommCode.DATA_UPDATE_WRONG.getMessage(),CommCode.DATA_UPDATE_WRONG.getCode());
                }
            }

            // TODO 上线时打开
        } finally {
            RedisLockUtils.unlock(bussinessNo);
        }

        return R.ok(result,"二要素验证成功");
    }

    @Override
    public Boolean signatureUploadFile(AuthUserSignatureInputBO authUserSignatureInputBO,MultipartFile fileProductPng,String uploadPath) {
        log.info("进入方法上传图片方法 method:signatureUploadFile{}，路径为{}",JSON.toJSONString(authUserSignatureInputBO),uploadPath);
        String username = SecurityUtils.getUsername();
        String account = username;
        Boolean result = false;
        String filePngSuffix = ".png";
        File localProductPng = null;
        GentlemanSaltingVo gentlemanSaltingVo = getAuthSafety();
        try {
            if (null != fileProductPng) {
                localProductPng = transferToFile(fileProductPng, filePngSuffix);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        GpadIdentityAuthInfo gpadIdentityAuthInfo = gpadIdentityAuthInfoRepository.selectByAccount(account);
        //上传到君子签 产品专家
        String identityCard1 = gpadIdentityAuthInfo.getIdentityCard();
        log.info("上传身份信息 identityCard1{}",identityCard1);
        result = uploadMemorySignPath(gentlemanSaltingVo, localProductPng, identityCard1);
        if (!result) {
            throw new ServiceException(CommCode.UPLOAD_SIGN_PNG_WRONG.getMessage(), CommCode.UPLOAD_SIGN_PNG_WRONG.getCode());
        }


        if (ObjectUtil.isNotEmpty(gpadIdentityAuthInfo)){
            authUserSignatureInputBO.setId(gpadIdentityAuthInfo.getId()+"");
            authUserSignatureInputBO.setMemorySignPath(uploadPath);

            log.info("上传身份信息 identityCard1{}",JSON.toJSONString(authUserSignatureInputBO));
            AuthUserSignatureInputBO autoSignature = new AuthUserSignatureInputBO();
            autoSignature.setMemorySignPath(uploadPath);
            autoSignature.setIdentityCard1(gpadIdentityAuthInfo.getIdentityCard());
            autoSignature.setIdentityType1(1);
            autoSignature.setProductMobile(gpadIdentityAuthInfo.getProductMobile());
            autoSignature.setProductName(gpadIdentityAuthInfo.getProductName())     ;
            autoSignature.setId(gpadIdentityAuthInfo.getId()+"");
            log.info("上传身份信息 identityCard1{}",JSON.toJSONString(autoSignature));
            result = gpadIdentityAuthInfoRepository.updateAuthProductSignaturePath(autoSignature,gpadIdentityAuthInfo);
            if (!result){
                throw  new ServiceException(CommCode.UPLOAD_SIGN_PNG_WRONG.getMessage(),CommCode.UPLOAD_SIGN_PNG_WRONG.getCode());
            }
        }
        log.info("执行结束 method:signatureUploadFile{}",JSON.toJSONString(authUserSignatureInputBO));
      return result;
    }

    public static void main(String[] args) {
//        RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
////构建请求参数
//        Map<String,Object> params=new HashMap<>();
//        params.put("emailOrMobile","gqcq2@bccto.me");
//        ResultInfo<JSONObject> ri= requestUtils.doPost("/v2/user/organizationAuditStatus",params);
//        log.info("请求结果:"+ JSONObject.toJSONString(ri));
    }
    @Override
    public R filtOUTSteam(String apl,String bussinessNo) {
        int i = 0;
        List<UploadFileOutputDto> list = new ArrayList<>();
        RequestUtils requestUtils = RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
        //构建请求参数
        Map<String,Object> params =new HashMap<>();
        params.put("applyNo",apl); //TODO *
//        params.put("applyNo","apl"); //TODO *
        String data;
        do {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ResultInfo<String> ri= requestUtils.doPost("/v2/sign/linkFile",params);
            log.info("打印返回地址== {}", ri);
            data = ri.getData();
            log.info("打印调了多少次== {}", i);
            i++;
        } while (StringUtils.isEmpty(data) && i <=10);

        log.info("打印当前地址PDF 生成地址== {}", data);
        HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoRepository.queryDeliverCarConfirmInfo(bussinessNo);
        log.info("handoverCarCheckInfo1--->>->>>{}",JSON.toJSONString(handoverCarCheckInfo));
        if (StringUtils.isNotEmpty(handoverCarCheckInfo.getContractLink())){
            log.info("进入PDF 转图片方法--->>->>>{}",handoverCarCheckInfo.getContractLink());
            try {

//            File file = UrltoFile(data);
                HttpURLConnection httpUrl = (HttpURLConnection) new URL(data).openConnection();
                httpUrl.connect();
                InputStream ins = httpUrl.getInputStream();
                //文件存储路径
                String filePath = FILE_PATH.concat(File.separator).concat(DateUtil.generateDateTimeStr()).concat(File.separator);
                log.info("文件上传!当前文件存储路径=== {}", filePath);
                String newFilename = UuidUtil.generateUuidWithDate() + "." + "pdf";
                FileUtil.uploadJzqPngFile(ins, filePath, newFilename);

                String result = filePath.concat(newFilename).replaceAll("\\\\", "/");
                String subResult = result.substring(4);
                UploadFileOutputDto uploadFileOutputDto = new UploadFileOutputDto();
                uploadFileOutputDto.setFileName(newFilename);
                uploadFileOutputDto.setFilePath(subResult);

                FileInfo fileInfo = new FileInfo();
                fileInfo.setDelFlag(0);
                fileInfo.setBussinessNo(bussinessNo);
                fileInfo.setCreateTime(new Date());
                fileInfo.setFileType(3);
                fileInfo.setLinkType(33);
                fileInfo.setFileName(newFilename);
                fileInfo.setFilePath(subResult);
                fileInfo.setOrderNum(0);
                fileInfo.setVersion(0);
                log.info("保存PDF--->>->>>{}",JSON.toJSONString(fileInfo));
                fileInfoRepository.save(fileInfo);
                log.info("文件上传!结束返回接口参数为 {}", JSON.toJSONString(uploadFileOutputDto));

                String fileName = UuidUtil.generateUuidWithDate() + "." + "png";
                //输入路径，输出路径
                String pngPath = FILE_PATH.concat(File.separator).concat(DateUtil.generateDateTimeStr()).concat(File.separator);

                File pathDir = new File(pngPath);
                if (!pathDir.exists()) {
                    pathDir.mkdirs();
                }
                File savedFile = new File(pngPath.concat(File.separator).concat(fileName));
//            File file = new File("D:\\usr\\gpadfilepath\\20231120.png");
//            System.out.println(file.getAbsolutePath());
                pdf2multiImage(result, savedFile.getAbsolutePath());

                list.add(uploadFileOutputDto);
                pngPath = filePath.concat(fileName).replaceAll("\\\\", "/");
                UploadFileOutputDto uploadFileOutputDto1 = new UploadFileOutputDto();
                String subPngPath = pngPath.substring(4);
                uploadFileOutputDto1.setFileName(fileName);
                uploadFileOutputDto1.setFilePath(subPngPath);
                list.add(uploadFileOutputDto1);

                FileInfo fileInfo1 = new FileInfo();
                fileInfo1.setDelFlag(0);
                fileInfo1.setBussinessNo(bussinessNo);
                fileInfo1.setCreateTime(new Date());
                fileInfo1.setFileType(1);
                fileInfo1.setLinkType(33);
                fileInfo1.setFileName(fileName);
                fileInfo1.setFilePath(subPngPath);
                fileInfo1.setOrderNum(0);
                fileInfo1.setVersion(0);
                log.info("保存png--->>->>>{}",JSON.toJSONString(fileInfo1));
                fileInfoRepository.save(fileInfo1);

            } catch (Exception e) {
                e.printStackTrace();
            }finally {

            }
        }
        log.info("PDF转图片结束 --->>->>>{}",JSON.toJSONString(list));
        return R.ok(list);
    }

    public File transferToFile(MultipartFile multipartFile,String suffix) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        System.out.println(originalFilename);
        String prefix = System.currentTimeMillis() + "";
//        if (!StringUtils.isEmpty(originalFilename)){
//            prefix = originalFilename.substring(0, originalFilename.lastIndexOf("."));
//            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        }
        File file = File.createTempFile(prefix, suffix);
        multipartFile.transferTo(file);
        return file;
    }
    /**
     * 将PDF按页数每页转换成一个jpg图片
     * @param filePath
     * @return
     */
    public static List<String> pdfToImagePath(String filePath){
        List<String> list = new ArrayList<>();
        String fileDirectory = filePath.substring(0,filePath.lastIndexOf("."));//获取去除后缀的文件路径

        String imagePath;
        File file = new File(filePath);
        try {
            File f = new File(fileDirectory);
            if(!f.exists()){
                f.mkdir();
            }
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for(int i=0; i<pageCount; i++){
                // 方式1,第二个参数是设置缩放比(即像素)
                // BufferedImage image = renderer.renderImageWithDPI(i, 296);
                // 方式2,第二个参数是设置缩放比(即像素)
                BufferedImage image = renderer.renderImage(i, 1.25f);  //第二个参数越大生成图片分辨率越高，转换时间也就越长
                imagePath = fileDirectory + "/"+i + ".jpg";
                ImageIO.write(image, "PNG", new File(imagePath));
                list.add(imagePath);
            }
            doc.close();              //关闭文件,不然该pdf文件会一直被占用。
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @Description pdf转成一张图片
     * @created 2019年4月19日 下午1:54:13
     * @param pdfFile
     * @param outpath
     */
    private static void pdf2multiImage(String pdfFile, String outpath) {
        try {
            InputStream is = new FileInputStream(pdfFile);
            PDDocument pdf = PDDocument.load(is);
            int actSize  = pdf.getNumberOfPages();
            List<BufferedImage> piclist = new ArrayList<BufferedImage>();
            for (int i = 0; i < actSize; i++) {
                BufferedImage  image = new PDFRenderer(pdf).renderImageWithDPI(i,130, ImageType.RGB);
                piclist.add(image);
            }
            yPic(piclist, outpath);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将宽度相同的图片，竖向追加在一起 ##注意：宽度必须相同
     * @param piclist  文件流数组
     * @param outPath  输出路径
     */
    public static void yPic(List<BufferedImage> piclist, String outPath) {// 纵向处理图片
        if (piclist == null || piclist.size() <= 0) {
            System.out.println("图片数组为空!");
            return;
        }
        try {
            int height = 0, // 总高度
                    width = 0, // 总宽度
                    _height = 0, // 临时的高度 , 或保存偏移高度
                    __height = 0, // 临时的高度，主要保存每个高度
                    picNum = piclist.size();// 图片的数量
            int[] heightArray = new int[picNum]; // 保存每个文件的高度
            BufferedImage buffer = null; // 保存图片流
            List<int[]> imgRGB = new ArrayList<int[]>(); // 保存所有的图片的RGB
            int[] _imgRGB; // 保存一张图片中的RGB数据
            for (int i = 0; i < picNum; i++) {
                buffer = piclist.get(i);
                heightArray[i] = _height = buffer.getHeight();// 图片高度
                if (i == 0) {
                    width = buffer.getWidth();// 图片宽度
                }
                height += _height; // 获取总高度
                _imgRGB = new int[width * _height];// 从图片中读取RGB
                _imgRGB = buffer.getRGB(0, 0, width, _height, _imgRGB, 0, width);
                imgRGB.add(_imgRGB);
            }
            _height = 0; // 设置偏移高度为0
            // 生成新图片
            BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < picNum; i++) {
                __height = heightArray[i];
                if (i != 0) _height += __height; // 计算偏移高度
                imageResult.setRGB(0, _height, width, __height, imgRGB.get(i), 0, width); // 写入流中
            }
            File outFile = new File(outPath);
            ImageIO.write(imageResult, "jpg", outFile);// 写图片
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//////        long ts = System.currentTimeMillis();
//////        String nonce= DigestUtils.md5Hex(System.currentTimeMillis()+"");
//////        String signSrc="nonce"+nonce+"ts"+ts+"app_key"+APP_KEY+"app_secret"+APP_SECRET;
//////        String sign=DigestUtils.md5Hex(signSrc);
//////        GentlemanSaltingVo build = GentlemanSaltingVo.builder()
//////                .ts(ts)
//////                .sign(sign)
//////                .nonce(nonce)
//////                .build();
//////        String url= SERVICE_URL+"/v2/sign/notify";
//////        System.out.println(ts+"-----"+nonce+"-----"+sign+"");
//////
////////        RequestUtils requestUtils = RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
////////构建请求参数
//////        Map<String,Object> params = new HashMap<>();
//////        params.put("applyNo","APL1706236248945348608"); //TODO +
////////params.put("bussinessNo","XXX"); //TODO +
//////        params.put("fullName","LF171625");
//////        params.put("identityCard","222401198904210332");
//////        params.put("identityType",1);
//////        params.put("signNotifyType",1); //默认为1
//////        params.put("ts",build.getTs());
//////        params.put("app_key",APP_KEY);
//////        params.put("encry_method","md5");
//////        params.put("nonce",build.getNonce());
//////        params.put("sign",build.getSign());
//////        String ri= HttpClientUtils.init().getPost(url,null,params,true);
//////        System.out.println(ri);
////////        ResultInfo<Void> ri= requestUtils.doPost("/v2/sign/notify",params);
//////        System.out.println(ri);
////        RequestUtils requestUtils = RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
////        //构建请求参数
////        Map<String,Object> params=new HashMap<>();
////        params.put("name","张现彬");//
////        params.put("identityCard","372522198405100972");//
////        ResultInfo<Void> ri= requestUtils.doPost("/v2/auth/userValid",params);
////        System.out.println(ri);
////        if (null != ri.getData()){
////            String data = ri.getData()+"";
////            System.out.println(data);
//
////            String s = JSONUtil.parseObj(data).get("valid") + "";
////            String code = JSONUtil.parseObj(data).get("code") + "";
////            System.out.println(code);
////            String message = JSONUtil.parseObj(data).get("message") + "";
////            System.out.println(message);
////            Boolean aBoolean = Boolean.valueOf(s);
////            Boolean x = s);
////            System.out.println(x);
////            System.out.println(data);
////       }
//////
////
////
////           //上传手写个人签
//////            RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
//////            //构建请求参数
//////            Map<String,Object> params=new HashMap<>();
//////            params.put("identityCard","222401198904210332");
//////            params.put("signImgFile",new FileBody(new File("D:\\广汽传祺pad，移动端\\10-07\\微信图片_20231010124505.png")));
//////            ResultInfo<Void> ri= requestUtils.doPost("/v2/user/uploadPersSign",params);
//////            ri.setSuccess(false);
//////        System.out.println(ri);
//////        String string = JSONObject.toJSONString(ri) ;
//////        String text = String.valueOf(ri);
//////        if (!StringUtils.isEmpty(string)){
//////            JSONObject jsonObject = JSON.parseObject(string);
//////            String success = jsonObject.getString("success");
//////            Boolean aBoolean = Boolean.valueOf(success);
//////            System.out.println(aBoolean);
//////        }
////
////       获取PDF下载文件
//        RequestUtils requestUtils = RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
//        //构建请求参数
//        Map<String,Object> params =new HashMap<>();
//        params.put("applyNo","APL1726926248741453824"); //TODO *
//        ResultInfo<String> ri= requestUtils.doPost("/v2/sign/linkFile",params);
//        String data = ri.getData();
//        System.out.println(data);
//
//        try {
//            File file = UrltoFile(data);
//            System.out.println(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        InputStream inputStreamFromUrl = getInputStreamFromUrl(data);
//
//        try {
//            HttpServletResponse res;
//            BufferedInputStream bis = null;
//            bis = new BufferedInputStream(inputStreamFromUrl);
//            System.out.println(data);
//            OutputStream outputStream = null;
//            outputStream = res.getOutputStream();
//            byte[] buffer = new byte[1024];
//            int i = bis.read(buffer);
//            while (i != -1) {
//                outputStream.write(buffer, 0, i);
//                i = bis.read(buffer);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        byte[] bytes = SERVICE_URL.getBytes();
//
////
 //       获取在线查看链接
//        RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
////构建请求参数
//        Map<String,Object> params=new HashMap<>();
//        params.put("applyNo","APL1725717778008657920"); //TODO *
//        ResultInfo<String> ri= requestUtils.doPost("/v2/sign/linkAnonyDetail",params);
//        System.out.println(ri);
//    }
//    public static InputStream getInputStreamFromUrl(String urlStr) {
//       InputStream inputStream=null;
//       try {
//        //url解码
//        URL url = new URL(java.net.URLDecoder.decode(urlStr, "UTF-8"));
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        //设置超时间为3秒
//        conn.setConnectTimeout(3 * 1000);
//        //防止屏蔽程序抓取而返回403错误
//        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//        //得到输入流
//        inputStream = conn.getInputStream();
//         } catch (IOException e) {
//        System.out.println(1);
//        }
//       return inputStream;
//    }
//
//
//    public static void downloadFile(String filePath, String fileName, HttpServletResponse res) {
//        log.info("下载文件--->>> fileFullPath = {} fileName = {}", filePath, fileName);
//
//        if (Strings.isEmpty(filePath) || Strings.isEmpty(fileName)) {
//            throw new ServiceException("文件路径或文件名不能为空!", StatusCode.PARAMETER_ILLEGAL.getValue());
//        }
//
//        InputStream inputStream = null;
//        BufferedInputStream bis = null;
//        OutputStream outputStream = null;
//
//        //设置返回文件的名字
//        try {
//            inputStream = new FileInputStream(filePath.concat(File.separator).concat(fileName));
//            res.setHeader("Content-Disposition",
//                    "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));
//            //设置返回值的类型
//            res.setHeader("content-type", "application/octet-stream");
//            bis = new BufferedInputStream(inputStream);
//            outputStream = res.getOutputStream();
//            byte[] buffer = new byte[1024];
//            int i = bis.read(buffer);
//            while (i != -1) {
//                outputStream.write(buffer, 0, i);
//                i = bis.read(buffer);
//            }
//        } catch (Exception e) {
//            log.error("下载文件出错! {}", e.getMessage());
//        } finally {
//            try {
//                if (null != outputStream) {
//                    outputStream.close();
//                }
//                if (null != inputStream) {
//                    inputStream.close();
//                }
//                if (null != bis) {
//                    bis.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//
////获取链接地址文件的byte数据
//public static byte[] getUrlFileData(String fileUrl) throws Exception
//{
//URL url = new URL(fileUrl);
//HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//httpConn.connect();
//InputStream cin = httpConn.getInputStream();
//ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//byte[] buffer = new byte[1024];
//int len = 0;
//while ((len = cin.read(buffer)) != -1) {
//outStream.write(buffer, 0, len);
//}
//cin.close();
//byte[] fileData = outStream.toByteArray();
//outStream.close();
//return fileData;
//}

    public File UrltoFile(String url) throws Exception {
        HttpURLConnection httpUrl = (HttpURLConnection) new URL(url).openConnection();
        httpUrl.connect();
        InputStream ins=httpUrl.getInputStream();
        ApplicationHome applicationHome = new ApplicationHome(this.getClass());
        // 保存目录位置根据项目需求可
//        String str = applicationHome.getDir().getParentFile().getParentFile().getAbsolutePath() + "\\src\\main\\resources\\static\\"+ System.currentTimeMillis();
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "xie");//System.getProperty("java.io.tmpdir")缓存
        if (file.exists()) {
            file.delete();//如果缓存中存在该文件就删除
        }
        OutputStream os = new FileOutputStream(file);
        int bytesRead;
        int len = 8192;
        byte[] buffer = new byte[len];
        while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;

    }
//
//

}
