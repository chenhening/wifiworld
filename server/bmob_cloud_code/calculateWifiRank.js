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
		        if (!map_rank.contains(cursor.MacAddr))
					map_rank[cursor.Macaddr] = 0;
				if (cursor.Type == 0) {//����
					map_rank[cursor.Macaddr] += 5;
				} else if (cursor.Type == 1) {
					map_rank[cursor.Macaddr] -= 5;
				}
		    }
		}
	});
	
	//��ȡ�ղر�
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
	
	//��ȡ�ٱ���
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