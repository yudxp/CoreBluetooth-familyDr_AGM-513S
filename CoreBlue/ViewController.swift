//
//  ViewController.swift
//  CoreBlue
//
//  Created by Yudha Hamdi Arzi on 23/05/22.
//

import UIKit
import CoreBluetooth

class ViewController: UIViewController {
  
  @IBOutlet weak var BlConnection: UIImageView!
  @IBOutlet weak var connectionStatus : UILabel!
  @IBOutlet weak var glucoseTableView: UITableView!
  
  var glucoseData: [GlucoseData] = []
  
  var centralManager: CBCentralManager!
  let glucoseMeterUUID = CBUUID(string: "0xFF00") // UUID of the devices (you can use NRFConnect for scan it)
  let glucoseMeterCharacteristic = "FF01" // Characteristic that we want get data from (see bluetooth LE documentation)
  var glucosePeripheral : CBPeripheral!
  let dateFormatter = DateFormatter()


  override func viewDidLoad() {
    super.viewDidLoad()
    registerTableViewCells()
    glucoseData = CoreDataManager.shared.fetchGlucose()
    glucoseTableView.reloadData()
    for data in glucoseData {
      Logger.log(data.timeStamp!, data.value, data.event)
    }
    centralManager = CBCentralManager(delegate: self, queue: nil)
    dateFormatter.dateFormat = "dd-MM-yyyy HH:mm"

  }
  
  // MARK: - Regeister your custom cell
  private func registerTableViewCells() {
      let nib = UINib(nibName: "GlucoseLevelCell", bundle: nil)
      glucoseTableView.register(nib, forCellReuseIdentifier: "GlucoseLevelCell")
      glucoseTableView.delegate = self
      glucoseTableView.dataSource = self
  }
  
  // MARK: - Confirmation alert
  private func confirmAlert(){
    let alert = UIAlertController(title: "Download Completed", message: "glucose data recorded", preferredStyle: UIAlertController.Style.alert)
    alert.addAction(UIAlertAction(title: "Confirm", style: UIAlertAction.Style.default, handler: nil))
    self.present(alert, animated: true, completion: nil)
  }

}

 // MARK: - CoreBluetooth delegate
extension ViewController: CBCentralManagerDelegate,CBPeripheralDelegate {
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
      guard let services = peripheral.services else {return}
      
      for service in services {
        Logger.log(service)
        peripheral.discoverCharacteristics(nil, for: service)
      }
    }
  
  // MARK: - Parse incoming data
  func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
    guard let glucoseData = characteristic.value else {
      Logger.log("missing updated value"); return }
      
    let gulaDarah = glucoseData as NSData //convert characteristic value to NSData
    var dataString: [Character] = []
    if (gulaDarah.length == 20) && (gulaDarah[2] > 48){ // Data valid only when it's length >20 Byte and it's first data
        for data in gulaDarah {
          let str = String(data, radix: 16)
          let newStr: Character = str[str.index(str.startIndex, offsetBy: 1)] // Take odd data
          dataString.append(newStr)
        }
      // Bitbang data, i think there is eficient way
      let ddate = dateFormatter.date(from: "\(dataString[8])\(dataString[9])-\(dataString[6])\(dataString[7])-20\(dataString[4])\(dataString[5]) \(dataString[10])\(dataString[11]):\(dataString[12])\(dataString[13])")
      let glucoseValue = Int64("\(dataString[15])\(dataString[16])\(dataString[17])")
      let glucoseEvent = Int16("\(dataString[19])")
      // Save to core data
      CoreDataManager.shared.insertGlucose(value: glucoseValue , timeStamp: ddate, event: glucoseEvent)
      }
    else {
      return
    }
    // Make confirmation alert if all data already downloaded
    confirmAlert()
    // Update data and update tableView
    self.glucoseData = CoreDataManager.shared.fetchGlucose()
    glucoseTableView.reloadData()
  }

  // MARK: - If found characteristic read the value
  func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
      if let characteristics = service.characteristics {
          for characteristic in characteristics {
              
              switch characteristic.uuid.uuidString{
              case glucoseMeterCharacteristic:
                  peripheral.setNotifyValue(true, for: characteristic)
                  Logger.log("Characteristic: \(characteristic)")
                  peripheral.readValue(for: characteristic)
              default:
                  Logger.log("")
              }
          }
      }
  }
  
  // MARK: - Do something when bluetooth in the state. I scan when bluetooth power on
  func centralManagerDidUpdateState(_ central: CBCentralManager) {
    switch central.state {
      case .unknown:
        Logger.log("central.state is .unknown")
      case .resetting:
        Logger.log("central.state is .resetting")
      case .unsupported:
        Logger.log("central.state is .unsupported")
      case .unauthorized:
        Logger.log("central.state is .unauthorized")
      case .poweredOff:
        Logger.log("central.state is .poweredOff")
      case .poweredOn:
        Logger.log("central.state is .poweredOn")
        centralManager.scanForPeripherals(withServices: [glucoseMeterUUID])
      @unknown default:
        Logger.log("Error")
    }
  }
  
  func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
    updateConnection(status: true)
    glucosePeripheral.discoverServices([glucoseMeterUUID])
  }
  
  func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
    updateConnection(status: false)
  }
  
  func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
    print(peripheral)
    glucosePeripheral = peripheral
    glucosePeripheral.delegate = self
    centralManager.stopScan()
    centralManager.connect(glucosePeripheral)
  }
  
  // MARK: - Update text and connection logo
  func updateConnection(status: Bool){
    switch status {
    case true:
      connectionStatus.text = "Connected"
      Logger.log("Glucose Meter Connected")
      BlConnection.tintColor = .green
    case false:
      connectionStatus.text = "Not Connected"
      Logger.log("Glucose Meter Disconnected")
      BlConnection.tintColor = .gray
    }
  
  }
}

// MARK: - TableView delegate
extension ViewController: UITableViewDelegate, UITableViewDataSource {
  
  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return glucoseData.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    let cell = tableView.dequeueReusableCell(withIdentifier: "GlucoseLevelCell", for: indexPath) as! GlucoseLevelCell
    let glucoseValue = glucoseData[indexPath.row].value
    let dateTime = dateFormatter.string(from:(glucoseData[indexPath.row].timeStamp! as Date))
    let mealEvent = glucoseData[indexPath.row].event
    
    cell.glucoseLevel.text = String(glucoseValue)
    cell.dateLabel.text = dateTime
    
    switch mealEvent {
    case 0:
        cell.eventImage.image = UIImage.init(named: "fasting")
    case 1:
        cell.eventImage.image = UIImage.init(named: "postMeal")
    case 2:
        cell.eventImage.image = UIImage.init(named: "preMeal")
    default:
        Logger.log("Is it new data?")
    }
    return cell
  }
}
