# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/user1/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# for gson (GSON @Expose annotation)
 -keep public class com.google.gson.Gson
 -keepattributes *Annotation*
 -keep class sun.misc.Unsafe { *; }
 -keep class com.google.gson.stream.** { *; }
 -keep class com.google.gson.examples.android.model.** { *; }

 -keepattributes Signature
 -keepattributes *Annotation*

-dontwarn lombok.**
-dontwarn org.apache.**
-dontwarn org.jasypt.**
-dontwarn org.joda.**
-dontwarn org.bouncycastle.**
-dontwarn com.sun.mail.**
-dontwarn javax.activation.**
