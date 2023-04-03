package com.atguigu.yygh.user.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.type.YearTypeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.atguigu.yygh.user.utils.ConstantWxPropertiesUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "微信调用接口")
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("getLoginParam")
    @ApiOperation(value = "获取登录参数")
    @ResponseBody
    public Result genQrConnect(HttpSession session) throws UnsupportedEncodingException{
        Map<String, Object> map=new HashMap<>();
        String redirect = URLEncoder.encode(ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL, "utf-8");
        map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
        map.put("redirectUri", redirect);
        map.put("scope", "snsapi_login");
        map.put("state", ""+System.currentTimeMillis());
        return Result.ok(map);
    }

    @GetMapping("callback")
    @ApiOperation(value = "回调函数")
    public String callback(String code, String state){
        //获取授权临时票据
        System.out.println("微信授权服务器回调......");
        System.out.println("state=" + state);
        System.out.println("code=" + code);

        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(code)){
            System.out.println("非法回调请求");
            throw new YyghException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        //使用code和appid以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl=new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl=String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);
        String result=null;
        try{
            result= HttpClientUtils.get(accessTokenUrl);
//            System.out.println("accessInfo:"+result);
        }catch (Exception e){
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        System.out.println("使用code换取的access_token结果="+result);

        JSONObject resuleJson=JSONObject.parseObject(result);
        if (resuleJson.getString("errcode")!=null){
            System.out.println("获取access_token失败"+resuleJson.getString("errcode")+resuleJson.getString("errmsg"));
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        String access_token=resuleJson.getString("access_token");
        String openid=resuleJson.getString("openid");
        System.out.println(access_token);
        System.out.println(openid);

        //根据access_token获取微信用户的基本信息
        //1.先根据openid进行数据库查询 无则调用微信个人信息获取的接口 有则直接登录
        UserInfo userInfo=userInfoService.findByOpenid(openid);
        if (userInfo==null){
            //数据库不存在
            String baseUserUrl="https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl=String.format(baseUserUrl, access_token, openid);
            String resultInfo=null;
            try{
                resultInfo=HttpClientUtils.get(userInfoUrl);
            } catch (Exception e) {
                throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            System.out.println("使用access_token获取用户信息的结果="+resultInfo);
            JSONObject resultUserInfoJson=JSONObject.parseObject(resultInfo);
            if(resultUserInfoJson.getString("errcode")!=null){
                System.out.println("获取用户信息失败："+resultUserInfoJson.getString("errcode")+resultUserInfoJson.getString("errmsg"));
                throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            //解析用户信息
            String nickname=resultUserInfoJson.getString("nickname");
            String headimgurl=resultUserInfoJson.getString("headimgurl");

            userInfo=new UserInfo();
            userInfo.setStatus(1);
            userInfo.setOpenid(openid);
            userInfo.setNickName(nickname);
            userInfoService.save(userInfo);
        }

        //返回name和token
        Map<String,Object> map=new HashMap<>();
        String name=userInfo.getName();
        if(StringUtils.isEmpty(name)){
            name=userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)){
            name=userInfo.getPhone();
        }
        map.put("name", name);

        //判断userInfo是否有手机号，为空则返回openid 不为空则返回空
        if(StringUtils.isEmpty(userInfo.getPhone())){
            map.put("openid", userInfo.getOpenid());
        }else{
            map.put("openid", "");
        }

        String token= JwtHelper.createToken(userInfo.getId(),name);
        map.put("token",token);
        return "redirect:" +
                ConstantWxPropertiesUtils.YYGH_BASE_URL +
                "/weixin/callback?token="+map.get("token")+
                "&openid="+map.get("openid")+"&name="+
                URLEncoder.encode((String)map.get("name"));
    }
}
