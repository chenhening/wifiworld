//
//  CurrentWifiSSID.m
//  wifiworld
//
//  Created by Jane on 15/6/9.
//  Copyright (c) 2015年 com.anynet. All rights reserved.
//

#import "CurrentWifiSSID.h"
#import <SystemConfiguration/CaptiveNetwork.h>

@implementation CurrentWifiSSID

+ (id)fetchSSIDInfo {
    
    NSArray *ifs = (__bridge_transfer id)CNCopySupportedInterfaces();
    
    NSString* ssid = @"";
    for (NSString *ifnam in ifs) {
        id  info = (__bridge_transfer id)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifnam);
        
        ssid = [info objectForKey:@"SSID"];
        
        if (ssid) { break; }
        
    }
    return ssid;
}

@end
