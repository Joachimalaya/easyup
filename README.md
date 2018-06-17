## EasyUp
EasyUp is a desktop application for uploading videos to YouTube similar to the webpage on YouTube itself.

### Limitations
Unfortunately there are features, that are available on the webpage, that can not be accessed by the API. Namely with current version of the API it is not possible to set the video category and associated information.

### Aborted uploads and deleting them
EasyUp will look for failed uploads associated with the users account on application start. Recognising failed uploads is quite tricky though and YouTube can take days to mark an upload as failed. Easyup only checks the last five uploads to save API quota. Thus you may want to remove failed uploads with the webpage if you upload several videos in a short amount of time.