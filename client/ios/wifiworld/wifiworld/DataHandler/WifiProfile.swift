//
//  WLBRemoteDataQuery.swift
//  wifiworld
//
//  Created by book on 15/5/31.
//  Copyright (c) 2015年 com.anynet. All rights reserved.
//

import UIKit


typealias handleDataResult = ([AnyObject]?,NSError?)->Void

class WifiProfile:BmobObject {
    
    var isShared:Bool! = false;
    var MacAddr:String! = ""; // mac地址, 唯一键
    var Ssid:String! = ""; // wifi的ssid
    var Password:String! = ""; // wifi的密码，经过base64后保存
    var Sponser:String! = ""; // 绑定的用户账号，wifi提供者
    var Type:Int! = 0; // wifi的类型
    // public String encryptType; //wifi加密类型
    var Logo:[UInt8]!; // 用户自定义的logo信息
    
    var  Alias: String! = ""; // 用户自定义的wifi别名
    var  Geometry:BmobGeoPoint!; // WiFi的地理位置
    var  ExtAddress:String! = "";
    var  Banner:String! = ""; // wifi的展示页内容(TODO(binfei)还需要定义更多)
    var  Income:Float! = 0; // wifi 收入
    
    enum WifiType {
         case WIFI_SUPPLY_BY_UNKNOWN ; // 未知wifi
         case WIFI_SUPPLY_BY_PUBLIC  ; // 公共场所WiFi，如家，kfc
         case WIFI_SUPPLY_BY_BUSINESS; // 商户提供的WiFi
         case  WIFI_SUPPLY_BY_HOME   ; // 家庭提供wifi
    }
    
    func queryObject(geo:BmobGeoPoint!,radian:Double!,resultBlock:handleDataResult!)  {

        //var dataList = [AnyObject]();
        var query:BmobQuery = BmobQuery(className:"WifiProfile")
        query.whereKey("Geometry", nearGeoPoint: geo, withinRadians: radian)
        query.findObjectsInBackgroundWithBlock { (list, error) -> Void in
            
            resultBlock?(list,error);

            if error != nil{
                println("error",error.description)
            }else{
                //dataList = list;
                //resultBlock?(list,nil);
                //println("dataList=\(list)");

            }
        }
        //resultBlock?(dataList);
    }

}