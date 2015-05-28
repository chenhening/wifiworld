/**
 * @author buffer(17977034@qq.com)
 * @desc 定时抓取服务器上各大表的数据，计算相应的排名，这种遍历的比较简单粗暴后期需要写单独服务
 * 		规则如下：
 *			评论表，好评论+5,差评论-5
 *			收藏表, +5
 * 			举报表, -5,举报类型还可以分为几种验证情况来扣分
 *			使用一次, +1
 *			使用时间, 1个小时+1，不满一个小时算一个小时
 */
 
function onRequest(request, response, modules) {
	//获取数据库对象
	var db = modules.oData;
	
	//读取评论表
	var map_rank = {};
	db.find({
		"table":"WifiComments",
	}, function(err, data) {
		if (data == null)
			return;
			
		var rows = JSON.parse(data);
		for (var cursor in rows) {
            if (!map_rank.contains(cursor.MacAddr))
                map_rank[cursor.Macaddr] = 0;
			if (cursor.Type == 0) {//好评
                map_rank[cursor.Macaddr] += 5;
			} else if (cursor.Type == 1) {
                map_rank[cursor.Macaddr] -= 5;
			}
		}
	});
	
	//读取收藏表
	db.find({
		"table":"WifiFollow",
	}, function(err, data) {
		if (data == null)
			return;
			
		var rows = JSON.parse(data);
		for (var cursor in rows) {
            if (!map_rank.contains(cursor.MacAddr))
                map_rank[cursor.Macaddr] = 0;
            map_rank[cursor.Macaddr] += 5;
		}
	});
	
	//读取举报表
	db.find({
		"table":"WifiReport",
	}, function(err, data) {
		if (data == null)
			return;
			
		var rows = JSON.parse(data);
		for (var cursor in rows) {
            if (!map_rank.contains(cursor.MacAddr))
                map_rank[cursor.Macaddr] = 0;
            map_rank[cursor.Macaddr] -= 5;
		}
	});

    response.end(map_rank.toString());
}