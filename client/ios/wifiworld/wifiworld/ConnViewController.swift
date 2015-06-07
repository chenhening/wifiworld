//
//  ConnViewController.swift
//  wifiworld
//
//  Created by Jane on 15/6/4.
//  Copyright (c) 2015年 com.anynet. All rights reserved.
//

import UIKit

class ConnViewController: UIViewController,UITableViewDataSource,UITableViewDelegate {
    @IBOutlet weak var lb_CurrentWifi: UILabel!

    @IBOutlet weak var btn_CustomSwitch: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.initInterface();
    }

    func initInterface(){
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(named: "wifi_title_bg"), forBarMetrics: UIBarMetrics.Default)
        let statusView = UIView(frame: CGRectMake(0, -20, UIScreen.mainScreen().bounds.width, 20));
        statusView.backgroundColor = UIColor.whiteColor();
        self.navigationController?.navigationBar.addSubview(statusView);
        UIApplication.sharedApplication().setStatusBarStyle(UIStatusBarStyle.Default, animated: true);
    self.btn_CustomSwitch.addTarget(self, action: "clickLeftCustomSwitch:", forControlEvents: UIControlEvents.TouchUpInside)
    
    }
    
    func clickLeftCustomSwitch(button:UIButton!){
    
        if button.tag == 0{
            button.setBackgroundImage(UIImage(named: "wifi_switch_on"), forState: UIControlState.Normal);
            button.tag = 1;
        }else {
            button.tag = 0;
            button.setBackgroundImage(UIImage(named: "wifi_switch_off"), forState: UIControlState.Normal);

        }
    
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

    //MARK: - tableviewDelegate  
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        
        return 2;
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 3;
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        
        var cellIdentifier = "CustomCell";
        var bkgName = "";
        if indexPath.section == 0{
            if indexPath.row == 0{
                bkgName = "wifi_free_item0";
            }else if indexPath.row == 1{
                bkgName = "wifi_free_item1";
            }else if indexPath.row == 2{
                bkgName = "wifi_free_item2";
            }
            
        }else{
            bkgName = "wifi_lock_item0"
        }
        let cell = tableView.dequeueReusableCellWithIdentifier(cellIdentifier, forIndexPath: indexPath) as! UITableViewCell;
        let bkgV = UIImageView(image: UIImage(named: bkgName));
        bkgV.frame = CGRectMake(0, 0, tableView.frame.width, cell.frame.height);
        cell.layer.cornerRadius = 50;
        cell.backgroundView = bkgV;
        
        return cell;
    }

   
    
    func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let view  = UIView(frame: CGRectMake(0, 0, 100, 30));
        var imgName = "";
        var rect = CGRectZero;
        let label = UILabel(frame: CGRectMake(20, 20, 100, 20));
        if section == 0{
            label.text = "认证Wi-Fi"
            imgName = "wifi_free_title_icon";
            rect = CGRectMake(0, 20, 10, 20);
        }else{
            imgName = "wifi_lock_title_icon"
            label.text = "锁定Wi-Fi"
            rect = CGRectMake(0, 20, 15, 20);
        };
        let imgV = UIImageView(image: UIImage(named: imgName));
        imgV.frame = rect;
        view.addSubview(imgV);
        view.addSubview(label);
        return view;
    }
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        tableView.deselectRowAtIndexPath(indexPath, animated: true);
    }
}
