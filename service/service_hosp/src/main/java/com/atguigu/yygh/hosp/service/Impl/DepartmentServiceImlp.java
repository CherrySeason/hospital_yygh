package com.atguigu.yygh.hosp.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DepartmentServiceImlp implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        //参数map集合转换对象
        String mapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(mapString, Department.class);
        //是否存在数据
        Department targetDepartment =
                departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        //如果存在 添加
        if (targetDepartment != null) {
            department.setUpdateTime(targetDepartment.getUpdateTime());
            department.setCreateTime(targetDepartment.getCreateTime());
            department.setIsDeleted(targetDepartment.getIsDeleted());
            departmentRepository.save(department);
        } else {
            //如果不存在 修改 0:未上线 1：已上线
            department.setUpdateTime(new Date());
            department.setCreateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        Example<Department> example = Example.of(department, matcher);
        return departmentRepository.findAll(example, pageable);
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        //物理删除
        if (null != department)
            departmentRepository.delete(department);
        //逻辑删除 如果要用的话需要改对应的find 没有开启自动检测is_deleted
//        department.setIsDeleted(1);
//        departmentRepository.save(department);
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list存放最终集合
        List<DepartmentVo> result = new ArrayList<>();
        //根据hoscode查询所有科室
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        List<Department> all = departmentRepository.findAll(example);//该医院所有科室

        //根据大科室编号分组 bigcode 获取里面的下级科室
        Map<String, List<Department>> departmentMap =
                all.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map
        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()) {
            //大科室编号
            String bigCode = entry.getKey();
            //对应全部数据
            List<Department> departmentList = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(departmentList.get(0).getBigname());
            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department1 : departmentList) {
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode(department1.getDepcode());
                departmentVo1.setDepname(department1.getDepname());
                children.add(departmentVo1);
            }
            //小科室集合放到大科室children里
            departmentVo.setChildren(children);
            result.add(departmentVo);
        }
        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        return department != null ? department.getDepname() : null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }
}
