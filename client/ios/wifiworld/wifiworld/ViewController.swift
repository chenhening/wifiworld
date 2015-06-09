//
//  ViewController.swift
//  wifiworld
//
//  Created by ChenJian on 15/5/25.
//  Copyright (c) 2015年 com.anynet. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        println("hello world")
        let afNet = AFURLConnectionOperation(request: NSURLRequest(URL: NSURL(string: "http://www.baidu.com")!))
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be     .
    }

}

