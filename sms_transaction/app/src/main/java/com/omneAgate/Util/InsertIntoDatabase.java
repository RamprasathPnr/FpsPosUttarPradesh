package com.omneagate.Util;

import android.content.Context;

import com.omneagate.DTO.MessageDto;

/**
 * Created by user1 on 7/3/15.
 */
public class InsertIntoDatabase {

    Context context;

    public InsertIntoDatabase(Context context) {
        this.context = context;
    }


    public void insertIntoDatabase() {
        insertDbErrors(1, 1000, "ta", " அணுகல் மறுக்கப்பட்டது  ");
        insertDbErrors(2, 1000, "en", "Access Denied");

        insertDbErrors(3, 1001, "ta", " கடவுச்சொல் பொருத்தமில்லை   ");
        insertDbErrors(4, 1001, "en", "Password mismatch");

        insertDbErrors(5, 1002, "ta", "  தவறான பயனர் பெயர் அல்லது கடவுச்சொல்");
        insertDbErrors(6, 1002, "en", "Incorrect UserName or Password");

        insertDbErrors(7, 3001, "ta", "   தவறான அளவுரு     ");
        insertDbErrors(8, 3001, "en", " invalid parameter ");

        insertDbErrors(9, 3002, "ta", "  தவறான அளவுரு வடிவம்  ");
        insertDbErrors(10, 3002, "en", "invalid parameter format");

        insertDbErrors(11, 4000, "ta", " பொதுவான தரவுத்தளம் தகவல் பிழை");
        insertDbErrors(12, 4000, "en", " Generic database error");

        insertDbErrors(13, 2001, "ta", "உள்ளீடு பிழை ");
        insertDbErrors(14, 2001, "en", " Input error");

        insertDbErrors(15, 101, "ta", " அட்டை இல்லை");
        insertDbErrors(16, 101, "en", " Card Not present");

        insertDbErrors(17, 103, "ta", "பயனாளியின் ஒதுக்கீடு அளவு மீறப்பட்டுவிட்டது");
        insertDbErrors(18, 103, "en", " Beneficiary exceeded entitlement");

        insertDbErrors(19, 102, "ta", "ஒதுக்கீடு தவறாக உள்ளது");
        insertDbErrors(20, 102, "en", "Entitlement mismatch");

        insertDbErrors(21, 104, "ta", "அட்டை முடக்கப்பட்டுள்ளது");
        insertDbErrors(22, 104, "en", " Beneficiary Inactive. Please contact helpdesk");

        insertDbErrors(23, 105, "ta", "சாதனம் உபயோகத்தில் இல்லை");
        insertDbErrors(24, 105, "en", "Device is Blocked/Inactive .Please contact helpdesk ");

        insertDbErrors(25, 106, "ta", "நியாய விலை கடை  உபயோகத்தில் இல்லை ");
        insertDbErrors(26, 106, "en", "FPS Store Blocked/Inactive");

        insertDbErrors(27, 107, "ta", "பொருட்களின் கையிருப்பு  கிடைக்கவில்லை ");
        insertDbErrors(28, 107, "en", "Stock Not available ");

        insertDbErrors(29, 108, "ta", "பயனாளியின்  விவரங்கள் இல்லை ");
        insertDbErrors(30, 108, "en", "Entitlement not available");

        insertDbErrors(31, 109, "ta", "ஓடிபி நேரம் முடிவடைந்தது");
        insertDbErrors(32, 109, "en", "OTP expired");

        insertDbErrors(33, 110, "ta", "ஓடிபி பயனாளிக்கு உருவாக்க முடியவில்லை");
        insertDbErrors(34, 110, "en", "No valid OTP available for beneficiary");

        insertDbErrors(35, 201, "ta", "தவறான கோரிக்கை. நிர்வாகச் சான்றுகளை கொண்டு உள்நுழையவும்");
        insertDbErrors(36, 201, "en", "Invalid request. Please login with Admin credentials");

        insertDbErrors(37, 202, "ta", "தவறான சாதனம்");
        insertDbErrors(38, 202, "en", "Invalid Device");

        insertDbErrors(39, 203, "ta", "தவறான சாதனம்");
        insertDbErrors(40, 203, "en", "Invalid Device");

        insertDbErrors(41, 301, "ta", "சாதன எண் இல்லை");
        insertDbErrors(42, 301, "en", "FPS Device id should not be empty");

        insertDbErrors(43, 111, "ta", "அலைபேசி எண் பதிவு செய்யப்படவில்லை. உதவி மையத்தை அணுகவும்");
        insertDbErrors(44, 111, "en", "RMN Not registered in FPS.Please contact helpdesk to register RMN");

        insertDbErrors(45, 112, "ta", " கையிருப்பு  கிடைக்கவில்லை ");
        insertDbErrors(46, 112, "en", "Stock Not available");

        insertDbErrors(47, 113, "ta", "பயனாளர் குடும்ப விவரங்கள் இல்லை");
        insertDbErrors(48, 113, "en", "Beneficiary family details not found.");

        insertDbErrors(49, 114, "ta", "தவறான மேம்படுத்துதல் கோரிக்கை");
        insertDbErrors(50, 114, "en", "Invalid update stock request.");

        insertDbErrors(51, 115, "ta", "கடைக்கான தவறான மேம்படுத்துதல் கோரிக்கை");
        insertDbErrors(52, 115, "en", "Invalid FPSStock for update");

        insertDbErrors(53, 201, "ta", "பயனாளியை பதிவு செய்ய அலைபேசி எண் அவசியம்");
        insertDbErrors(54, 201, "en", "Mobile Number is mandatory for beneficiary registration");

        insertDbErrors(55, 202, "ta", "பயனாளி அட்டை  செயல்படுத்தபட்டுவிட்டது");
        insertDbErrors(56, 202, "en", "Beneficiary is active");

        insertDbErrors(57, 203, "ta", "அலைபேசி எண் மற்றோர் பயனாளிக்கு பதிவு செய்யப்பட்டு உள்ளது");
        insertDbErrors(58, 203, "en", "Mobile number is already registered to another beneficiary");

        insertDbErrors(59, 204, "ta", "பழைய அட்டை விவரங்கள் ஏற்கனவே உள்ளது");
        insertDbErrors(60, 204, "en", "Old card details already available");

        insertDbErrors(61, 1002, "ta", "உள்ளீட்டு விவரங்கள் தவறு");
        insertDbErrors(62, 1002, "en", "Input request is invalid");

        insertDbErrors(63, 1003, "ta", "பில் விவரங்கள் இல்லை");
        insertDbErrors(64, 1003, "en", "Bill details is empty");

        insertDbErrors(65, 1004, "ta", "பொருட்கள் விவரங்கள் தவறு");
        insertDbErrors(66, 1004, "en", "Bill item details is invalid");

        insertDbErrors(67, 1005, "ta", "அட்டை விவரங்கள் தவறு");
        insertDbErrors(68, 1005, "en", "QR Code in input is invalid");

        insertDbErrors(69, 1006, "ta", "சாதன எண் தவறு");
        insertDbErrors(70, 1006, "en", "Device id input is invalid");

        insertDbErrors(71, 1007, "ta", "அலைபேசி எண் தவறு");
        insertDbErrors(72, 1007, "en", "Mobile Number input is invalid");

        insertDbErrors(73, 1008, "ta", "பழைய அட்டை விவரங்கள் தவறு");
        insertDbErrors(74, 1008, "en", "Old card number input is invalid");

        insertDbErrors(75, 1009, "ta", "அட்டை வகை  தவறு");
        insertDbErrors(76, 1009, "en", "Card type  input is invalid");

        insertDbErrors(77, 1010, "ta", "சிலிண்டர் எண் தவறு");
        insertDbErrors(78, 1010, "en", "Cylinder number input is invalid");

        insertDbErrors(79, 5001, "ta", "குருஞ்செய்தி அனுப்ப இயலாது");
        insertDbErrors(80, 5001, "en", "SMS service unavailable");

        insertDbErrors(81, 301, "ta", "சாதன எண்ணை உள்ளிடவும்");
        insertDbErrors(82, 301, "en", "FPS Device id should not empty");

        insertDbErrors(83, 501, "ta", "தரவு தளத்துடன் தொடர்பு கொள்ள இயலவில்லை");
        insertDbErrors(84, 501, "en", "Unable to connect to database");

        insertDbErrors(85, 5000, "ta", "இணைப்பில் கோளாறு உள்ளது. மீண்டும் முயற்சிக்கவும்");
        insertDbErrors(86, 5000, "en", "Requested service unavilable. Please try again");

        insertDbErrors(87, 116, "ta", "ஒதுக்கீட்டு விவரங்கள் இந்த அட்டைக்கு  இல்லை");
        insertDbErrors(88, 116, "en", "Allotment details unavailable for card type");

        insertDbErrors(89, 117, "ta", "வயது சம்பந்தமான விவரங்கள் இல்லை");
        insertDbErrors(90, 117, "en", "Age group details unavailable");

        insertDbErrors(91, 118, "ta", "பொருட்களின் விவரங்கள் இல்லை");
        insertDbErrors(92, 118, "en", "Product details not available");

        insertDbErrors(93, 119, "ta", "பரிவர்த்தனை விவரங்கள் இல்லை");
        insertDbErrors(94, 119, "en", "Transaction Request can not be null");

        insertDbErrors(97, 130, "ta", "Request not available for ration card number");
        insertDbErrors(98, 130, "en", "Request not available for ration card number");

        insertDbErrors(99, 131, "ta", "Card Type not available");
        insertDbErrors(100, 131, "en", "Card Type not availabler");

        insertDbErrors(101, 132, "ta", "Request for card number available");
        insertDbErrors(102, 132, "en", "Request for card number available");

        insertDbErrors(103, 133, "ta", "Request with mobile number already available");
        insertDbErrors(104, 133, "en", "Request with mobile number already available");

        insertDbErrors(105, 134, "ta", "Invalid card number format");
        insertDbErrors(106, 134, "en", "Invalid card number format");

        insertDbErrors(107, 135, "ta", "Ufc is already associated");
        insertDbErrors(108, 135, "en", "Ufc is already associated");

        insertDbErrors(109, 136, "ta", "Ufc does not belong to fps id");
        insertDbErrors(110, 136, "en", "Ufc does not belong to fps id");

        insertDbErrors(111, 137, "ta", "Ufc is blocked");
        insertDbErrors(112, 137, "en", "Ufc is blocked");

        insertDbErrors(113, 129, "ta", "Invalid OTP");
        insertDbErrors(114, 129, "en", "Invalid OTP");

    }


    private void insertDbErrors(int id, int errorCode, String lId, String description) {
        MessageDto messages = new MessageDto();
        messages.setId(id);
        messages.setLanguageCode(errorCode);
        messages.setLanguageId(lId);
        messages.setCreatedBy(1);
        messages.setModifiedBY(1);
        messages.setDescription(description);
        FPSDBHelper.getInstance(context).insertLanguageTable(messages);
    }

}
