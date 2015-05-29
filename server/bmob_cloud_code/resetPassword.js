/**
 * @file reset password for bmob cloud code
 * @author buffer(17977034@qq.com)
 */
 
function onRequest(request, response, modules) {
    var username = request.body.username; //手机号码也是用户名信息
    var pwd = request.body.password; //新密码
    var db = modules.oData;
    db.find({
        "table":"_User",
        "where":{"username":username},
    },function(err,data){
        if(data){
            var resultObject= JSON.parse(data);
            var objectId = resultObject.results[0].objectId;
            db.setHeader({"X-Bmob-Master-Key":"ce830840a8a2a805704c8e55e934d95a"});
            db.updateUserByObjectId({"objectId":objectId, data:{"password":pwd}},function(err,data){
                response.end(data);
            });
        }
    });
}