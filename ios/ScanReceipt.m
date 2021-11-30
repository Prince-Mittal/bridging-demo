//
//  ScanReceipt.m
//  RNBlinkreceipt
//
//  Created by Prince Mittal on 04/07/21.
//

#import "ScanReceipt.h"
#import <React/RCTLog.h>
#import <AVFoundation/AVFoundation.h>
#import <BlinkReceipt/BlinkReceipt.h>
#import "AppDelegate.h"

@interface ScanReceipt() <RCTBridgeModule>

@property (nonatomic, strong) RCTResponseSenderBlock callbackSuccess;
@property (nonatomic, strong) RCTResponseSenderBlock callbackError;

@end

@implementation ScanReceipt

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(scan:(RCTResponseSenderBlock)callbackSuccess callbackError:(RCTResponseSenderBlock)callbackError){
  @try {
    self.callbackSuccess = callbackSuccess;
    self.callbackError = callbackError;
    NSString *mediaType = AVMediaTypeVideo;
    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:mediaType];
    BRScanOptions *scanOptions = [BRScanOptions new];
    scanOptions.storeUserFrames = true;
    scanOptions.jpegCompressionQuality = 0.6;
    scanOptions.detectDuplicates = true;
    if(authStatus == AVAuthorizationStatusAuthorized) {
        dispatch_async(dispatch_get_main_queue(), ^{
                [[BRScanManager sharedManager] startStaticCameraFromController:[self currentApplicationViewController]
                                                                     scanOptions:scanOptions
                                                                    withDelegate:self];
        });
    }else{
      // not determined
      [AVCaptureDevice requestAccessForMediaType:mediaType completionHandler:^(BOOL granted) {
        if(granted){
            dispatch_async(dispatch_get_main_queue(), ^{
              [[BRScanManager sharedManager] startStaticCameraFromController:[self currentApplicationViewController]
                                                                   scanOptions:scanOptions
                                                                  withDelegate:self];
              
            });
        } else {
          callbackError(@[@"CAMERA_PERMISSION"]);
        }
      }];
    }
  }@catch(NSException *e){
    callbackError(@[e.reason]);
  }
}

- (UIViewController *) currentApplicationViewController {
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UIViewController *rootViewController = window.rootViewController;
    
    if([rootViewController isKindOfClass:[UIViewController class]]){
        return [[UIApplication sharedApplication]delegate].window.rootViewController;
    } else {
        UINavigationController *navigationController = (UINavigationController *)[[UIApplication sharedApplication]delegate].window.rootViewController;
        return(UIViewController *)[navigationController topViewController];
    }
    return nil;
}

- (void)didFinishScanning:(UIViewController *)cameraViewController withScanResults:(BRScanResults *)scanResults {
  @try{
    NSUInteger n = [BRScanManager sharedManager].userFramesFilepaths.count;
    NSString* imageArr = @"";
    for(int i=0;i<n;i++){
      NSString* currentPath = [BRScanManager sharedManager].userFramesFilepaths[i];
      if(i==0){
        imageArr = currentPath;
      }else{
      imageArr = [[imageArr stringByAppendingString:@" "] stringByAppendingString:currentPath];
      }
    }
  if(scanResults.products.count > 0){
    NSDictionary *resultsDict = [scanResults dictionaryForSerializing];
    self.callbackSuccess(@[resultsDict,imageArr]);
  }
  else{
    self.callbackError(@[@"Please scan a valid Receipt"]);
  }
  dispatch_async(dispatch_get_main_queue(), ^{
    [cameraViewController dismissViewControllerAnimated:YES completion:nil];
  });
  }
  @catch(NSException *e){
    self.callbackError(@[e.reason]);
  }
}

- (void)didCancelScanning:(UIViewController *)cameraViewController{
  NSLog(@"Scan Cancelled");
  @try{
  dispatch_async(dispatch_get_main_queue(), ^{
    [cameraViewController dismissViewControllerAnimated:YES completion:nil];
  });
    self.callbackError(@[@"cancel"]);
  }
  @catch(NSException *e){
    self.callbackError(@[e.reason]);
  }
}

@end
