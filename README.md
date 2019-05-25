<p align="center">
 <a href="https://play.google.com/store/apps/details?id=zebrostudio.wallr100">
 <img src="https://i.imgur.com/PWzM7sn.png" alt="Explore Wallpapers" width=800 height=400 hspace="2">
 </a>
</p>

# HD Wallpapers from WallR
Did you know that an average user checks their device more than 80 times a day? Make each time a real pleasure with beautiful HD wallpapers from WallR. Let your device be a treat to your eyes every-time you check it.

## Table of Contents
- [Introduction](#introduction) <br>
- [Screenshots](#screenshots) <br>
- [Insights](#insights) <br>
- [Features](#features) <br>
- [Usage Tips](#usage-tips) <br>
- [Project Setup Notes](#project-setup-notes) <br>
- [Acclamations](#acclamations) <br>
- [References](#references) <br>

## Introduction
<p>
WallR is an open-source Android wallpaper app written in <b>Kotlin</b>, keeping in mind proper coding guidelines and app architecture so that it is easy to maintain and scale.<br>
It comes with a plethora of customization options like editing a wallpaper, crystallizing or even making a new minimal wallpaper. WallR also comes with an automatic wallpaper changer which keeps changing wallpapers automatically once enabled.
</p>

## Screenshots
<p align="center">
  <img src="https://i.imgur.com/xlTXmLD.jpg" alt="Explore Wallpapers" width=425 height=650 hspace="2">
  <img src="https://i.imgur.com/tCYtXOg.jpg" alt="Wallpaper Categories" width=425 height=650 hspace="2">
</p>
<p align="center">
  <img src="https://i.imgur.com/xDNyaOn.png" alt="Collections" width=425 height=650 hspace="2">
  <img src="https://i.imgur.com/bODjKaX.jpg" alt="Search Wallpapers" width=425 height=650 hspace="2">
</p>
<p align="center">
  <img src="https://i.imgur.com/BfUPBLr.jpg" alt="Minimal Wallpapers" width=425 height=650 hspace="2">
  <img src="https://i.imgur.com/2fJsYzR.jpg" alt="Wallpaper Details" width=425 height=650 hspace="2">
</p>
 
## Insights
  - [Clean Architecture With MVP](#clean-architecture-with-mvp)<br>
  - [Dependency Injection Using Dagger2](#dependency-injection-using-dagger2)<br>
  - [Multi-threading Using RxJava2](#multi-threading-using-rxjava2)<br>
  - [Unsplash Api](#unsplash-api)<br>
  - [Firebase Realtime Database](#firebase-realtime-database)<br>
  - [Room Database](#room-database)<br>
  - [Shared Preferences](#shared-preferences)<br>
  - [Retrofit For Networking](#retrofit-for-networking)<br>
  - [JUnit And Mockito For Unit Testing](#junit-and-mockito-for-unit-testing)<br>
  - [Espresso For UI Tests](#espresso-for-ui-tests)<br>
  
 ### Clean Architecture With MVP
  In Clean Architecture, the code is separated into layers in an onion shape with one dependency rule: The inner layers should not know   anything about the outer layers. Inner layers contain business logic, whereas the outer layers contain implementation and the middle     layer contain Interface Adapters. Each ring represent one layer of abstraction.
  <p align="center">
  <img src="https://i.imgur.com/ZTLG6Ax.png" width=400 height=300>
  </p>
  
  <br>
 
  #### A diagram representing different layers, components and how they communicate with each other in the app :-
  
  <br>
  
  <p align="center">
  <img src="https://i.imgur.com/dHLjdiu.png" width=800 height=350>
  </p>
  
  #### Presentation Layer
  - MVP (Model View Presenter) is suitable for the presentation layer.
  - Views are dumb and implement Passive View pattern. It is a set of interfaces that could be implemented by any Android view, such as     Activities, Fragments, Adapters or Custom Views.
  - Presenter serve as a middleman between views (abstractions over Android specific components) and the business logic (interactors/Use     Cases). They handle user interactions, invoke appropriate business logic and send the data to the UI for rendering.
  - Presenter does not depend on Android classes hence improves testability.
  
  #### Domain Layer
  - A simple example of Use Case would be “Fetch new wallpapers”. Each Use Case is a reusable component that executes a specific             business logic. It fetches the data from a repository, executes the business logic and returns the result to the presenter.
  
  #### Data Layer (Database & API)
  - Repository Pattern is responsible to create an abstraction of the data sources from which the Use Cases get the data to act upon.
  - Business logic shouldn’t know where the data comes from.
 
 ### Dependency Injection Using Dagger 2
  - Dagger is a dependency injection framework, which makes it easier to manage the dependencies between the classes in our app.
  - It is a software design pattern that implements inversion of control for resolving dependencies.
  - The original Dagger was partly reflection-based, and didn’t play well with Proguard. Dagger2 however is based entirely on annotation     processing, so it does the magic at compile time. It works without any additional Proguard configuration, and is generally faster.
  - Implementing proper dependency injection in our apps allows us to have :
    - Testable classes.
    - Re-usable and interchangeable components.
  #### Dependency graph for this project :-
  
  <br>
  <p align = "center"><img src="https://i.imgur.com/5l5vIhq.png" height=450></p>
  <br>
    
 ### Multi-threading Using RxJava2
   RxJava is a Java VM implementation of Reactive Extensions. The official doc describes Reactive Extension(ReactiveX) as a library for    composing <b>asynchronous</b> and <b>event-based</b> programs by using <b>observable sequences</b>.
   - Asynchronous: It implies that the different parts of a program run simultaneously.
   - Event-Based: The program executes the codes based on the events generated while the program is running.
   - Observable sequences: Publishers like Observable and Flowable take some items (Observable sequences) and pass onto its subscribers       so that they can inturn react to the incoming items.
  <p align="center"> <img src="https://i.imgur.com/iGYOeYB.png" widht=450 height=300></a>
  <p align="center">Credits: <a href="https://dzone.com/articles/marble-diagrams-rxjava-operators">DZone</a></p>
  
   In this project, <a href="https://github.com/uber/AutoDispose">Autodispose<a> is used to dispose observables.
 
 ### Unsplash API
   The Unsplash API is a modern JSON API that surfaces all of the info required for displaying various wallpapers to the users. For more    information, please click <a href="https://unsplash.com/developers">here</a>
 
 ### Firebase Realtime Database
   The Firebase Realtime Database is a cloud-hosted NoSQL database that lets us store and sync data between our users in realtime. The      basic structure of the firebase database for this project looks like :- 
   <p align="center"> <img src="https://i.imgur.com/WThrSrw.png" widht=450 height=350></a>
 
 ### Room Database
   Room is a database layer on top of an SQLite database which is used to store data locally on the device. Room takes care of mundane      tasks that is generally handled with an SQLiteOpenHelper. Room uses the DAO to issue queries to its database.
   <p align="center"> <img src="https://i.imgur.com/dxvdjSf.png" widht=250 height=200></a>
 
 ### Shared Preferences
   Shared Preferences is a way of storing data in Android. It allow us to save and retrieve data in the form of key,value pairs.
 
 ### Retrofit For Networking
   Retrofit is a REST Client for Java and Android. It is used to retrieve JSON data via a REST based webservice. It also helps in data      serialization using the gson converter. Retrofit uses the OkHttp library for HTTP requests.
 
 ### JUnit And Mockito For Unit Testing
   TDD (Test Driven Development) is an effective way of developing the applicaion by incrementally adding the code and writing tests.      Unit tests in this project are written using the Mockito framework and run using the JUnit runner.
   <p align="center"><img src="https://i.imgur.com/IxDnxIt.png" height = 200></a>
 
 ### Espresso For UI Tests
   User interface (UI) testing lets you ensure that your app meets its functional requirements and achieves a high standard of quality      such that it is more likely to be successfully adopted by users. <br>
   One approach to UI testing is to simply have a human tester perform a set of user operations on the target app and verify that it is   behaving correctly. However, this manual approach can be time-consuming, tedious, and error-prone. A more efficient approach is to       write UI tests such that user actions are performed in an automated way. The automated approach allows us to run tests                   quickly and reliably in a repeatable manner.
 
 ## Features
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
  
## Usage Tips
 - Automatic wallpaper changer is only available to pro users and can be found inside collections.
 - The option to enable automatic wallpaper changer is only available upon adding atleast 2 images to collection.
 - Create your own assorted collection of wallpapers so that automatic wallpaper changer can cycle through them periodically after the      time interval (default - 30 mins) set by you.
 - You can reorder images in collection by holding down an image and then dragging and dropping it to the desired location.
 - Clearing the app by swiping from recent apps screen might lead to the automatic wallpaper changer malfunctioning.
 - If WallR does not show a notification stating that automatic wallpaper changer then please restart the app so that automatic            wallpaper changer can be restarted by the app itself.
 
## Project Setup Notes
- The google-services.json file has been purposely ignored by git due to security purposes. Please login to <a href="https://console.firebase.google.com/u/0/?gclid=CjwKCAjw__fnBRANEiwAuFxET6VRIbt1VaeN3D_DAYAU3rAwAC1uJGY1FaKZvmWSTe8bkiGe8lRAPBoCd0QQAvD_BwE">Firebase console</a> and create a new project and obtain your own google-services.json file and paste it at app/src/debug to set it up and running.<br> For more information, please refer the <a href="https://firebase.google.com/docs/android/setup">docs</a>.

## Acclamations

WallR was selected as one of the best customization apps by <a href="https://www.androidauthority.com/5-android-apps-you-shouldnt-miss-this-week-android-apps-weekly-review-90-796074">Android Authority</a>, <a href="https://www.androidpolice.com/2017/08/21/11-new-notable-1-wtf-android-apps-last-week-81517-82117/">Android Police</a>. It also recieved huge number of warm and positive feedbacks and reviews at <a href="https://forum.xda-developers.com/android/apps-games/app-wallr-wallpapers-beta-testers-t3568221">XDA Developers Community</a>.

<a href='https://play.google.com/store/apps/details?id=zebrostudio.wallr100&hl=en_IN&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height=60 width = 145/></a>

## References

<a href="https://kotlinlang.org/docs/reference/">Kotlin</a>,
<a href="https://github.com/ReactiveX/RxJava">RxJava 2</a>,
<a href="https://unsplash.com/">Unsplash</a>,
<a href="https://github.com/square/retrofit">Retrofit</a>,
<a href="https://github.com/uber/AutoDispose">Autodispose</a>,
<a href="https://github.com/google/dagger">Dagger 2</a>,
<a href="https://github.com/mockito/mockito">Mockito</a>,
<a href="https://github.com/junit-team/junit4">JUnit 4</a>,
<a href="https://github.com/GrenderG/Toasty">Toasty</a>,
<a href="https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout">WaveSwipeRefreshLayout</a>,
<a href="https://github.com/ybq/Android-SpinKit">Android-SpinKit</a>,
<a href="https://github.com/ogaclejapan/SmartTabLayout">SmartTabLayout</a>,
<a href="https://github.com/afollestad/material-dialogs">Material Dialogs</a>,
<a href="https://github.com/bumptech/glide">Glide</a>,
<a href="https://github.com/wasabeef/recyclerview-animators">Recyclerview Animator</a>,
<a href="https://firebase.google.com/docs/android/setup">Firebase</a>,
<a href="https://github.com/umano/AndroidSlidingUpPanel">Sliding up panel</a>,
<a href="https://github.com/hdodenhof/CircleImageView">Circle ImageView</a>,
<a href="https://github.com/KeepSafe/TapTargetView">TapTarget View</a>,
<a href="https://github.com/jaredrummler/MaterialSpinner">Material Spinner</a>,
<a href="https://github.com/MLSDev/RxImagePicker">Rx ImagePicker</a>,
<a href="https://github.com/chrisbanes/PhotoView">Photoview</a>,
<a href="https://github.com/Yalantis/uCrop">Ucrop</a>,
<a href="https://github.com/Yalantis/GuillotineMenu-Android">Guillotine-Menu</a>,
<a href="https://github.com/skydoves/ColorPickerView">Colorpicker view</a>

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