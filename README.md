# Sharier-Final-App
Share Images, Videos and PDFs among your Friends and Users.
Libraries Imported:
PICASSO - To display Images.
ExoPlayer - For Video Controls(Play/Pause), Fullscreen, Buffering
Video Compressor Module - for compressing Videos.
Firebase - To implement Firebase Storage, Database and Authentication

Plan:
1) Multiple users can **Register** and **Login** on to the app.
2) Create their own folders in which they can store their Videos, Images and Documents.
3) 3 floating buttons for uploading Images, Videos and documents respectively.
4) Images and Videos will get **Compressed** when Uploading without affecting their Quality.
5) Different Fragments for Images, Videos and Documents are made.
6) Navigating to the menu, find the **Users List**, all the users of this app will be available in this section.
7) **Search** for the particular user you want to see the contents.
8) On clicking for a particular user, it will ask you to input the required folder name you want to access.
9) On typing the correct folder name (case-sensitive), it will give access to its media.
10) **Download** the images directly to your local device (or) **Share** the link to that image with others via Social media.
11) **Streaming** the video directly through the app (or) **Download** it (or) **Share** the video link via social media.
12) **Download** or **View** the PDF documents from the Documents Fragment.
13) After your work is completed, click on the **Logout** button on the menu to log out of the app.

PROCESS

1) Client and User separate Registration and Login pages.
2) 


Picasso - for images
For compressing images - bitmap
for compressing video= videocompressor library (module)
exoplayer - for video controls/player ....
anstron core helper - to help jetpack libraries to import

image compressing line
bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputstream);



