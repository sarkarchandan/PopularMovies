#PopularMovies

#####Popular Movies is an assignment project of the Android Fast Track program designed by udacity and co-created by Google. This project is currently in Stage - II where the public api offered by [The Movie Database](https://www.themoviedb.org/) website has been used. This api exposes the metadata about movies and tv-shows in JSON format.

#####Movies are categorized into Popular and Top-Rated categories. Categories are separated by Tabs. Swipe behavior is not yet available. Hence Tabs have to be touched to interact. Upon tapping on Movie poster a detail page will be opened which will display generic information about the Movie along with Trailers and available Reviews, again separated by Tabs.

#####A material "favorite icon" has been incorporated at the Detail screen to set a Movie as favorite. A collection of All such favorite movies are available in the Favorite page which can be toggled from the Main screen using the overflow menu button. In the Favorite page, favorite movie posters are available, again in grid. Details of a favorite Movie can also be Viewed from tapping on Movie poster in the Favorite screen. Movie posters can be swiped out of the favorite movie collection left or right, if they are no longer "so favorite".

####External Libraries Used
- [Android Support Design Libarry](https://developer.android.com/topic/libraries/support-library/index.html)
- [Butterknife](http://jakewharton.github.io/butterknife/)
- [Picasso](http://square.github.io/picasso/)
- [Firebase JobDispatcher](https://github.com/firebase/firebase-jobdispatcher-android#user-content-firebase-jobdispatcher-)
- [Google Volley](https://developer.android.com/training/volley/index.html)


##### This Project is currently in development. It could result in unexpected behavior.


##### Following are some sample executions:
<img src="https://cloud.githubusercontent.com/assets/19269229/24726064/5e9b6f38-1a51-11e7-8e6e-9b00dfeb561f.png" width="150" height="250">
<img src="https://cloud.githubusercontent.com/assets/19269229/24726089/71961480-1a51-11e7-9c1a-35ba86c0a214.png" width="150" height="250">
<img src="https://cloud.githubusercontent.com/assets/19269229/24726115/82f10640-1a51-11e7-8356-7ecf08df2310.png" width="150" height="250">
<img src="https://cloud.githubusercontent.com/assets/19269229/24726130/903b3d0c-1a51-11e7-9a60-9e522fbc959f.png" width="150" height="250">


##Authentication
#####In order to request for the movies data using the movie database api we need an API Key which is unique to every user account registered with the movie database. The user need to [register](https://www.themoviedb.org/account/signup) with the movie database.
#####Post registration and successful email verification, user will be able to request for a free api key. 
#####User needs to state that the usage will be for educational/non-commercial use. The user will also need to provide some personal information to complete the request. Once the request is submitted, the user should receive the key via email shortly after. While sending the request for the movie data to the appropriate endpoint the api-key must be included as a query parameter to the uri.


##Build-Execution
#####Post obtaining the api-key, following steps should be performed to build and execute the project:

```
- Clone the public repository
	: git clone https://github.com/sarkarchandan/PopularMovies.git
- Import the project in the Android Studio and go to the gradle.properties (Project Properties) file.
- Place the obtained api-key to the field TMDB_API_KEY.
	:TMDB_API_KEY="Your API Key Goes Here"
- Sync The Project.
- Build in Android Studio and run the App in the Android Emulator or any android device.
```


##Ongoing/Upcoming development
##### This project is being actively worked upon, to incorporate fresh design features to conform with Material Design Guidelines. Along with that the implementation of other useful libraries and design paradigms, such as 
- [RxAndroid](https://github.com/ReactiveX/RxAndroid)
- [MVP](https://github.com/googlesamples/android-architecture)
- [Material Dialogue](https://github.com/afollestad/material-dialogs)
- [Dagger 2](https://github.com/google/dagger)

are being incorporated to ensure a richer user experience.
