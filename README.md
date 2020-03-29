# ff-protect-android
FF protect is an open-source voluntary tracker app installed on a mobile phone remotely activated based on the global/national health scenario to analyse and track possible influenza carriers.

## Proposed Solution

* Develop and maintain a voluntary bluetooth and location tracking app to be used by general public.
* Provide a web interface to health workers for inputting confirmed and suspected cases info.
* Process and give basic insight to govt officials based on the data collected.
* Give raw data access to data scientists to extract scenario based insights and trackable information.

## Why? How? Demo?
Please watch this short video by FareFirst team: https://youtu.be/mHnu5omVSKw

## Download the production ready apk for android phones
https://github.com/FareFirst/ff-protect-android/blob/master/releases/FF_Protect_0102.apk

## Presentation Slides & Project status
https://drive.google.com/file/d/12rhzLw8JOXYogY40fY3Lqna4d0_pSHMU/view?usp=sharing

## Privacy / Security
* All user data leaving the users phone are encrypted with a secure public key.
* The server storing this data will be secure with all the govt mandated specifications. (preferably google cloud mumbai region)
* The stored data can be decrypted only with the private key from the govt.
* Mobile number and any user identifiable details need another private key from the govt to decrypt once those set of users come under the radar.

## Description
With the growing need for a contact tracing solution which could help the efforts to break the chain of infection, resulting in effective containment of contagious infections such as the ongoing COVID-19 , the FareFirst team has come up with a complete tracking system and made it open-source a week back. 

This solution needs you to just install the app and sign up. The app collects Bluetooth and optinally Location signals in the background. With Bluetooth low energy technology we can calculate the exact distance between users and also the duration of exposure to each other. This data can be utilized by the officials to create an accurate contact tracing map in case of an outbreak.  The solution is completely encrypted and anonymized . Please share this solution with your government officials and let’s fight the pandemic together.


## Journey

### User Journey
1) User installs FF Protect app
2) Signs up with phone number

The app will automatically get activated and push encrypted data automatically.

### Government authorities journey
1) Enable user tracking in our dashboard whenever there is a suspected epidemic/pandemic scenario.

a) This can be further tweaked to specific geographical areas only OR nationwide.

b) Advanced filtering and conditional tracking options available, for example: people entering or exiting particular geographical area.

The data will get streamed and saved securely on gov/private server.

2) The health workers inform confirmed cases, or a set of people to be tracked. The following info are to be entered in the dashboard.

a) Phone number/s of index patient or suspected people. 

b) If phone number unavailable, then, Area of the of index patient or suspected people


### Data analyser/Data scientist journey
(Basic data analysing and mapping will be provided by our system itself with predefined data processing algorithms)
 
1) The data will be processed based on the health workers input and presented to the data scientist/s to analyse and predict the spread. 

2) The analyst can prepare reports and actionable insights by querying and processing the raw data.



## See Also
* https://www.tracetogether.gov.sg/
* https://www.bna.bh/en/BahrainlaunchescontacttracingapptocontainspreadofCoronavirusCOVID19.aspx?cms=q8FmFJgiscL2fwIzON1%2BDh0PXzItThrawIIySvGFaGI%3D
