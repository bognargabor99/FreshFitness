# Fresh & Fitness

![Android workflow](https://github.com/bognargabor99/FreshFitness/actions/workflows/android.yml/badge.svg)

This repository contains the source code for my thesis. The title of the task is _Fitness application using Android and Amazon Web Services_.
## Why?
We can find [studies](https://www.health.harvard.edu/blog/regular-exercise-changes-brain-improve-memory-thinking-skills-201404097110) on the internet on the correlation between physical exercise and our brain's cognitive ability. The performance of the human brain peaks at the age of 25 and after it decreases linearly. This phenomenon, even if it cannot be completely avoided, can be reduced by maintaining a physically active lifestyle.
On the other hand, I consider myself a physically active person, who tries to become more and more healthy over time. During my life I tried many sports - such as football (soccer), swimming, tennis and calisthenics. My calisthenics experiences helped me in becoming a stronger, better and  - most importantly - a more disciplined person.
For these reasons I wanted to create my own mobile application that can help me and others stay on a healthy track. 

## Project description
The application uses AWS services as backend with the following architecture. 

![aws-arch drawio](https://github.com/bognargabor99/FreshFitness/assets/61208246/2e419f21-6089-46b7-bab6-5cb43fcb1b73)

The Android application and the backend is connected via Amazon Amplify, which helps application development on different platforms for AWS. With its libraries and command-line interface (CLI) the client application can be easily connected to cloud services. Amplify communicates directly with 3 services:
- AWS Cognito: User and session management
- AWS S3: Storing media files for posts and exercises
- AWS API Gateway: Fetching and posting data. (posts, images, exercises)
The other services can be reachable through API Gateway:
- Lambda functions: The API Gateway calls Lambda functions to communicate with the database
- AWS Relational Database Service: Via this service I created a MySQL database for the application. In this database I mostly use stored procedures to make the interaction with the database simpler.

The application focuses on physical activities. Here is a brief summary about the functionalities of the application:
- __User management__: Management of users with registration, login and resetting password. Also you can set your profile picture once you log in.
- __Social feed__: Post your pictures of working out, ask for help or search for a training partner. Also, comment and like on posts. Viewing the posts is enabled for everybody, while posting, commenting and liking is for authorized users only.
- __Exercise__
	- Search for nearby gyms on the __Nearby Gyms__ screen. Enable your location for the app, set the maximum distance of the place and the gyms will pop-up.
	- Track your running route on the __Track Running__ screen. Get your running shoes and start tracking your route, while locking your phone. At the end, stop by clicking on the notification action which saves your route. View your run on a map and share it on the social feed.
	- View all the available exercises (80+) and their details on the __Exercise Bank__ screen. Each exercise has an illustration (image, video, YouTube video), details, equipment, targeted muscle, difficulty etc.
	- View and create workouts on the __Workout Planning__ screen. Workouts have exercises, optional warmup exercises, target muscle, date and an owner. There are also daily workouts created by the administrators available for everyone. Authorized users can create their own workouts with specifying the target muscle, difficulty and other settings. After reviewing, the workout is created and available on all devices that the user is logged in. Workouts can be scheduled in the device's calendar, and viewed on __Schedule__ screen.

## Images

### Light vs dark mode and calendar event

| ![screenshot_schedule_light](https://github.com/bognargabor99/FreshFitness/assets/61208246/aa5989ba-2dfe-46ec-a7f0-37f0132ffa38) | ![Screenshot_20231203_210509](https://github.com/bognargabor99/FreshFitness/assets/61208246/e6446463-7d72-4899-ba87-ae63a1ffeb94) | ![screenshot_schedule_dark](https://github.com/bognargabor99/FreshFitness/assets/61208246/76f289b1-5786-4b2e-895d-660f0080bddd) | 
|:-:|:-:|:-:|
| Light mode | Calendar event | Dark mode |

### Example workout and Schedule screen

| ![screenshot_workout](https://github.com/bognargabor99/FreshFitness/assets/61208246/db0348c7-a388-44f3-b8fd-55666e86947c) | ![schedulescreen_listanddetail](https://github.com/bognargabor99/FreshFitness/assets/61208246/c0efa2cc-9150-469a-86c6-2078aefdc8ce) | ![schedulescreen_listonly](https://github.com/bognargabor99/FreshFitness/assets/61208246/9e46f9b0-98aa-466f-b434-7724fbe1cf35) |
|:-:|:-:|:-:|
| Example workout | Schedule screen on tablet | Schedule screen on mobile |
