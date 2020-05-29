# CHANGELOG

- fix bug in IndexService when no songNumber was found in the metadata
- improve SearchActivity (Toasts when something is wrong, re-initialize search before making a new search)
- fix duplicated rows in database by using a unique hash for each song
- lock app orientation to portrait since interfaces doesn't work in landscape mode
