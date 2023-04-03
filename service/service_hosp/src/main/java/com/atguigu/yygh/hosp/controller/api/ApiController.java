package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(value = "医院管理接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "添加医院接口")
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        //获取信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        check(paramMap, hoscode);
        //转换logoData
        String logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData", logoData);

        //调用service
        hospitalService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation(value = "查询医院接口")
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        //获取信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        check(paramMap, hoscode);
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    private void check(Map<String, Object> paramMap, String hoscode) {
        //获取签名
        String hospSign = (String) paramMap.get("sign");
        //根据编码查询数据库 签名
//        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        String signKey = hospitalSetService.getSignKey(hoscode);
        //把查询出来的签名进行MD5加密
//        String signKeyMd5 = MD5.encrypt(signKey);
        if (!hospSign.equals(signKey)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    @ApiOperation(value = "上传科室接口")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        //获取信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        check(paramMap, hoscode);
        departmentService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation(value = "查询科室接口")
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request) {
        //获取信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");

        int page = StringUtils.isEmpty(paramMap.get("page")) ?
                1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ?
                1 : Integer.parseInt((String) paramMap.get("limit"));
        check(paramMap, hoscode);
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);
        Page<Department> pageModel = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "删除科室")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        //获取信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        check(paramMap, hoscode);
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    @ApiOperation(value = "排班上传")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        //获取信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        check(paramMap, hoscode);
        scheduleService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation(value = "排班查询")
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request) {
        //获取信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        check(paramMap, hoscode);
        int page = StringUtils.isEmpty(paramMap.get("page")) ?
                1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ?
                1 : Integer.parseInt((String) paramMap.get("limit"));
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        Page<Schedule> pageModel = scheduleService.findPageSchedule(page, limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "排班删除")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        //获取信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        check(paramMap, hoscode);
        String hosScheduleId = (String) paramMap.get("hosScheduleId");
        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }

}
