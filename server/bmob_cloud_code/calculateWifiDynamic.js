/**
 * @author buffer(17977034@qq.com)
 * @desc 定时去WifiDynamic表遍历得到相关的次数时长数据并保存到一张新表中
 */

function onRequest(request, response, modules) {
    //获取数据库对象
    var db = modules.oData;

    //读取WifiDynamic表
    var map_used = {};
    db.find({
        "table":"WifiDynamic",
    }, function(err, data) {
        if (data == null)
            return;

        var objects = JSON.parse(data);
        for (var cursor in objects) {
            var rows = objects[cursor];
            for (var row in rows) {
				if (rows[row].MacAddr == null || rows[row].MacAddr = "0")
					continue;
                if (map_used[rows[row].MacAddr] == null) {
                    map_used[rows[row].MacAddr] = new Array();
					map_used[rows[row].MacAddr][0] = 0; //表示连接次数
					map_used[rows[row].MacAddr][1] = 0; //表示连接时长
                }
                ++map_used[rows[row].MacAddr][0];
				if (rows[row].LogoutTime != 0) {
					map_used[rows[row].MacAddr][1] += rows[row].LogoutTime - rows[row].LoginTime;
				} else {
					map_used[rows[row].MacAddr][1] += 60*60*1000;
				}
            }
        }
		
		//插入数据到表
		for (var cursor in map_used) {
			db.insert({
				"table":"WifiUsed", //表名
				"data":{"MacAddr" : cursor, "Count" : map_used[cursor][0], "Time" : map_used[cursor][1]} //需要更新的数据，格式为JSON
			}, function(err, data) { //回调函数
				 if(err)  response.end("error is  " + err.code  + "error message is " + err.error );
			});
		}
		response.end(cursor + " = " + map_used[cursor][0]);
    });
}