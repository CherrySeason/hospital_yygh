import request from '@/utils/request'

const api_name_schedule = '/admin/hosp/schedule'

export default {
    //查询排班规则
    getScheduleRule(page, limit, hoscode, depcode) {
        return request({
            url: `${api_name_schedule}/getScheduleRule/${page}/${limit}/${hoscode}/${depcode}`,
            method: 'get'
        })
    },

    //查询排班详情
    getScheduleDetail(hoscode, depcode, workDate) {
        return request({
            url: `${api_name_schedule}/getScheduleDetail/${hoscode}/${depcode}/${workDate}`,
            method: 'get'
        })
    },
}