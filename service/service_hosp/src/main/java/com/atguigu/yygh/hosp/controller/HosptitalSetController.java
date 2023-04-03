package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.common.utils.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.atguigu.yygh.hosp.service.HospitalSetService;

import java.util.List;
import java.util.Random;

//@CrossOrigin
@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HosptitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    @GetMapping("findAll")
    @ApiOperation(value = "获取所有医院设置")
    public Result findAllHospitalSet() {
        return Result.ok(hospitalSetService.list());
    }

    @DeleteMapping("{id}")
    @ApiOperation(value = "逻辑删除医院信息")
    public Result removeHospSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) return Result.ok();
        else return Result.fail();
    }

    @ApiOperation(value = "条件查询带分页")
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalQueryVo hospitalQueryVo) {
        Page<HospitalSet> page = new Page<>(current, limit);
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hosname = hospitalQueryVo.getHosname();
        String hoscode = hospitalQueryVo.getHoscode();
        if (!StringUtils.isEmpty(hosname))
            wrapper.like("hosname", hospitalQueryVo.getHosname());
        if (!StringUtils.isEmpty(hoscode))
            wrapper.eq("hoscode", hospitalQueryVo.getHoscode());
        Page<HospitalSet> pageHos = hospitalSetService.page(page, wrapper);
        return Result.ok(pageHos);
    }

    @ApiOperation(value = "保存医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        hospitalSet.setStatus(1);
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));

        boolean save = hospitalSetService.save(hospitalSet);
        if (save) return Result.ok();
        else return Result.fail();
    }

    @GetMapping("getHoapitalSet/{id}")
    @ApiOperation(value = "根据id获取医院设置信息")
    public Result getHospitalSet(@PathVariable long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    @PostMapping("updateHospitalSet")
    @ApiOperation(value = "根据id更新医院设置")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) return Result.ok();
        else return Result.fail();
    }

    @DeleteMapping("batchRemove")
    @ApiOperation(value = "批量删除医院设置")
    public Result batchRemovehospitalSet(@RequestBody List<Long> list) {
        hospitalSetService.removeByIds(list);
        return Result.ok();
    }

    @ApiOperation(value = "医院设置 锁定和解锁（是否锁定接口）")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id, @PathVariable Integer status) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    @ApiOperation(value = "发送前面密钥")
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        return Result.ok();
    }
}
