/**
 * @author buffer(17977034@qq.com)
 * @desc 定时去WifiDynamic表遍历得到相关的次数时长数据并保存到一张新表中
 */

 //--------------------------------------------------------------------------------------------------------------------
function onRequest(request, response, modules) {
  //获取数据库对象
  var db = modules.oData;
  //eventproxy模块，解决异步回调的问题
  var ep = modules.oEvent;  
  
  var searchNum = 0;   //表的总行数，用sql语句获得
  var limitnum = 1000; //默认最多返回1000条记录
  var runcount = parseInt(searchNum/1000);
  var tablename = "WifiDynamic";
  
  var map_used = {};
  db.find({
    "table":tablename,
    "count":1
  },function(err,data1){      
    var resultObject = JSON.parse(data1);
    searchNum = resultObject.count;
    runcount = parseInt(searchNum/1000);
		
    //获取排行榜统计数据后对更新
    ep.after('got_data', runcount+1, function (list) {
      //插入数据到表
      for (var cursor in map_used) {
        db.insert({
          "table":"WifiUsed", //表名
          "data":{"MacAddr" : cursor, "Count" : map_used[cursor][0], "Time" : map_used[cursor][1]} 
        }, function(err, data) { //回调函数
           
        });
        
        //如果数据存在就只做更新
        db.find({
          "table":"WifiUsed", //表名
          "where":{"MacAddr" : cursor},
          "limit":1,            //limit大小，一页返回多少条记录，默认为0
        }, function(err, data) { //回调函数
          if (err) {
              response.end("error is  " + err.code  + "error message is " + err.error );
          } else {
            var objects = JSON.parse(data);
            for(var object in objects) {
              var rows = objects[object];
              for (var row in rows) {
                db.update({
                  "table":"WifiUsed", //表名
                  "objectId":rows[row].objectId,
                  "data":{"Count" : map_used[rows[row].MacAddr][0], "Time" : map_used[rows[row].MacAddr][1]} 
                }, function(err, data) { //回调函数
                    //if (err) response.end("error1 is  " + err.code  + "error message is " + err.error + \
                      " fuch :" + rows[row].objectId);
                    //else response.end("ok:" + rows[row].objectId);
                });
              }
            }
            //response.end("ok");
          }
        });
      }
    });
    
    //分多次获取记录，因为每次只能获取1000条
    for(var i=0; i<=runcount; i++) {
      var skipNum = 1000*i;
      if(i == runcount) {
        limitnum = searchNum - 1000;
      } else {
        limitnum = 1000;
      }
      
      db.find({
        "table":tablename,
        "limit":limitnum,            
        "skip":skipNum
      }, function(err, data) { 
        var objects = JSON.parse(data);
        //遍历这个Json对象
        for(var object in objects) {
          var rows = objects[object];
          for (var row in rows) {
            if (map_used[rows[row].MacAddr] == null) {
              map_used[rows[row].MacAddr] = new Array();
              map_used[rows[row].MacAddr][0] = 0; //表示连接次数
              map_used[rows[row].MacAddr][1] = 0; //表示连接时长
            }
            ++map_used[rows[row].MacAddr][0];
            var dura = rows[row].LogoutTime - rows[row].LoginTime;
            if (dura > 0) {
              map_used[rows[row].MacAddr][1] += dura;
            } else {
              map_used[rows[row].MacAddr][1] += 60*60*1000;
            }
          }
        }
        ep.emit('got_data', 1);        
      });
    }
  });
}                        