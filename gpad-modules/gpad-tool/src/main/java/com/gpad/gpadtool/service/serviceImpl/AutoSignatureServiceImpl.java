package com.gpad.gpadtool.service.serviceImpl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.bo.input.AuthUserSignatureInputBO;
import com.gpad.common.core.bo.input.AutoSignatureGetLinkInputBO;
import com.gpad.common.core.bo.input.AutoSignatureInputBO;
import com.gpad.common.core.bo.input.ContinueStartSignatureInputBO;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.common.core.vo.GentlemanSaltingVo;
import com.gpad.gpadtool.constant.CommCode;
import com.gpad.gpadtool.constant.FlowNodeNum;
import com.gpad.gpadtool.domain.dto.FileInfoDto;
import com.gpad.gpadtool.domain.dto.FlowInfoDto;
import com.gpad.gpadtool.domain.entity.GpadIdentityAuthInfo;
import com.gpad.gpadtool.domain.entity.HandoverCarCheckInfo;
import com.gpad.gpadtool.repository.FileInfoRepository;
import com.gpad.gpadtool.repository.FlowInfoRepository;
import com.gpad.gpadtool.repository.GpadIdentityAuthInfoRepository;
import com.gpad.gpadtool.repository.HandoverCarCheckInfoRepository;
import com.gpad.gpadtool.service.AutoSignatureService;
import com.gpad.gpadtool.service.GRTService;
import com.gpad.gpadtool.utils.RedisLockUtils;
import com.junziqian.sdk.bean.ResultInfo;
import com.junziqian.sdk.bean.req.sign.ext.SignatoryReq;
import com.junziqian.sdk.util.RequestUtils;
import com.junziqian.sdk.util.http.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
    private GRTService grtSservice;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> startGentlemanSignature(AutoSignatureInputBO autoSignatureInputBO, MultipartFile file,MultipartFile fileCustomerPng,MultipartFile fileProductPng) {
        String data = "";
        String bussinessNo = autoSignatureInputBO.getBussinessNo();
        try {
            RedisLockUtils.lock(bussinessNo);
            //TODO 查询数据
            GpadIdentityAuthInfo gpadIdentityAuthInfo = gpadIdentityAuthInfoRepository.checkMemorySign(autoSignatureInputBO);
            if(!ObjectUtil.isNotEmpty(gpadIdentityAuthInfo)){
                return R.fail(null, CommCode.UNKNOWN_REAL_NAME.getCode(),"请先通过实名认证");
            }

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
            Boolean result1 = gpadIdentityAuthInfoRepository.updateAuthUserSignature(authUserSignatureInputBO);
            if(!result1){
                throw  new ServiceException("产品专家认证失败，请检查身份证号",CommCode.DATA_UPDATE_WRONG.getCode());
            }

            //必填校验  TODO 判断文件是否为空
            if (null == file && null == fileCustomerPng){
                //TODO 上传到文件服务器 存储到数据库
                FileInfoDto fileInfoDto = new FileInfoDto();
                fileInfoDto.setFilePath(autoSignatureInputBO.getMemorySignPath());
                fileInfoDto.setBussinessNo(autoSignatureInputBO.getBussinessNo());
                fileInfoDto.setId(autoSignatureInputBO.getFileId());
                fileInfoDto.setFileType(1);
                fileInfoDto.setLinkType(31);
                Boolean result = fileInfoRepository.saveOrUpdateFileInfoDto(fileInfoDto);
                if (!result){
                    throw new ServiceException("文件数据入库失败",CommCode.DATA_UPDATE_WRONG.getCode());
                }
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

            R result = turnOnLineSignature(autoSignatureInputBO,gentlemanSaltingVo,file,fileCustomerPng,fileProductPng);
            if (!StringUtils.isNotEmpty(result.getData().toString())){
                return R.fail(null,CommCode.INTFR_OUTTER_INVOKE_ERROR.getCode(),"发起线上签失败");
            }

            RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
            //构建请求参数
            Map<String,Object> params=new HashMap<>();
            String apl = result.getData().toString();
            params.put("applyNo", apl); //TODO *
            ResultInfo<String> ri= requestUtils.doPost("/v2/sign/linkAnonyDetail",params);

            if (ObjectUtil.isNotEmpty(ri)){
                data = ri.getData();
            }

            Boolean res = handoverCarCheckInfoRepository.updatecontractInfoById(apl, data, autoSignatureInputBO);
            if (!res){
                throw new ServiceException("合同连接入库失败",CommCode.DATA_UPDATE_WRONG.getCode());
            }
        } finally {
            RedisLockUtils.unlock(bussinessNo);
        }
        return R.ok(data,"合同发起成功");
    }

    public R turnOnLineSignature(AutoSignatureInputBO autoSignatureInputBO,GentlemanSaltingVo gentlemanSaltingVo, MultipartFile file, MultipartFile fileCustomerPng,MultipartFile fileProductPng) {
        File localCustomerPng = null;
        //查询合同编号 是否存在
        String bussinessNo = autoSignatureInputBO.getBussinessNo();
        HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoRepository.queryDeliverCarConfirmInfo(bussinessNo);
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

        try {
            RedisLockUtils.lock(bussinessNo);
            if (!StringUtils.isNotEmpty(contractAplNo)){
                params = getAuthPostParams(gentlemanSaltingVo);
                //发起
                Integer isArchive = 0;

                //覆盖之前得签名 TODO //提取参数 默认调用 产品专家
                String identityCard1 = autoSignatureInputBO.getIdentityCard1();
                Boolean result = uploadMemorySignPath(gentlemanSaltingVo,localProductPng, identityCard1);
                if (!result){
                    throw new ServiceException("君子签名重传失败",500);
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
                params.put("signatories",signatories.toJSONString());
                log.info("封住结束参数为{}", JSONObject.toJSONString(signatories));
                //这里必须用new String，因为使用的是IdentityHashMap（为了多个file的同name上传）

                //开始调用外部接口
                String str= HttpClientUtils.init().getPost(url,null,params,true);
                log.info("封装结束参数为{}", JSONObject.toJSONString(str));
                if (false){
                    throw new ServiceException("君子签名发起失败",CommCode.INTFR_OUTTER_INVOKE_ERROR.getCode());
                }
                if (!StringUtils.isEmpty(str)){
                    apl = JSON.parseObject(str).getString("data");
                    System.out.println(apl);
                }
                // 数据入库 TODO 是否归档 在数据落库
                Boolean rel = handoverCarCheckInfoRepository.updatecontractInfoById(apl,null,autoSignatureInputBO);
                if (!rel){
                    throw new ServiceException("合同PDF入库失败",CommCode.DATA_UPDATE_WRONG.getCode());
                }
            }else {
                //覆盖之前得签名 TODO //提取参数 默认调用 产品专家
                if (null != fileCustomerPng){
                    Boolean result = uploadMemorySignPath(gentlemanSaltingVo,localCustomerPng,autoSignatureInputBO.getIdentityCard());
                    if (!result){
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
        return R.ok(apl);
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
        }
        return Boolean.valueOf(success);
    }

    public boolean personValid(GentlemanSaltingVo gentlemanSaltingVo,String name,String identityCard) {
        Boolean result = false;
        RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
        //构建请求参数
        Map<String,Object> params = getCommonValidPostParams(gentlemanSaltingVo);
        params.put("name",name);
        params.put("identityCard",identityCard);
        ResultInfo<Void> ri= requestUtils.doPost("/v2/auth/userValid",params);
        System.out.println(ri);
        String string = JSONObject.toJSONString(ri);
        String str = JSONObject.toJSONString(ri);
        if (!StringUtils.isEmpty(str)){
            result = Boolean.valueOf(JSON.parseObject(str).getString("success"));
            System.out.println(result);
        }
        //TODO 上线要放开
        return true;
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
    public R authUserValid(AutoSignatureInputBO autoSignatureInputBO) {
        //安全认证->接口加盐处理
        GentlemanSaltingVo gentlemanSaltingVo = getAuthSafety();
        boolean result = personValid(gentlemanSaltingVo, autoSignatureInputBO.getFullName(), autoSignatureInputBO.getIdentityCard());
        if (!result){
            throw new ServiceException("实名认证失败",500);
        }
        //查询当前账号是不是第一次签名，如果是就添加   不是就修改
        // TODO 上线时打开
        return R.ok(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> turnOffSignature(String status,String bussinessNo) {
        Boolean result = false;
        FlowInfoDto bybussinessNo = flowInfoRepository.getBybussinessNo(bussinessNo);
        Integer nodeNum = bybussinessNo.getNodeNum();
        int i = Integer.parseInt(status);
        if (i != nodeNum){
            throw new ServiceException("状态有误",500);
        }

        if (true){
            System.out.println("查询数据合同是否能归档：0:未归档 ,1：归档");
        }

        FlowInfoDto flow = new FlowInfoDto();
        flow.setBussinessNo(bussinessNo);
        flow.setNodeNum(FlowNodeNum.HAND_OVER_CAR_GUIDE.getCode());
        result = flowInfoRepository.updateDeliverCarReadyToConfirm(flow);
        if (!result){
            throw new ServiceException("流程接口扭转失败",500);
        }
        return R.ok(result);
    }

    @Override
    public R authUserSignatureValid(AuthUserSignatureInputBO authUserSignatureInputBO) {
        Boolean result = false;
        String bussinessNo = authUserSignatureInputBO.getBussinessNo();
        try {
            RedisLockUtils.lock(bussinessNo);
            //安全认证->接口加盐处理
            GentlemanSaltingVo gentlemanSaltingVo = getAuthSafety();
            result = personValid(gentlemanSaltingVo, authUserSignatureInputBO.getFullName(), authUserSignatureInputBO.getIdentityCard());
            if (!result){
                throw new ServiceException("实名认证失败",500);
            }
            //查询当前账号是不是第一次签名，如果是就添加   不是就修改
            result = gpadIdentityAuthInfoRepository.saveAuthUserSignatureValid(authUserSignatureInputBO);
            if (!result){
                throw new ServiceException(CommCode.DATA_UPDATE_WRONG.getMessage(),CommCode.DATA_UPDATE_WRONG.getCode());
            }
            // TODO 上线时打开
        } finally {
            RedisLockUtils.unlock(bussinessNo);
        }

        return R.ok(result,"二要素验证成功");
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

//    public static void main(String[] args) {
////        long ts = System.currentTimeMillis();
////        String nonce= DigestUtils.md5Hex(System.currentTimeMillis()+"");
////        String signSrc="nonce"+nonce+"ts"+ts+"app_key"+APP_KEY+"app_secret"+APP_SECRET;
////        String sign=DigestUtils.md5Hex(signSrc);
////        GentlemanSaltingVo build = GentlemanSaltingVo.builder()
////                .ts(ts)
////                .sign(sign)
////                .nonce(nonce)
////                .build();
////        String url= SERVICE_URL+"/v2/sign/notify";
////        System.out.println(ts+"-----"+nonce+"-----"+sign+"");
////
//////        RequestUtils requestUtils = RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
//////构建请求参数
////        Map<String,Object> params = new HashMap<>();
////        params.put("applyNo","APL1706236248945348608"); //TODO +
//////params.put("bussinessNo","XXX"); //TODO +
////        params.put("fullName","LF171625");
////        params.put("identityCard","222401198904210332");
////        params.put("identityType",1);
////        params.put("signNotifyType",1); //默认为1
////        params.put("ts",build.getTs());
////        params.put("app_key",APP_KEY);
////        params.put("encry_method","md5");
////        params.put("nonce",build.getNonce());
////        params.put("sign",build.getSign());
////        String ri= HttpClientUtils.init().getPost(url,null,params,true);
////        System.out.println(ri);
//////        ResultInfo<Void> ri= requestUtils.doPost("/v2/sign/notify",params);
////        System.out.println(ri);
////        RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
////        //构建请求参数
////        Map<String,Object> params=new HashMap<>();
////        params.put("name","张现彬");//
////        params.put("identityCard","372522198405100972");//
////        ResultInfo<Void> ri= requestUtils.doPost("/v2/auth/userValid",params);
////        if (null != ri.getData()){
////            String data = ri.getData()+"";
////            String s = JSONUtil.parseObj(data).get("valid") + "";
////            Boolean x = Boolean.valueOf(s);
////            System.out.println(
////                    x
////            );
////            System.out.println(data);
////        }
////
//
//
//           //上传手写个人签
////            RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
////            //构建请求参数
////            Map<String,Object> params=new HashMap<>();
////            params.put("identityCard","222401198904210332");
////            params.put("signImgFile",new FileBody(new File("D:\\广汽传祺pad，移动端\\10-07\\微信图片_20231010124505.png")));
////            ResultInfo<Void> ri= requestUtils.doPost("/v2/user/uploadPersSign",params);
////            ri.setSuccess(false);
////        System.out.println(ri);
////        String string = JSONObject.toJSONString(ri) ;
////        String text = String.valueOf(ri);
////        if (!StringUtils.isEmpty(string)){
////            JSONObject jsonObject = JSON.parseObject(string);
////            String success = jsonObject.getString("success");
////            Boolean aBoolean = Boolean.valueOf(success);
////            System.out.println(aBoolean);
////        }
//
////       获取PDF下载文件
//        RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
//        //构建请求参数
//        Map<String,Object> params=new HashMap<>();
//        params.put("applyNo","APL1717365084781039616"); //TODO *
//        ResultInfo<String> ri= requestUtils.doPost("/v2/sign/linkFile",params);
//        System.out.println(ri);
//
//        //获取在线查看链接
////        RequestUtils requestUtils=RequestUtils.init(SERVICE_URL,APP_KEY,APP_SECRET);//建议生成为spring bean
//////构建请求参数
////        Map<String,Object> params=new HashMap<>();
////        params.put("applyNo","APL1717365084781039616"); //TODO *
////        ResultInfo<String> ri= requestUtils.doPost("/v2/sign/linkAnonyDetail",params);
////        System.out.println(ri);
//    }
//


}
