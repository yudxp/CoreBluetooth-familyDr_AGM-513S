//
//  Logger.swift
//  CoreBlue
//
//  Created by Yudha Hamdi Arzi on 12/07/22.
//

// Function to dissable print

import Foundation

var globalVerbose: Bool = false

class Logger {
  static func log(_ data: Any...){
    if globalVerbose {
      for item in data {
        print(item, terminator: " ")
      }
      print("")
    }
  }
}
