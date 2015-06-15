//
//  WifiDetailViewController.swift
//  wifiworld
//
//  Created by Jane on 15/6/15.
//  Copyright (c) 2015年 com.anynet. All rights reserved.
//

import UIKit

class WifiDetailViewController: UIViewController {

    @IBOutlet weak var btn_needle: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        
    }

    @IBAction func clickButtonNeedle(sender: AnyObject) {
        self.testSpeedAnnimation();
        
    }
    
    // CAAnimation for search
    func testSpeedAnnimation(){
        let animation = CABasicAnimation(keyPath: "transform.rotation.z");
        animation.fromValue = 0;
        animation.toValue = M_PI;
        animation.duration = 1;
        animation.cumulative = false;
        animation.autoreverses = false;
        animation.repeatCount = 1;
        animation.removedOnCompletion = false;
        animation.fillMode = kCAFillModeForwards;
        self.btn_needle.layer.addAnimation(animation, forKey: nil);
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
