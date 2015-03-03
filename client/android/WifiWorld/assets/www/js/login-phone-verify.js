function getCode(){
    var tel = $("#mobile").val();//获取手机号码输入框值
    var reg = /^1[3|4|5|8][0-9]\d{4,8}$/;
    if(!reg.test(tel)){ //校验手机号码格式
        alert("请先输入您的正确手机号！");
        return false;
    }
    var paras = "o_tel="+tel;
    //jquery post方法同步提交
    //（提交地址；   data：返回值）
    $.post('<%=basePath%>mobile/sendCode?'+paras,function(data) {
        if(data!=null&&typeof(data)!="undefined"){
            var msg = data.msg;  //返回值为json格式
            if(msg!=null&&typeof(msg)!="undefined"&&msg=="SUCCESS"){
                get_code_time();  //发送成功则出发get_code_time（）函数
            }else{
                alert("短信验证码发送失败！请重新获取。");
            }
        }else{
            alert("短信验证码发送失败！请重新获取。");
        }
    },"json");
}

var wait = 120;
function get_code_time(){
    if(wait==0){
        $("#updateverify").removeAttr("disabled");//移除获取验证码按钮的disabled属性
        $("#updateverify").val("获取验证码");
        wait = 120;
    }else{
        $("#updateverify").attr("disabled", true);//设置获取验证码按钮为不可触发
        $("#updateverify").val("剩(" + wait + ")秒");
        wait--;
        setTimeout("get_code_time()", 1000);
    }
}