<h1 align="center">
  <a href="#"><img src="./images/glimpse_logo.webp" alt="Glimpse" height="250px"></a>
  <br>
  Glimpse-Android
  <br>
</h1>
<h4 align="center">Show your products using Augmented Reality</h4>



<!--# Glimpse-Android-->
<!--![Optional Text](/images/glimpse_logo.webp)-->

Contains working code for [Glimpse](https://play.google.com/store/apps/details?id=com.glimpse.app). The app can be used to have a look at 3D models of furniture or other home appliances.
The app is built using following tech:
1. [Sceneform](https://github.com/google-ar/sceneform-android-sdk) for Augmented Reality
2. Firebase for backend DB
<p align="center" float="left">
  <img src="./images/glimpse_example1.webp" width="100" />
  <img src="./images/glimpse_example2.webp" width="100" />
</p>

## How to Use
1. Open the project in Android Studio and Build
**Note: The app won't work on the emulator because of phone sign in**
2. By Default it'll use our own firebase DB for login
3. Login and select a store
4. Select a product
5. Now move you phone side to side with camera facing a plane
6. Tap on the plane to place the product


## How to make Changes
1. Replacing the Database:
   1. With Firebase: replace `app/google-services.json` with the one generated with your own firebase project
   2. With Some other DB: You'll need to make changes wherever Firebase client is being called, for example getting the stores, getting products within a store etc
2. Using my own models: integrate your own database and upload your models there


## License
MIT

