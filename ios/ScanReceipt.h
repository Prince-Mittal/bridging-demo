//
//  ScanReceipt.h
//  RNBlinkreceipt
//
//  Created by Prince Mittal on 04/07/21.
//

#import <React/RCTBridgeModule.h>
#import <BlinkReceipt/BlinkReceipt.h>

@interface ScanReceipt : NSObject <RCTBridgeModule,BRScanResultsDelegate>

@end
