import request from '@/utils/request'
import { postForm } from '@/utils/request'

// 线路管理API
export function getPageList(query) {
    return request({
        url: '/line/getSysLinePage',
        method: 'post',
        data: query
    })
}

export function getLine(id) {
    return request({
        url: '/line/getSysLineById',
        method: 'get',
        params: { id }
    })
}

export function addLine(data) {
    return request({
        url: '/line/saveSysLine',
        method: 'post',
        data
    })
}

export function updateLine(data) {
    return request({
        url: '/line/editSysLine',
        method: 'post',
        data
    })
}

export function deleteLine(ids) {
    return request({
        url: '/line/removeSysLine',
        method: 'get',
        params: ids
    })
}

export function changeLineStatus(params) {
    return request({
        url: '/line/changeSysLineStatus',
        method: 'get',
        params
    })
}

// 线路订单管理API
export function getOrderPageList(query) {
    return request({
        url: '/lineOrder/getSysLineOrderPage',
        method: 'post',
        data: query
    })
}

export function getLineOrder(id) {
    return request({
        url: '/lineOrder/getSysLineOrderById',
        method: 'get',
        params: { id }
    })
}

export function updateOrderState(params) {
    const { id, state } = params;

    // 直接构建URL，确保两个参数都以查询参数形式传递
    // 注意：此API方法目前已被组件中的axios直接调用替代
    return request({
        url: `/lineOrder/updateSysLineOrderState?id=${id}&state=${state}`,
        method: 'post'
    });
}

export function cancelOrder(params) {
    const { id } = params;

    // 直接构建URL
    // 注意：此API方法目前已被组件中的axios直接调用替代
    return request({
        url: `/lineOrder/cancelSysLineOrder?id=${id}`,
        method: 'post'
    });
}