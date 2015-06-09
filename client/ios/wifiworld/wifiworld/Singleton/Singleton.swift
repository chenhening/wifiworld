//
//  Singleton.swift
//  wifiworld
//
//  Created by ChenJian on 15/5/25.
//  Copyright (c) 2015年 com.anynet. All rights reserved.
//

import UIKit

class Singleton: NSObject {
   
    
    struct Static {
        static var onceToken:dispatch_once_t = 0
        static var instance:Singleton! = nil
    }
    
    class var sharedInstance:Singleton {
        get {
            dispatch_once(&Static.onceToken) {
                Static.instance = Singleton()
            }
            return Static.instance
        }
    }
    
    
    
    
    class async
    {
        class func bg(block: dispatch_block_t) {
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), block)
        }
        
        class func main(block: dispatch_block_t) {
            dispatch_async(dispatch_get_main_queue(), block)
        }
    }
    
    class sync
    {
        class func bg(block: dispatch_block_t) {
            dispatch_sync(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), block)
        }
        
        class func main(block: dispatch_block_t) {
            if NSThread.isMainThread() {
                block()
            }
            else {
                dispatch_sync(dispatch_get_main_queue(), block)
            }
        }
    }
    
}
