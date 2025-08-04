# PeaceMind – Real-Time Stress Detection Android App

PeaceMind is an intelligent Android application that performs real-time stress detection using facial expression analysis powered by image processing and machine learning. It aims to help users identify their stress levels based on emotional cues and provide instant stress management suggestions.

This app leverages a TensorFlow Lite (TFLite) deep learning model (CNN-based) to classify facial emotions such as Happy, Sad, Angry, Neutral, etc., and deduce stress levels based on repeated negative emotions. It uses CameraX for live face detection, Firebase for authentication, and Retrofit for API communication with a Flask backend.

# Key Features:

Real-time emotion detection using CameraX and a custom-trained TFLite model

Stress classification based on frequency of negative emotions over time

Firebase Authentication for secure login and registration

Flask API integration using Retrofit for backend communication and emotion logging

Personalized stress management techniques and suggestions

Session tracking to monitor emotional trends

# Tech Stack:

Android Studio Chipmunk (Java-based)

OpenCV, CameraX, dlib for facial image preprocessing

TensorFlow Lite (TFLite) for CNN-based emotion classification

Firebase Authentication

Retrofit for API calls

Flask backend for data handling and emotional state analysis

# Purpose:

PeaceMind was designed as part of a research and development project to provide a practical tool for monitoring emotional well-being, particularly targeting IT professionals and students facing high-stress environments.

