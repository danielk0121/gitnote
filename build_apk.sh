#!/bin/bash

# Build the APK
echo "Building APK..."
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
    
    # Define source and destination
    APK_SRC="app/build/outputs/apk/debug/app-debug.apk"
    TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
    APK_DEST="app/build/outputs/apk/debug/gitnote_${TIMESTAMP}.apk"
    
    # Rename APK (Copy to preserve original)
    if [ -f "$APK_SRC" ]; then
        cp "$APK_SRC" "$APK_DEST"
        echo "APK created and renamed to: $APK_DEST"
        
        # Placeholder for Google Drive upload
        echo "---------------------------------------------------"
        echo "Ready to deploy!"
        echo "File: $APK_DEST"
        echo "Please upload this file to your Google Drive shared folder."
        echo "---------------------------------------------------"
    else
        echo "Error: APK file not found at $APK_SRC"
        exit 1
    fi
else
    echo "Build failed!"
    exit 1
fi
