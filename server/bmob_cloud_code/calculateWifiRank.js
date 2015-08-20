/**
 * @author buffer(17977034@qq.com)
 * @desc ��ʱץȡ�������ϸ��������ݣ�������Ӧ�����������ֱ����ıȽϼ򵥴ֱ�������Ҫд��������
 * 		�������£�
 *			���۱����۷�ֵ+rating
 *			�ղر�, +5
 * 			�ٱ���, -5,�ٱ����ͻ����Է�Ϊ������֤������۷�
 *			ʹ��һ��, +1
 *			ʹ��ʱ��, 1��Сʱ+1������һ��Сʱ��һ��Сʱ
 */
 
//---------------------------------------------------------------------------------------------------------------------
function onRequest(request, response, modules) {
  //��ȡ���ݿ����
  var db = modules.oData;
  //��ȡ�¼����󣬽���첽�ص�����
  var ep = modules.oEvent;

  var limitnum = 1000; //Ĭ����෵��1000����¼
  var skipNum = 0;

  ep.all('got_profile', 'got_comments', 'got_follow', 'got_report', function(profile, comments, follow, report) {
    var map_rank = new Array();
      
      //������֤WiFi����
    var objects_profile = JSON.parse(profile);
    for (var cursor in objects_profile) {
      var rows = objects_profile[cursor];
      for (var row in rows) {
        map_rank[rows[row].MacAddr] = 0;
      }
    }

      //������������
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
      
    //�����ղ�����
    var objects_follow = JSON.parse(follow);
    for (var cursor in objects_follow) {
      var rows = objects_follow[cursor];
      for (var row in rows) {
        if (map_rank[rows[row].MacAddr] == null)
          map_rank[rows[row].MacAddr] = 0;
        map_rank[rows[row].MacAddr] += 5;
      }
    }
        
    //����ٱ�����
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
 
      //ɾ���Ѿ����������
      map_rank_keys.splice(cur_idx, 1);
      map_rank_sort.push(current);
    }

    //���������ݲ�����
    var index = map_rank_sort.length;
    for (var cursor in map_rank_sort) {
      db.insert({
        "table":"WifiRankT",             //����
        "data":{"MacAddr" : map_rank_sort[cursor], "Score" : parseInt(map_rank[map_rank_sort[cursor]]), "Rank" : index--} 
      }, function(err, data) {         //�ص�����
          //if(err)  response.end("error is  " + err.code  + "error message is " + err.error );
      });
    }
    //response.end("Got Data Done, len: ");
  });
	
	//��ȡ��ע��Wi-Fi�б�
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
	
	//��ȡ���۱��е�������Ϣ
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
	
	//��ȡ�ղر���������Ϣ
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
    
  //��ȡ�ٱ�����������Ϣ
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