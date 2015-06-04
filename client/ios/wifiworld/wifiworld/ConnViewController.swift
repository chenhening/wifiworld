//
//  ConnViewController.swift
//  wifiworld
//
//  Created by Jane on 15/6/4.
//  Copyright (c) 2015年 com.anynet. All rights reserved.
//

import UIKit

class ConnViewController: UIViewController {

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

}
