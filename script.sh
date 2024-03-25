#!/bin/bash

# The directory to process
BASEDIR="src"

# Function to rename directories and files
rename_dirs_files() {
  find "$1" -depth -name '*person*' | while IFS= read -r path; do
    # Compute new path by replacing Person with Client
    local newpath="${path//person/client}"
    echo "Renaming $path to $newpath"
    mv "$path" "$newpath"
  done
}

# Function to replace content inside files
replace_content() {
  find "$1" -type f -exec sh -c '
        for file do
            # Using sed to replace Person with Client, handling differences in -i option usage
            if sed --version 2>/dev/null | grep -q GNU; then
                # GNU sed (Linux)
                sed -i "s/PERSON/CLIENT/g" "$file"
            else
                # BSD sed (macOS)
                sed -i "" "s/PERSON/CLIENT/g" "$file"
            fi
        done
    ' sh {} +
}

# Start processing
echo "Starting to rename directories and files in $BASEDIR..."
rename_dirs_files "$BASEDIR"

echo "Starting to replace 'Person' with 'Client' inside files..."
replace_content "$BASEDIR"

echo "Processing completed."
