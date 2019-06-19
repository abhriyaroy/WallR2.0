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
- [Features](#features) <br>
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
  - [JUnit](#junit)<br>
  - [Mockito](#mockito)<br>
  - [Espresso](#espresso)<br>
</p>

# Screenshots

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
  - [Testing](#testing)
     - [Unit Tests](#unit-tests)<br>
     - [Instrumentation Tests](#instrumentation-tests)<br>
     
  
 ## Architecture
 
 A proper app architecture implements the `SOLID` principles and ensures :-
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
  - <a href="https://github.com/abhriyaroy/WallR2.0/tree/develop/app/src/main/java/zebrostudio/wallr100/data">Repository</a> Pattern is     responsible to create an abstraction of the data sources from which the Use Cases get the data to act upon.
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
  - The original <a href="https://github.com/square/dagger">Dagger</a> was partly reflection-based, and didn’t play well with Proguard.     Dagger 2 however is based entirely on annotation processing, so it does the magic at compile time. It works without any additional       Proguard configuration, and is generally faster.
    
  Dependency graph for this project :-
  
  <br>
  <p align = "center"><img src="https://i.imgur.com/5l5vIhq.png" height=450></p>
  <br>
    
 ## Multi-threading 
 
 ### RxJava 2
 
   RxJava is a Java VM implementation of Reactive Extensions. The official doc describes Reactive Extension(ReactiveX) as a library for    composing <b>asynchronous</b> and <b>event-based</b> programs by using <b>observable sequences</b>.
   - Asynchronous: It implies that the different parts of a program run simultaneously.
   - Event-Based: The program executes the codes based on the events generated while the program is running.
   - Observable sequences: Publishers like Observable and Flowable take some items (Observable sequences) and pass onto its subscribers       so that they can inturn react to the incoming items.
  
   In this project, <a href="https://github.com/uber/AutoDispose">Autodispose<a> is used to dispose observables.
 
 ## Remote Data Source
 
 ### Unsplash API
 
   The Unsplash API is a modern JSON API that surfaces all of the info required for displaying various wallpapers to the users. For more    information, please click <a href="https://unsplash.com/developers">here</a>
 
 ### Firebase Realtime Database
 
   The Firebase Realtime Database is a cloud-hosted NoSQL database that lets us store and sync data between our users in realtime. The      basic structure of the firebase database for this project looks like :- 
   <p align="center"> <img src="https://i.imgur.com/WThrSrw.png" widht=450 height=350></a>
   
 ## Data Persistence
 
 ### Room Database
 
   Room is a database layer on top of an SQLite database which is used to store data locally on the device. Room takes care of mundane      tasks that is generally handled with an SQLiteOpenHelper. Room uses the DAO to issue queries to its database.
   <p align="center"> <img src="https://i.imgur.com/dxvdjSf.png" widht=250 height=200></a>
 
 ### Shared Preferences
 
   Shared Preferences is a way of storing data in Android. It allow us to save and retrieve data in the form of key,value pairs.
   
 ## Networking
 
 ### Retrofit
 
   Retrofit is a REST Client for Java and Android. It is used to retrieve JSON data via a REST based webservice. It also helps in data      serialization using the gson converter. Retrofit uses the OkHttp library for HTTP requests.
 
 ## Testing
 
 ### Unit Tests
   TDD (Test Driven Development) is an effective way of developing the applicaion by incrementally adding the code and writing tests.      Unit tests in this project are written using the Mockito framework and run using the JUnit runner.
   
   #### JUnit
   
   #### Mockito
   
   <p align="center"><img src="https://i.imgur.com/IxDnxIt.png" height = 200></a>
 
 ### Instrumentation Tests
   User interface (UI) testing lets you ensure that your app meets its functional requirements and achieves a high standard of quality      such that it is more likely to be successfully adopted by users. <br>
   One approach to UI testing is to simply have a human tester perform a set of user operations on the target app and verify that it is   behaving correctly. However, this manual approach can be time-consuming, tedious, and error-prone. A more efficient approach is to       write UI tests such that user actions are performed in an automated way. The automated approach allows us to run tests                   quickly and reliably in a repeatable manner.
   
   #### Espresso
 
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
  
 
## Project Setup Notes
- The google-services.json file has been purposely ignored by git due to security purposes. Please login to <a href="https://console.firebase.google.com/u/0/?gclid=CjwKCAjw__fnBRANEiwAuFxET6VRIbt1VaeN3D_DAYAU3rAwAC1uJGY1FaKZvmWSTe8bkiGe8lRAPBoCd0QQAvD_BwE">Firebase console</a> and create a new project and obtain your own google-services.json file and paste it at app/src/debug to set it up and running.<br> For more information, please refer the <a href="https://firebase.google.com/docs/android/setup">docs</a>.

## Acclamations

  WallR was selected as one of the best customization apps by <a href="https://www.androidauthority.com/5-android-apps-you-shouldnt-       miss-this-week-android-apps-weekly-review-90-796074">Android Authority</a>, <a href="https://www.androidpolice.com/2017/08/21/11-new-   notable-1-wtf-android-apps-last-week-81517-82117/">Android Police</a>. It also recieved huge number of warm and positive feedbacks and   reviews at <a href="https://forum.xda-developers.com/android/apps-games/app-wallr-wallpapers-beta-testers-t3568221">XDA Developers       Community</a>.

  <a href='https://play.google.com/store/apps/details?id=zebrostudio.wallr100&hl=en_IN&pcampaignid=MKT-Other-global-all-co-prtnr-py-       PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height=60 width = 145/></a>

## Libraries

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
