let httpUtilFormDate = axios.create({
   
})
httpUtilFormDate.interceptors.request.use(
    config => {
        layer.load(2);
        return config;
    }
)

httpUtilFormDate.interceptors.response.use(
    response => {
        //拦截响应，做统一处理
        console.log("============拦截器拦截输出==================")
        console.log(response.data)
        console.log("==========================================")
        layer.closeAll('loading');
        if (response.data.code !== 201)
            layer.msg(response.data.msg
                //     , {
                //     offset: 't',
                // }
            );
        if (response.data.code) {
            if (response.data.code !== 200 && response.data.code !== 201)
                return Promise.reject(response.data)
        }

        return response.data
    },
    //接口错误状态处理，也就是说无响应时的处理
    error => {
        layer.closeAll('loading');
        console.log("============拦截器拦截输出==================")
        console.log(error)
        console.log("==========================================")

        layer.msg("网络错误，请稍后再试"
            //     , {
            //     offset: 't',
            // }
        );
        // layer.msg("网络错误，请稍后再试")
        return Promise.reject(error) // 返回接口返回的错误信息
    })

export default httpUtilFormDate