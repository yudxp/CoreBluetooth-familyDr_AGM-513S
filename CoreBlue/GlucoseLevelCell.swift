//
//  GlucoseCellView.swift
//  CoreBlue
//
//  Created by Yudha Hamdi Arzi on 11/07/22.
//

import UIKit

class GlucoseLevelCell: UITableViewCell {
  @IBOutlet weak var eventImage: UIImageView!
  @IBOutlet weak var glucoseLevel: UILabel!
  @IBOutlet weak var dateLabel: UILabel!
  
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
