<p align="center">
 <a href="https://play.google.com/store/apps/details?id=zebrostudio.wallr100">
 <img src="https://i.imgur.com/PWzM7sn.png" alt="Explore Wallpapers" width=800 height=400>
 </a>
</p>

Did you know that an average user checks their device more than 80 times a day? Make each time a real pleasure with beautiful HD wallpapers from WallR. Let your device be a treat to your eyes every-time you check it.

# Table of Contents

- [Introduction](#introduction) <br>
- [Screenshots](#screenshots) <br>
- [App Details](#app-details) <br>
- [App Features from an User's Perspective](#app-features-from-an-users-perspective) <br>
- [Project Setup Notes](#project-setup-notes) <br>
- [Acclamations](#acclamations) <br>
- [Libraries](#libraries) <br>
- [About the Author](#about-the-author)<br>
- [License](#license)<br>

# Introduction

<p>
WallR is an open-source Android wallpaper app written in <b>Kotlin</b>, keeping in mind proper coding guidelines and app architecture so that it is easy to maintain and scale.<br>

Salient features of the app :-
  - [Clean Architecture With MVP](#clean-architecture-with-mvp)<br>
  - [Dagger 2](#dagger-2)<br>
  - [RxJava 2](#rxjava-2)<br>
  - [Unsplash Api](#unsplash-api)<br>
  - [Firebase Realtime Database](#firebase-realtime-database)<br>
  - [Room Database](#room-database)<br>
  - [Shared Preferences](#shared-preferences)<br>
  - [Retrofit](#retrofit)<br>
  - [Glide](#glide)<br>
  - [Unit Tests](#unit-tests)<br>
  - [Instrumentation Tests](#instrumentation-tests)<br>

</p>

# Screenshots

<p align="center">
  <img src="https://i.imgur.com/xlTXmLD.jpg" alt="Explore Wallpapers" width=280 height=500 hspace="3">
  <img src="https://i.imgur.com/tCYtXOg.jpg" alt="Wallpaper Categories" width=280 height=500 hspace="3">
  <img src="https://i.imgur.com/xDNyaOn.png" alt="Collections" width=280 height=500 hspace="3">
</p> 
<p align="center">
  <img src="https://i.imgur.com/bODjKaX.jpg" alt="Search Wallpapers" width=280 height=500 hspace="3">
  <img src="https://i.imgur.com/BfUPBLr.jpg" alt="Minimal Wallpapers" width=280 height=500 hspace="3">
  <img src="https://i.imgur.com/2fJsYzR.jpg" alt="Wallpaper Details" width=280 height=500 hspace="3">
</p>
 
# App Details

  - [Architecture](#architecture)
     - [Clean Architecture With MVP](#clean-architecture-with-mvp)<br>
  - [Dependency Injection](#dependency-injection)<br>
     - [Dagger 2](#dagger-2)
  - [Multi-threading](#multi-threading)<br>
     - [RxJava 2](#rxjava-2)
  - [Remote Data Source](#remote-data-source)
     - [Unsplash Api](#unsplash-api)<br>
     - [Firebase Realtime Database](#firebase-realtime-database)<br>
  - [Data Persistence](#data-persistence)
     - [Room Database](#room-database)<br>
     - [Shared Preferences](#shared-preferences)<br>
  - [Networking](#networking)
     - [Retrofit](#retrofit)<br>
  - [Image Loading](#image-loading)
     - [Glide](#glide)
  - [Testing](#testing)
     - [Unit Tests](#unit-tests)<br>
     - [Instrumentation Tests](#instrumentation-tests)<br>
     
  
 ## Architecture
 
 A proper app architecture implements the <a href="https://howtodoinjava.com/best-practices/5-class-design-principles-solid-in-java/">SOLID</a> principles and ensures :-
 - Easy `scalability` as features are independent of each other.
 - Easy `maintainance` since the classes are decoupled in nature thus we need to make changes only to the desired class and it would be reflected everywhere.
 - `Testability`, since the abstraction layers are easy to mock and test.
 <br>
 Thus, for WallR, Clean Architecture with MVP was chosen as the architecture.<br>
  
 ### Clean Architecture with MVP
 
  In Clean Architecture, the code is separated into layers in an onion shape with one dependency rule: The inner layers should not know   anything about the outer layers. Inner layers contain business logic, whereas the outer layers contain implementation and the middle     layer contain Interface Adapters. Each ring represent one layer of abstraction.
  <p align="center">
  <img src="https://i.imgur.com/ZTLG6Ax.png" width=400 height=300>
  </p>
  
  <br>
 
  A diagram representing different layers, components and how they communicate with each other in the app :-
  
  <br>
  
  <p align="center">
  <img src="https://i.imgur.com/dHLjdiu.png" width=800 height=350>
  </p>
  
  ### Presentation Layer
  - `MVP (Model View Presenter)` is suitable for the presentation layer.
  - <a href="https://github.com/abhriyaroy/WallR2.0/tree/develop/app/src/main/java/zebrostudio/wallr100/android/ui">Views</a> are dumb        and implement `Passive View pattern`. It is a set of interfaces that could be implemented by any Android view, such as Activities,      Fragments, Adapters or Custom Views.
  - <a href="https://github.com/abhriyaroy/WallR2.0/tree/develop/app/src/main/java/zebrostudio/wallr100/presentation">Presenter</a>         serve as a middleman between views (abstractions over Android specific components) and the business logic (Interactors/Use               Cases). They handle user interactions, invoke appropriate business logic and send the data to the UI for rendering.
  - `Presenter` does not depend on Android classes hence improves testability.
  
  ### Domain Layer
  - A simple example of <a href="https://github.com/abhriyaroy/WallR2.0/tree/develop/app/src/main/java/zebrostudio/wallr100/domain/interactor">Use Case</a> would be "Fetch new wallpapers". Each Use Case is a reusable component that executes a specific business logic. It fetches the data from a repository, executes the business logic and returns the result to the presenter.
  
  ### Data Layer (Database & API)
  - <a href="https://github.com/abhriyaroy/WallR2.0/tree/develop/app/src/main/java/zebrostudio/wallr100/data">Repository</a> is     responsible to create an abstraction of the data sources from which the Use Cases get the data to act upon.
  - Business logic shouldn’t know where the data comes from.
 
 ## Dependency Injection
 
  It is a software design pattern that implements inversion of control for resolving dependencies. Implementing proper dependency         injection in our apps allows us to have : <br>
  <ul>
    <li> Testable classes as dependencies which are injected from outside the class can be easily mocked</li>
    <li> Re-usable and interchangeable components. </li>
    <li> Scoped dependencies so that classes can share the same dependency state as and when required without having to create a new             instance every time. </li>
 </ul>
 
 ### Dagger 2
  - <a href="https://github.com/google/dagger">Dagger 2</a> is a dependency injection framework, which makes it easier to manage the         dependencies between the classes in our app.
  
  In this project the dependency graph is constructed via :
  <ul>
    <li> An <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/di/AppComponent.kt">App Component</a> which is used to bind the dependency graph to the application. </li>
    <li> An <a href="https://dagger.dev/api/latest/dagger/android/AndroidInjectionModule.html">Android Injection Module</a> which helps us inject into the android framework classes.</li>
    <li> An <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/di/AppModule.kt">App Module</a> which contains all the dependencies required at the app level. </li>
    <li> An <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/di/ActivityBuilder.kt">Activity Builder Module</a> which creates the various activity subcomponents. </li>
    <li> The <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/di/ServiceBuilder.kt">Service Builder Module</a> which creates the service subcomponent. </li>
  </ul>
  
  <br>
  
  The `Main Activity Subcomponent` has the <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/di/FragmentProvider.kt">Fragment Provider Module</a> which in-turn creates the fragment subcomponents.<br>
  
  The various dependency scopes used in this app are :
  <ul>
   <li> <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/di/scopes/PerApplication.kt">Per Application</a> - This is similar to the Singleton scope where the dependency lasts for the entire lifetime of the application. </li>
   <li> <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/di/scopes/PerActivity.kt">Per Activity</a> - This is the scope where the dependency lasts as long as the activity lasts. </li>
   <li> <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/di/scopes/PerFragment.kt">Per Fragment</a> - Where the dependency is attached to the lifecycle of the fragment. </li>
   <li> <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/di/scopes/PerService.kt">Per Service</a> - Where the dependency is attached to the lifecycle of the service. </li>
  </ul>
    
  A diagramatic representation od the Dependency graph for this project :-
  
  <br>
  <p align = "center"><img src="https://i.imgur.com/5l5vIhq.png" height=450></p>
  <br>
    
 ## Multi-threading 
 
  To provide the users with a fast and responsive app, heavy work such as network calls, database operations, file operations or other     background tasks need to be done on threads other than the `UI Thread`. This is where multi-threading comes into play.<br>
  Schedulers like `Computation`, `IO`, `Android Main Thread` have been used to effectively juggle between background and foreground         activities. However, all of this is done using a layer of abstraction for the <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/AndroidBackgroundThreads.kt">background</a> and <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/AndroidMainThread.kt">foreground</a> schedulers so that they can be easilty tested.
 
 ### RxJava 2
 
   <a href="https://github.com/ReactiveX/RxJava">RxJava</a> is used to do all the heavy lifting in seperate `background threads` and to    return the result of those operations to the `UI Thread` so that they can be used or displayed to the user without any stutters or lags in the app.
   <br>
   In this project the various reactive streams used are : <br>
   <ul>
   <li> <a href="http://reactivex.io/documentation/observable.html">Observables</a> are used in cases such as <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/data/ImageHandler.kt">fetching an image</a> from a remote source while updating the the progress on the screen and at last setting the image as the wallpaper of the device. </li>
   <li> <a href="http://reactivex.io/documentation/single.html">Single</a> is used in places where there is a one time operation like retrieving a <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/data/ImageHandler.kt">bitmap from a prevoiusly saved image file</a> and returning the bitmap so that some operation can be done using it. </li>
 <li> <a href="http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html">Completable</a> where only the result success or the error state is required such as <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/data/ImageHandler.kt">clearing the local image cache</a>. </li>
   </ul>
   
   In order to encash on the multi-threading capability of RxJava, the various schedulers that are used are :
   <ul>
   <li><a href="http://reactivex.io/RxJava/javadoc/io/reactivex/schedulers/Schedulers.html#io--">IO Scheduler</a> - is used for tasks such as <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/data/WallrDataRepository.kt">network requests, file write operations</a>, etc.</li>
   <li><a href="http://reactivex.io/RxJava/javadoc/io/reactivex/schedulers/Schedulers.html#computation--">Computation Scheduler</a> - is used to perform CPU intensive tasks like <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/data/WallrDataRepository.kt">generating or processing an image bitmap</a>. </li>
   <li><a href="https://static.javadoc.io/io.reactivex/rxandroid/1.2.1/rx/android/schedulers/AndroidSchedulers.html#mainThread()">Mainthread Scheduler</a> - represents the `UI Thread` and is used to observe the data coming from the various reactive streams. </li>
   </ul>
    
   <a href="https://github.com/uber/AutoDispose">Autodispose<a> is used to dispose off the various streams.
 
 ## Remote Data Source
 
 In this project, two sources of wallpaper have been used :-
 
 - `Unsplash API` which is used when an user searches for any specific wallpaper tag
 - `Firebase Realtime Database` which is used to provides a cached copy of the various categories of wallpapers from Unsplash due to the limited number of api requests available from unsplash derectly.
 
 ### Unsplash API
 
   The <a href="https://unsplash.com/developers">Unsplash API</a> is a modern JSON API that surfaces all of the info required for displaying various wallpapers to the users.<br>
   
   The `JSON` response obtained from the `Unsplash API` using [Retrofit](#retrofit), is trimmed down to the following data model :<br>
   
   <p align="center"><img src="https://i.imgur.com/zNQgcMn.png"></p>
   
  To convert the data into the above model, the <a href="http://square.github.io/retrofit/2.x/converter-gson/retrofit2/converter/gson/GsonConverterFactory.html">GSON Converter Factory</a> is used which maps the data into the model using the `SerializedName` provided with each field. <br>
 
 ### Firebase Realtime Database
 
   The Firebase Realtime Database is used to cache images from `Unsplash` as the number of `API requests` available in the `Unsplash        API` is limited. Thus the app fetches all the wallpaper data required for shocasing the default wallpapers from `Firebase` itself.      The basic structure of the firebase database for this project looks like :- 
   <p align="center"> <img src="https://i.imgur.com/WThrSrw.png" widht=700 height=550></a>
   
   The data model to which the `JSON` response from the `Firebase Realtime Database` is mapped, looks like :-
   
   <p align="center"><img src="https://i.imgur.com/7yldJuy.png"></p>
   
   To convert the data into the above model, <a href="https://github.com/google/gson">gson</a> is used which maps the data into the        model using the `variable names`.<br>
   
 ## Data Persistence
 
  Data Persistence is used to remember the `user preferences` and wether the user is pro or not using the <a href="https://developer.android.com/reference/android/content/SharedPreferences">shared preferences</a>. <br>
  The <a href="https://developer.android.com/topic/libraries/architecture/room">Room database</a> is used to run the <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/service/AutomaticWallpaperChangerService.kt">automatic wallpaper changer</a> which uses a locally saved collection of images and changes the device's wallpaper from    time to time.
 
 ### Room Database
 
   <p align="center"> <img src="https://i.imgur.com/dxvdjSf.png" widht=250 height=200></a>
   
   In `WallR`, the database is used behind an <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/data/SharedPrefsHelper.kt">abstraction</a> so that it can be tested and also future changes can be easily incorporated.<br>
   The `database` looks like :-
   
   <p align="center"><img src="https://i.imgur.com/kA3LBJB.png"></p>
 
 ### Shared Preferences
 
   The shared preferences are used under a <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/data/SharedPrefsHelper.kt">layer of abstraction</a> which helps us to test the logic and also makes provision for easy integration of api changes in the future. <br>
   The layer of abstraction also allows us to easily migrate from Shared Preferences to any other storing framework as changes under the layer will be reflested all througout the app.
   
 ## Networking
 
 Network operations are heavy tasks which should be performed in background threads so that the user does not face any `app not responding` errors due to the `UI Thread` getting clogged up. Thus I have used `Retrofit` with RxJava.
 
 ### Retrofit
 
   <a href="https://square.github.io/retrofit/">Retrofit</a> is also used behind an <a href="https://github.com/abhriyaroy/WallR2.0/tree/develop/app/src/main/java/zebrostudio/wallr100/data/api">abstraction</a> so that it can be tested and easily replaced with any other framework if needed. <br>
   <a href="https://github.com/JakeWharton/retrofit2-rxjava2-adapter">RxJava Call Adapter</a> is used to return the `responses` wrapped in `reactive streams`.<br>
   The <a href="http://square.github.io/retrofit/2.x/converter-gson/retrofit2/converter/gson/GsonConverterFactory.html">GSON Converter Factory</a> is used which map the `response data` into various `entity models` to be consumed. <br>
   `Retrofit` deep down uses the <a href="https://square.github.io/okhttp/">OkHttp</a> library for HTTP requests.<br>
   
 ## Image Loading
 
 Image loading can be from various sources of data like a `file path` or a `bitmap` or a `drawable resource` or it may also happen that the image needs to be processed befoer being used and glide caters to all that with very minimal `boiler-plate` code with proper `caching mechanisms` and hence was suitable for this project.
 
 ### Glide
 <a href="https://github.com/bumptech/glide">Glide</a> is used behind an <a href="https://github.com/abhriyaroy/WallR2.0/blob/develop/app/src/main/java/zebrostudio/wallr100/android/ui/ImageLoader.kt">image loader</a> abstraction so that any other image loading library can be easily used instead of glide if required.<br>
 
 ## Testing
   TDD (Test Driven Development) is an effective way of developing the applicaion by incrementally adding the code and writing tests. It ensures us that the addition of new features have not mistakenly broken anything else and gives us more confidence to push out code. <br>
    While writing tests we often come across certain edge cases which were not thought of while writing the normal code thus helps us build a more robust and stable application.
 
 ### Unit Tests
 
   Unit tests in this project are written using the <a href="https://site.mockito.org/">Mockito</a> framework and run using the <a href="https://junit.org/junit4/">JUnit</a> runner. These tests are very fast in nature and help us quickly test our codebase for any breaking change.
   
   The following unit test code coverage values reflect lower than actual coverage values due to certain <a href="https://discuss.kotlinlang.org/t/inline-functions-coverage/5366">issues</a> in calculation of code coverage in kotlin. More reports of such issues can be found <a href="https://stackoverflow.com/questions/54498333/inline-functions-are-causing-errors-in-unit-test-code-coverage-report">here</a>. 
   <br>
   <p align="center"><img src="https://i.imgur.com/Y4LhSkm.png"><br>Overall Coverage</a>
   <p align="center"><img src="https://i.imgur.com/pjpZ7BT.png"><br>Presentation Layer Coverage</a>
   <p align="center"><img src="https://i.imgur.com/qPeXf3n.png"><br>Domain Layer Coverage</a>
   <p align="center"><img src="https://i.imgur.com/oQQ7M3Q.png"><br>Data Layer Coverage</a>
   
   <p align="center"><img src="https://i.imgur.com/9esAJDh.png"></a>
 
 ### Instrumentation Tests
   User interface (UI) testing lets you ensure that your app meets its functional requirements and achieves a high standard of quality      such that it is more likely to be successfully adopted by users. <br>
   One approach to UI testing is to simply have a human tester perform a set of user operations on the target app and verify that it is   behaving correctly. However, this manual approach can be time-consuming, tedious, and error-prone. A more efficient approach is to       write UI tests such that user actions are performed in an automated way. The automated approach allows us to run tests                   quickly and reliably in a repeatable manner.
   
 
 ## App Features from an User's Perspective
  - Daily new wallpapers 
  - More than 10 Categories of wallpapers 
  - Search for wallpaper of your choice from a collection of over 100k+ images 
  - Minimal wallpapers 
  - Create your own Material or Gradient or Plasma wallpaper 
  - Quick set wallpaper 
  - Edit wallpapers before setting them 
  - Download wallpaper to device </li>
  - Crystallize any wallpaper 
  - Share wallpapers with your contacts 
  - Preview full screen wallpaper before setting 
  - Add wallpapers to collection for future use 
  - Enable automatic wallpaper changer to automatically change wallpapers periodically 
  - Add any external image to collection to use it as a wallpaper or to crystallize it 
  
 
## Project Setup Notes
- The google-services.json file has been purposely ignored by git due to security purposes. Please login to <a href="https://console.firebase.google.com/u/0/?gclid=CjwKCAjw__fnBRANEiwAuFxET6VRIbt1VaeN3D_DAYAU3rAwAC1uJGY1FaKZvmWSTe8bkiGe8lRAPBoCd0QQAvD_BwE">Firebase console</a> and create a new project and obtain your own google-services.json file and paste it at `app/src/debug` or `app/src/release` directory as per your build flavour to set it up and running.<br> For more information, please refer the <a href="https://firebase.google.com/docs/android/setup">docs</a>.

- This project can be directly built from the `command line` using the command :
   - `gradle build` for Windows OS.
   - `./gradlew build` for Linux/Mac OS.

- The `unit tests` can be direclty run from the `command line` using the command :
   - `gradle test` for Windows OS.
   - `./gradlew test` for Linux/Mac OS.
   
 Pro Tip : If your build fails, please ensure that your `JAVA_HOME` value in environment variables is set to <a href="https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html">JDK V8</a>. Please refer to <a href="https://stackoverflow.com/questions/45762245/android-studio-build-works-gradle-command-line-fails">this gradle issue</a>.<br>

## Acclamations

  WallR was selected as one of the best customization apps by <a href="https://www.androidauthority.com/5-android-apps-you-shouldnt-       miss-this-week-android-apps-weekly-review-90-796074">Android Authority</a>, <a href="https://www.androidpolice.com/2017/08/21/11-new-   notable-1-wtf-android-apps-last-week-81517-82117/">Android Police</a>. It also recieved huge number of warm and positive feedbacks and   reviews at <a href="https://forum.xda-developers.com/android/apps-games/app-wallr-wallpapers-beta-testers-t3568221">XDA Developers       Community</a>.

  <a href='https://play.google.com/store/apps/details?id=zebrostudio.wallr100&hl=en_IN&pcampaignid=MKT-Other-global-all-co-prtnr-py-       PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height=60 width = 145/></a>

## Libraries

 <a href="https://github.com/ReactiveX/RxJava">RxJava 2</a> - helps with multi-threading.<br>
  <a href="https://github.com/uber/AutoDispose">Autodispose</a> - used for disposing off observeables created using RxJava.<br>
 <a href="https://unsplash.com/">Unsplash</a> - the source of every wallpaper used in the app.<br>
 <a href="https://github.com/square/retrofit">Retrofit</a> - used for networking.<br>
 <a href="https://github.com/google/dagger">Dagger 2</a> - used for dependency injection.<br> 
 <a href="https://github.com/mockito/mockito">Mockito</a>, - helps with testing by mocking classes.<br>
 <a href="https://github.com/junit-team/junit4">JUnit 4</a> - helps in running tests.<br>
 <a href="https://github.com/GrenderG/Toasty">Toasty</a> - a library for showing fancy toasts to the user.<br>
 <a href="https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout">WaveSwipeRefreshLayout</a> - a custom viewgroup which provides a fancy way to refreshing items on swipe.<br>
 <a href="https://github.com/ybq/Android-SpinKit">Android-SpinKit</a> - provides fancy loading animations.<br>
 <a href="https://github.com/ogaclejapan/SmartTabLayout">SmartTabLayout</a> - a custom material tab layout.<br>
 <a href="https://github.com/afollestad/material-dialogs">Material Dialogs</a> - easy to use material dialogs.<br>
 <a href="https://github.com/bumptech/glide">Glide</a> - used for image loading.<br>
 <a href="https://github.com/wasabeef/recyclerview-animators">Recyclerview Animator</a> - used to animate recycler view items.<br>
 <a href="https://firebase.google.com/docs/android/setup">Firebase</a> - used for caching images from unsplash.<br>
 <a href="https://github.com/umano/AndroidSlidingUpPanel">Sliding up panel</a> - provides a sliding up bottom panel.<br>
 <a href="https://github.com/hdodenhof/CircleImageView">Circle ImageView</a> - a custom imageview.<br>
 <a href="https://github.com/KeepSafe/TapTargetView">TapTarget View</a> - used for showing hints to the user.<br>
 <a href="https://github.com/jaredrummler/MaterialSpinner">Material Spinner</a> - a custom material spinner. <br>
 <a href="https://github.com/MLSDev/RxImagePicker">Rx ImagePicker</a> - an image picker library wrapped with rxjava. <br>
 <a href="https://github.com/chrisbanes/PhotoView">Photoview</a> - a custom imageview with pinch zoom funtionality. <br>
 <a href="https://github.com/Yalantis/uCrop">Ucrop</a> - an image processing library which provides various editing options. <br>
 <a href="https://github.com/Yalantis/GuillotineMenu-Android">Guillotine-Menu</a> - a custom navigation drawer.<br>
 <a href="https://github.com/skydoves/ColorPickerView">Colorpicker view</a> - a custom view which helps the user to choose a color in-order to generate custom wallpapers.

<br>

## About the Author
### Abhriya Roy

Android Developer with 2 years of experience in building apps that look and feel great. 
Enthusiastic towards writing clean and maintainable code.
Open source contributor.

<a href="https://www.linkedin.com/in/abhriya-roy/"><img src="https://i.imgur.com/toWXOAd.png" alt="LinkedIn" width=40 height=40></a> &nbsp;
<a href="https://twitter.com/AbhriyaR"><img src="https://i.imgur.com/ymEo5Iy.png" alt="Twitter" width=42 height=40></a> 
&nbsp;
<a href="https://stackoverflow.com/users/6197251/abhriya-roy"><img src="https://i.imgur.com/JakJaHP.png" alt="Stack Overflow" width=40 height=40></a> 
&nbsp;
<a href="https://angel.co/abhriya-roy?public_profile=1"><img src="https://i.imgur.com/TiwMDMK.pngg" alt="Angel List" width=40 height=40></a>

<br>

## License

    Copyright 2019 Abhriya Roy

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
