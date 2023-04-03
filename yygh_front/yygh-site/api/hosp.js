import request from "@/utils/request";

const api_name_hosp = `/api/hosp/hospital`;

export default {
  //用户前台，分页查询所有医院
  getPageList(page, limit, searchObj) {
    return request({
      url: `${api_name_hosp}/findHospList/${page}/${limit}`,
      method: "get",
      params: searchObj,
    });
  },

  //根据hosname模糊查询医院
  getByHosname(hosname) {
    return request({
      url: `${api_name_hosp}/findByHosName/${hosname}`,
      method: "get",
    });
  },

  // 根据hoscode获取医院预约挂号详情
  showBookingRuleDetail(hoscode) {
    return request({
      url: `${api_name_hosp}/findHospDetail/${hoscode}`,
      method: "get",
    });
  },

  //根据hoscode获取科室列表
  findDepartment(hoscode) {
    return request({
      url: `${api_name_hosp}/department/${hoscode}`,
      method: "get",
    });
  },

  //获取排班可预约日期数据
  getBookingScheduleRule(page, limit, hoscode, depcode) {
    return request({
      url: `${api_name_hosp}/auth/getBookingScheduleRule/${page}/${limit}/${hoscode}/${depcode}`,
      method: "get",
    });
  },

  //获取排班数据
  findScheduleList(hoscode, depcode, workDate) {
    return request({
      url: `${api_name_hosp}/auth/findScheduleList/${hoscode}/${depcode}/${workDate}`,
      method: "get",
    });
  },

  getSchedule(scheduleId) {
    return request({
      url: `${api_name_hosp}/getSchedule/${scheduleId}`,
      method: "get",
    });
  },
};
