//
//  RecentViewController.swift
//  wifiworld
//
//  Created by ChenJian on 15/5/25.
//  Copyright (c) 2015年 com.anynet. All rights reserved.
//

import UIKit

class RecentViewController: UIViewController ,MAMapViewDelegate{

    var mapView:MAMapView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.mapView = MAMapView(frame: CGRectMake(0, 64, self.view.frame.width, self.view.frame.height-113));
        self.view.addSubview(self.mapView);
        self.mapView.delegate = self;
        self.mapView.showsUserLocation = true;
        self.mapView.zoomLevel = 19;
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
            }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func mapView(mapView: MAMapView!, didUpdateUserLocation userLocation: MAUserLocation!, updatingLocation: Bool) {
        if CLLocationCoordinate2DIsValid(userLocation.coordinate) && mapView.tag == 0{
            self.mapView.setCenterCoordinate(userLocation.coordinate , animated: true)
            mapView.tag = 1;
        }

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
