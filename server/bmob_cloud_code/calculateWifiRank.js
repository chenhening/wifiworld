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
  boolean comments_ready = false;
	var map_rank_comments = {};
	db.find({
		"table":"WifiComments",
	}, function(err, data) {
		if (data == null) {
			comments_ready = true;
      return;
    }
			
		var objects = JSON.parse(data);
    for (var cursor in objects) {
      var rows = objects[cursor];
      for (var row in rows) {
        map_rank_comments[rows[row].MacAddr] = rows[row].Rating;
      }
    }
    comments_ready = true;
	});
  
  //读取收藏表
  boolean follow_ready = false;
	var map_rank_follow = {};
  db.find({
    "table":"WifiFollow",
  }, function(err, data) {
    if (data == null) {
      follow_ready = true;
      return;
    }
      
    var objects = JSON.parse(data);
    for (var cursor in objects) {
      var rows = objects[cursor];
      for (var row in rows) {
        map_rank_follow[rows[row].MacAddr] += 5;
      }
    }
    follow_ready = true;
  });
  
  //读取举报表
  boolean report_ready = false;
	var map_rank_report = {};
  db.find({
    "table":"WifiReport",
  }, function(err, data) {
    if (data == null) {
      report_ready = true;
      return;
    }
      
    var objects = JSON.parse(data);
    for (var cursor in objects) {
      var rows = objects[cursor];
      for (var row in rows) {
        map_rank_report[rows[row].MacAddr] -= 5;
      }
    }
    report_ready = true;
  });
  
  //等待所有查询数据ready
  while(!(comments_ready && follow_ready && report_ready)) {
    sleep(10);
  }
  
  //插入数据到表
  var index = 0;
  for (var cursor in map_rank) {
    db.insert({
      "table":"WifiRank",             //表名
      "data":{"MacAddr" : cursor, "Score" : map_rank[cursor], "Rank" : ++index} //需要更新的数据，格式为JSON
    }, function(err, data) {         //回调函数
      if(err)  response.end("error is  " + err.code  + "error message is " + err.error );
    });
  }
  response.end(cursor + " = " + map_rank[cursor]);
}                                                                                                                                                                                                                                                