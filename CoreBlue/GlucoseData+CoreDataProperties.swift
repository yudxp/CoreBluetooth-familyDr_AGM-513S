//
//  GlucoseData+CoreDataProperties.swift
//  CoreBlue
//
//  Created by Yudha Hamdi Arzi on 11/07/22.
//
//

import Foundation
import CoreData


extension GlucoseData {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<GlucoseData> {
        return NSFetchRequest<GlucoseData>(entityName: "GlucoseData")
    }
  
    @nonobjc public class func fetchRequest(on: Date) -> NSFetchRequest<GlucoseData>
    {
        let fetchRequest = NSFetchRequest<GlucoseData>(entityName: "GlucoseData")
        fetchRequest.predicate = NSPredicate(format: "timeStamp = %@", on as NSDate)
        return fetchRequest
    }

    @NSManaged public var value: Int64
    @NSManaged public var event: Int16
    @NSManaged public var timeStamp: Date?

}

extension GlucoseData : Identifiable {

}
