/**
 * @author buffer(17977034@qq.com)
 * @desc ��ʱץȡ�������ϸ��������ݣ�������Ӧ�����������ֱ����ıȽϼ򵥴ֱ�������Ҫд��������
 * 		�������£�
 *			���۱�������+5,������-5
 *			�ղر�, +5
 * 			�ٱ���, -5,�ٱ����ͻ����Է�Ϊ������֤������۷�
 *			ʹ��һ��, +1
 *			ʹ��ʱ��, 1��Сʱ+1������һ��Сʱ��һ��Сʱ
 */
 
function onRequest(request, response, modules) {
	//��ȡ���ݿ����
	var db = modules.oData;
	
	//��ȡ���۱�
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
  
  //��ȡ�ղر�
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
  
  //��ȡ�ٱ���
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
  
  //�ȴ����в�ѯ����ready
  while(!(comments_ready && follow_ready && report_ready)) {
    sleep(10);
  }
  
  //�������ݵ���
  var index = 0;
  for (var cursor in map_rank) {
    db.insert({
      "table":"WifiRank",             //����
      "data":{"MacAddr" : cursor, "Score" : map_rank[cursor], "Rank" : ++index} //��Ҫ���µ����ݣ���ʽΪJSON
    }, function(err, data) {         //�ص�����
      if(err)  response.end("error is  " + err.code  + "error message is " + err.error );
    });
  }
  response.end(cursor + " = " + map_rank[cursor]);
}                                                                                                                                                                                                                                                