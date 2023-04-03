import request from '@/utils/request'

const api_name_hosp = '/admin/hosp/hospital'
const api_name_cmn = '/admin/cmn/dict'
const api_name_department = '/admin/hosp/department'

export default {
    //分页查询医院列表
    getHospList(current, limit, Obj) {//当前页，当前页显示多少条数据，查询对象
        return request({
            url: `${api_name_hosp}/list/${current}/${limit}`,
            method: 'get',
            data: Obj //使用json传递
        })
    },

    //根据dictCode查询id，再根据id查询子数据
    getByDictCode(dictCode) {
        return request({
            url: `${api_name_cmn}/findByDictCode/${dictCode}`,
            method: 'get'
        })
    },

    //根据id查询下级数据字典
    findByParentId(dictCode) {
        return request({
            url: `/admin/cmn/dict/findChildData/${dictCode}`,
        ethod: 'get'
        })
    },


  //通过id更改status上线或下线医院
    updateStatus(id, status) {
        return request({
            url: `${api_name_hosp}/updateStatus/${id}/${status}`,
            method: 'get'
        })
    },

    //通过id查看医院详情
    getHospDetail(id) {
        return request({
            url: `${api_name_hosp}/showHospDetail/${id}`,
            method: 'get'
        })
    },

    //根据hoscode获取所有科室列表
    getDeptByHoscode(hoscode) {
        return request({
            url: `${api_name_department}/getDeptList/${hoscode}`,
            method: 'get'
        })
    }
}
