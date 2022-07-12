//
//  CoreDataManager.swift
//  CoreBlue
//
//  Created by Yudha Hamdi Arzi on 11/07/22.
//

import Foundation
import CoreData
import UIKit

public class CoreDataManager {
  
  // MARK: - Properties
  static let shared = CoreDataManager()
  
  // MARK: - Basic Function
  lazy var manageContext: NSManagedObjectContext = (UIApplication.shared.delegate as! AppDelegate).persistentContainer.viewContext
  
  // MARK: - Save glucose data without duplicate timestamp
  
  func insertGlucose(value: Int64?, timeStamp: Date?, event: Int16?){
    let context = CoreDataManager.shared.manageContext
    let checkFetchRequest = GlucoseData.fetchRequest(on: timeStamp!)
    do {
      let existingData = try context.fetch(checkFetchRequest)
      if existingData.count > 0 {
        return
      }
    }catch{
    }
        
    let glucose = GlucoseData(context: context)
    
    if let value = value,
       let timeStamp = timeStamp,
       let event = event {
      
      glucose.value = value
      glucose.timeStamp = timeStamp
      glucose.event = event
      try? context.save()
    }
  }
  
  // MARK: - Clear core data
  func clearGlucose() {
    let context = CoreDataManager.shared.manageContext
    let request = NSFetchRequest<GlucoseData>(entityName: "GlucoseData")
    let glucose = try! CoreDataManager.shared.manageContext.fetch(request)
    
    while glucose.count > 0 {
      context.delete(glucose[0])
      try? context.save()
    }
  }
  
  // MARK: - Fetch all data from core data
  func fetchGlucose() -> [GlucoseData] {
    let request = NSFetchRequest<GlucoseData>(entityName: "GlucoseData")
    let glucose = try! CoreDataManager.shared.manageContext.fetch(request)
    
    return glucose
  }
  
  
}
