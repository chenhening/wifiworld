/**
 * @author buffer(17977034@qq.com)
 * @desc 定时抓取服务器上各大表的数据，计算相应的排名，这种遍历的比较简单粗暴后期需要写单独服务
 * 		规则如下：
 *			评论表，评论分值+rating
 *			收藏表, +5
 * 			举报表, -5,举报类型还可以分为几种验证情况来扣分
 *			使用一次, +1
 *			使用时间, 1个小时+1，不满一个小时算一个小时
 */
 
//---------------------------------------------------------------------------------------------------------------------
function onRequest(request, response, modules) {
  //获取数据库对象
  var db = modules.oData;
  //获取事件对象，解决异步回调问题
  var ep = modules.oEvent;

  var limitnum = 1000; //默认最多返回1000条记录
  var skipNum = 0;

  ep.all('got_profile', 'got_comments', 'got_follow', 'got_report', function(profile, comments, follow, report) {
    var map_rank = new Array();
      
      //处理认证WiFi数据
    var objects_profile = JSON.parse(profile);
    for (var cursor in objects_profile) {
      var rows = objects_profile[cursor];
      for (var row in rows) {
        map_rank[rows[row].MacAddr] = 0;
      }
    }

      //处理评论数据
    var objects_comments = JSON.parse(comments);
    for (var cursor in objects_comments) {
      var rows = objects_comments[cursor];
      for (var row in rows) {
        if (map_rank[rows[row].MacAddr] == null)
          map_rank[rows[row].MacAddr] = 0;
        if (rows[row].Rating == null)
          rows[row].Rating = 0;
        map_rank[rows[row].MacAddr] += rows[row].Rating;
      }
    }
      
    //处理收藏数据
    var objects_follow = JSON.parse(follow);
    for (var cursor in objects_follow) {
      var rows = objects_follow[cursor];
      for (var row in rows) {
        if (map_rank[rows[row].MacAddr] == null)
          map_rank[rows[row].MacAddr] = 0;
        map_rank[rows[row].MacAddr] += 5;
      }
    }
        
    //处理举报数据
    var objects_report = JSON.parse(report);
    for (var cursor in objects_report) {
      var rows = objects_report[cursor];
      for (var row in rows) {
        if (map_rank[rows[row].MacAddr] == null)
          map_rank[rows[row].MacAddr] = 0;
        if (map_rank[rows[row].MacAddr] > 5)
          map_rank[rows[row].MacAddr] -= 5;
      }
    }
    
    var str = "";
    var map_rank_keys = Object.keys(map_rank);
    var map_rank_sort = new Array();
    for (var cursor1 in map_rank) {
      var current = map_rank_keys[0];
      var min = 9999;
      var cur_idx = 0;
      var index = 0;
      for (var cursor2 in map_rank_keys) {
        if (map_rank[map_rank_keys[cursor2]] < min) {
          current = map_rank_keys[cursor2];
          min = map_rank[map_rank_keys[cursor2]];
          cur_idx = index;
        }
        ++index;
      }
 
      //删除已经排序的数据
      map_rank_keys.splice(cur_idx, 1);
      map_rank_sort.push(current);
    }

    //将评分数据插入表格
    var index = map_rank_sort.length;
    for (var cursor in map_rank_sort) {
      db.insert({
        "table":"WifiRankT",             //表名
        "data":{"MacAddr" : map_rank_sort[cursor], "Score" : parseInt(map_rank[map_rank_sort[cursor]]), "Rank" : index--} 
      }, function(err, data) {         //回调函数
          //if(err)  response.end("error is  " + err.code  + "error message is " + err.error );
      });
    }
    //response.end("Got Data Done, len: ");
  });
	
	//获取已注册Wi-Fi列表
	db.find({
    "table":"WifiProfile",
    "keys":"MacAddr",
    "limit":limitnum,            
    "skip":skipNum
	}, function(err, data) {
    if (data == null) {
      return;
    }
    ep.emit('got_profile', data);
	});
	
	//获取评论表中的评分信息
	db.find({
		"table":"WifiComments",
    "limit":limitnum,            
    "skip":skipNum
	}, function(err, data) {
		if (data == null) {
      return;
    }
    ep.emit('got_comments', data);
	});
	
	//获取收藏表格的评分信息
  db.find({
    "table":"WifiFollow",
    "keys":"MacAddr",
    "limit":limitnum,            
    "skip":skipNum
  }, function(err, data) {
    if (data == null) {
      return;
    }
    ep.emit('got_follow', data);
  });
    
  //获取举报表格的评分信息
  db.find({
    "table":"WifiReport",
    "keys":"MacAddr",
    "limit":limitnum,            
    "skip":skipNum
  }, function(err, data) {
    if (data == null) {
      return;
    }
    ep.emit('got_report', data);
  });
}                                                                                                                                                                                                                                                                                                                                                                        