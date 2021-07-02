import {StyleSheet} from 'react-native';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    padding: 20,
  },
  scanBtnContainer: {
    marginVertical: 20,
  },
  tableHeader: {
    flexDirection: 'row',
  },
  productNameHeader: {
    fontWeight: 'bold',
    padding: 20,
    flex: 2,
    borderWidth: 1,
  },
  headerText: {
    fontWeight: 'bold',
    padding: 20,
    flex: 1,
    borderWidth: 1,
    borderLeftWidth: 0,
  },
  tableRow: {
    flexDirection: 'row',
  },
  productNameBody: {
    fontWeight: 'bold',
    padding: 20,
    flex: 2,
    borderWidth: 1,
    borderTopWidth: 0,
  },
  bodyText: {
    padding: 20,
    flex: 1,
    borderWidth: 1,
    borderLeftWidth: 0,
    borderTopWidth: 0,
  },
});

export default styles;
