# Popular movies

This project is built on top of AAC MVVM pattern, it fetches data from TMDB API and stores in local SQLlite db, and makes use of LiveData / MutableLiveData to notify observers for the API return result/ search result.

Currently this app can:
```
1. Load data from TMDB API
2. Display movie titles as CardViews in GridLayout
3. Scroll to load more data
4. Swipe to reload list
5. Search title from existing list
```

This app uses Retrofit library to fetch data, and Picasso to download and display images.

### Screenshots

#### Reload
![marlinppr2 181005 003mankitt10032018104347](https://user-images.githubusercontent.com/654012/46387850-7feb0600-c6fb-11e8-87ef-09e835fe18c5.gif)

#### Fetch more data
![marlinppr2 181005 003mankitt10032018104536](https://user-images.githubusercontent.com/654012/46387851-80839c80-c6fb-11e8-87e8-9120b728090a.gif)

#### Search current list
![marlinppr2 181005 003mankitt10032018104616](https://user-images.githubusercontent.com/654012/46387852-80839c80-c6fb-11e8-86b9-6a9095ccb65c.gif)
