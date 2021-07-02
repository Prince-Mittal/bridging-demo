import React, {useState} from 'react';
import {
  View,
  Text,
  Button,
  NativeModules,
  PermissionsAndroid,
  Platform,
  ScrollView,
} from 'react-native';
import styles from './styles';

function App() {
  const [products, setProducts] = useState([
    {
      name: 'iPhone 11',
      quantity: 1,
      price: 99000,
    },
    {
      name: 'Macbook Pro 16GB',
      quantity: 1,
      price: 154000,
    },
  ]);
  const callbackSuccess = (scanResult, imageArr) => {
    let data;
    if (Platform.OS === 'android') {
      data = JSON.parse(scanResult);
    }
    let productsData = [];
    data.products?.map((product, index) => {
      productsData = [
        ...productsData,
        {
          name: product.productDescription.value,
          quantity: product.quantity.value,
          price: product.totalPrice.value,
        },
      ];
    });
    setProducts(productsData);
  };

  const callbackError = error => {
    console.log('error', error);
  };

  const scanReceipt = async () => {
    //NativeModule will be called here
    if (Platform.OS === 'android') {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.CAMERA,
        {
          title: 'Permission to Access Camera',
          message: 'App needs access to your camera to scan the receipts',
          buttonNegative: 'Deny',
          buttonPositive: 'Allow',
        },
      );
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        NativeModules.ScanReceipt.scan(callbackSuccess, callbackError);
      } else {
        console.log('Camera permission denied');
      }
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.scanBtnContainer}>
        <Button onPress={scanReceipt} title="Scan Receipt" color="#50C878" />
      </View>
      <ScrollView>
        <View style={styles.tableHeader}>
          <Text style={styles.productNameHeader}>Name</Text>
          <Text style={styles.headerText}>Quantity</Text>
          <Text style={styles.headerText}>Price</Text>
        </View>

        <View style={styles.tableBody}>
          {products.map((product, index) => {
            return (
              <View style={styles.tableRow} key={index}>
                <Text style={styles.productNameBody}>{product.name}</Text>
                <Text style={styles.bodyText}>{product.quantity}</Text>
                <Text style={styles.bodyText}>
                  {Math.round(product.price * 100) / 100}
                </Text>
              </View>
            );
          })}
        </View>
      </ScrollView>
    </View>
  );
}

export default App;
