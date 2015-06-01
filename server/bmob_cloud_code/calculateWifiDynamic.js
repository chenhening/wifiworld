/**
 * @author buffer(17977034@qq.com)
 * @desc 定时去WifiDynamic表遍历得到相关的次数时长数据并保存到一张新表中
 */

function onRequest(request, response, modules) {
    //获取数据库对象
    var db = modules.oData;

    //读取WifiDynamic表
    var map_rank = {};
    db.find({
        "table":"WifiDynamic",
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
                if (rows[row].Type == 0) {//好评
                    map_rank[rows[row].MacAddr] += 5;
                } else if (rows[row].Type == 1) {
                    map_rank[rows[row].MacAddr] -= 5;
                } else {
                    map_rank[rows[row].MacAddr] += 5;
                }
            }
        }
    };
}