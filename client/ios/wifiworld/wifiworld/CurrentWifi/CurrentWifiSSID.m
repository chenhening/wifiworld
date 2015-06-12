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
    NSDictionary* info;
    for (NSString *ifnam in ifs) {
        info = (__bridge_transfer id)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifnam);
        
        ssid = [info objectForKey:@"SSID"];
        NSLog(@"wifiInfo=%@",info);
        if (ssid) { break; }
        
    }
    return info;
}

@end
