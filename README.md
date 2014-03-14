Android-Utils
=============

###Better Logging###
`L.d(this, "Log this text"); `
* Avoid having to create TAGs at the top of all your classes
* Debug logs do not log in release builds  
https://github.com/rosshambrick/Android-Utils/blob/master/src/com/rosshambrick/android/utils/L.java  

###Better AsyncTask###
* Handles error conditions explicitly
* Shows a progress dialog by default (can override to disable)  
https://github.com/rosshambrick/Android-Utils/blob/master/src/com/rosshambrick/android/utils/BetterAsyncTask.java

###Master-Detail Fragment Activity###
* Remove the hassle of setting up a master-detail view
* Auto responds to phone and tablet sizes
* Supports multi-select viewing  
  
![Screenshot](https://raw.githubusercontent.com/rosshambrick/Android-Utils/screenshots/screenshots/device-2014-03-14-074333.png)  
https://github.com/rosshambrick/Android-Utils/tree/master/src/com/rosshambrick/android/utils/masterdetail  
