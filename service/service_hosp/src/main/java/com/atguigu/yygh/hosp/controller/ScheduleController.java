package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
//@CrossOrigin
@Api(tags = "排班管理")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "查询排班规则数据")
    @GetMapping(value = "getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable Long page, @PathVariable Long limit,
                                  @PathVariable String hoscode, @PathVariable String depcode) {
        Map<String, Object> map = scheduleService.getScheduleRule(page, limit, hoscode, depcode);
        return Result.ok(map);
    }

    @ApiOperation(value = "排班详细信息")
    @GetMapping(value = "getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode, @PathVariable String depcode, @PathVariable String workDate) {
        List<Schedule> detailSchedule = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(detailSchedule);
    }

}
