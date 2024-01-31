# Earthlink App
### Project for CS4278 - Principles of Software Engineering

This project is a new social networking app that emphasizes location-based messaging using the Google Maps API. This unique feature enables users to leave messages tied to specific physical locations that can be accessed by others within a small range. The intention is to integrate conventional social networking features with geolocation to create a richer and more contextual user experience.

<img src="https://github.com/CornCube/earthlink-app/blob/main/assets/home.png" width="180"> <img src="https://github.com/CornCube/earthlink-app/blob/main/assets/view.png" width="180"> <img src="https://github.com/CornCube/earthlink-app/blob/main/assets/post.png" width="180"> <img src="https://github.com/CornCube/earthlink-app/blob/main/assets/profile.png" width="180"> <img src="https://github.com/CornCube/earthlink-app/blob/main/assets/settings.png" width="180">

This project has been completed for the purposes of the CS4278 class, so the backend is not being hosted currently - to run locally, a user needs to first clone the repo referenced at the bottom and run it according to the instructions there.

This project also utilizes various Google icons and Material Design components, which can be found at the following websites:
- https://m3.material.io/components
- https://fonts.google.com/icons

### Technologies Used
- Android Studio
- Kotlin
- Jetpack Compose

### Features
#### Location-Based Messaging

Earthlink distinguishes itself by enabling users to leave messages associated with specific physical locations. These messages can be accessed by other users within a small range, adding a layer of context to social interactions. If messages overlap with each other within a very small range, they are dynamically grouped into a list of messages that can be viewed by clicking on the top-most message. Users can also like/dislike messages, adding an extra layer of interactivity.

#### Profile Customization

Users can easily change their profile to fit their needs, as well as view/delete messages that they have posted in the past. 

#### App Preferences

Several map themes are built into the app, with overall color scheming being determined by the system light/dark modes. Users can also select between different levels of profanity they would like to filter out to engage with only the content they want to see.

Backend API Link:

https://github.com/ShinBlake/EarthLinkAPI
