import request from '@/utils/request'

const api_name = '/admin/cmn/dict'

export default {

    //查找集合数据
    getDictListById(id) {
        return request({
            url: `${api_name}/findChildData/${id}`,
            method: 'get'
        })
    },
}