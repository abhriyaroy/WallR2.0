# Kotlin
-keep class zebrostudio.wallr100.** { *; }

# Support appcompat
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# Support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Support constraint
-dontwarn android.support.constraint.**
-keep class android.support.constraint.** { *; }
-keep interface android.support.constraint.** { *; }
-keep public class android.support.constraint.R$* { *; }

# Dagger
-dontwarn dagger.internal.codegen.**
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}
-keep class dagger.* { *; }
-keep class javax.inject.* { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod, Exceptions
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Ucrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

# Rx ImagePicker
-dontwarn com.mlsdev.rximagepicker.**
-keep class com.mlsdev.rximagepicker** { *; }
-keep interface com.mlsdev.rximagepicker** { *; }
-dontwarn com.qingmei2.rximagepicker_extension_zhihu.**
-keep class com.qingmei2.rximagepicker_extension_zhihu** { *; }
-keep interface com.qingmei2.rximagepicker_extension_zhihu** { *; }
-dontwarn com.qingmei2.rximagepicker_extension.**
-keep class com.qingmei2.rximagepicker_extension** { *; }
-keep interface com.qingmei2.rximagepicker_extension** { *; }

# LowpolyRx
-dontwarn com.zebrostudio.lowpolyrxjava**
-keep class com.zebrostudio.lowpolyrxjava** { *; }
