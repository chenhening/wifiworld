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
	var map_rank = {};
	db.find({
		"table":"WifiComments",
	}, function(err, data) {
		if (data == null)
			return;
			
		var objects = JSON.parse(data);
        for (var cursor in objects) {
            var rows = objects[cursor];
            for (var row in rows) {
                if (map_rank[rows[row].MacAddr] == null) {
                    map_rank[rows[row].MacAddr] = 0;
                }
                if (rows[row].Type == 0) {//����
                    map_rank[rows[row].MacAddr] += 5;
                } else if (rows[row].Type == 1) {
                    map_rank[rows[row].MacAddr] -= 5;
                } else {
                    map_rank[rows[row].MacAddr] += 5;
                }
            }
        }
        
        //��ȡ�ղر�
    	db.find({
    		"table":"WifiFollow",
    	}, function(err, data) {
    		if (data == null)
    			return;
    			
    		var objects = JSON.parse(data);
            for (var cursor in objects) {
                var rows = objects[cursor];
                for (var row in rows) {
                    if (map_rank[rows[row].MacAddr] == null) {
                        map_rank[rows[row].MacAddr] = 0;
                    }
                    map_rank[rows[row].MacAddr] += 5;
                }
            }
            
            //��ȡ�ٱ���
        	db.find({
        		"table":"WifiReport",
        	}, function(err, data) {
        		if (data == null)
        			return;
        			
        		var objects = JSON.parse(data);
                for (var cursor in objects) {
                    var rows = objects[cursor];
                    for (var row in rows) {
                        if (map_rank[rows[row].MacAddr] == null) {
                            map_rank[rows[row].MacAddr] = 0;
                        }
                        map_rank[rows[row].MacAddr] -= 5;
                    }
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
        	});
    	});
	});
}                                                                                                                                                                                                                                                