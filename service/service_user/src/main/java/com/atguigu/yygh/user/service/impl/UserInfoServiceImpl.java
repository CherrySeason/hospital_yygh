package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.model.user.UserLoginRecord;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //从输入得到手机号和验证码
        String phone = loginVo.getPhone();
        String voCode = loginVo.getCode();
        //判断是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(voCode))
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);

        //验证 验证码是否一致
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (!voCode.equals(redisCode)){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //绑定手机号
        UserInfo userInfo=null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())){
            userInfo=this.findByOpenid(loginVo.getOpenid());
            if(null!=userInfo){
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            }else
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }

        //判断是否是第一次登录：数据库中是否有手机号
        if (null==userInfo) {
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }
        if (userInfo.getStatus() == 0)
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);

        //TODO 记录登录
//        UserLoginRecord userLoginRecord = new UserLoginRecord();
//        userLoginRecord.setUserId(userInfo.getId());
//        userLoginRecord.setIp(loginVo.getIp());
//        userLoginRecordMapper.insert(userLoginRecord);

        //返回token信息
        Map<String,Object> map=new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)){
            name=userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)){
            name=userInfo.getPhone();
        }
        map.put("name", name);
        //token生成
        map.put("token", JwtHelper.createToken(userInfo.getId(), name));
        return map;
    }

    @Override
    public UserInfo findByOpenid(String openid) {
        QueryWrapper<UserInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息
        userInfo.setName(userAuthVo.getName());
        //其他认证信息
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //信息更新
        baseMapper.updateById(userInfo);
    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        //userInfoQuery获取条件值
        String keyword = userInfoQueryVo.getKeyword();//用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus();//认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();//开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();//结束时间
        //条件值非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(keyword)){
            wrapper.like("name", keyword);
        }
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status", status);
        }
        if(!StringUtils.isEmpty(authStatus)){
            wrapper.eq("auth_status", authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            wrapper.gt("create_time", createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            wrapper.lt("create_time", createTimeEnd);
        }
        Page<UserInfo> userInfoPage = baseMapper.selectPage(pageParam, wrapper);
        userInfoPage.getRecords().forEach(this::packageUserInfo);
        return userInfoPage;
    }

    @Override
    public void lock(Long userid, Integer status) {
        if (status ==0 || status ==1){
            UserInfo userInfo=this.getById(userid);
            userInfo.setStatus(status);
            this.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String, Object> map = new HashMap<>();
        //根据id查用户信息
        UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
        map.put("userInfo", userInfo);
        //根据id查询就诊人信息
        List<Patient> patients = patientService.findAllUserId(userId);
        map.put("patientList", patients);
        return map;
    }

    @Override
    public void approval(Long userid, Integer authStatus) {
        if (authStatus==2 || authStatus==-1){
            UserInfo userInfo=baseMapper.selectById(userid);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //用户状态
        String status = userInfo.getStatus() ==0? "锁定":"正常";
        userInfo.getParam().put("statusString", status);
        return userInfo;
    }
}
