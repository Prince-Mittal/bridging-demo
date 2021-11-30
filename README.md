# bridging-demo

React Native bridging demo using blinkreceipt by micro blink. In this demo app we have bridged an OCR app that can scan receipts and provide us the products inside that receipt.

![final-demo.gif](final-demo.gif)

## Project Setup

### Android:

- Clone this repository
- Go to the cloned directory and run `npm i`
- Generate [blinkreceipt](https://microblink.com/products/blinkreceipt) key on  [micro blink](https://microblink.com/) website.
- Paste the generated key in `android/app/src/main/AndroidManifest.xml` file.
- Now the setup is complete. To launch the app run command `npm run android`.
