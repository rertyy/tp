#!/usr/bin/bash

# Function to rename directories
rename_directory() {
  local old_dir="$1"
  local new_dir="$2"

  mv "$old_dir" "$new_dir"
}

# Function to replace string in file contents
replace_in_file() {
  local file="$1"
  local old_str="$2"
  local new_str="$3"

  sed -i "s/$old_str/$new_str/g" "$file"
}

# Function to recursively rename directories and replace string in file contents
rename_and_replace() {
  local directory="$1"
  local old_str="$2"
  local new_str="$3"

  # Rename directories
  find "$directory" -type d -depth -exec bash -c 'rename_directory "$0" "${0//'"$old_str"'/'"$new_str"'}"' {} \;

  # Replace string in file contents
  find "$directory" -type f -print0 |
    while IFS= read -r -d '' file; do
      replace_in_file "$file" "$old_str" "$new_str"
    done
}

# Usage: ./rename_and_replace.sh <directory_path> <old_string> <new_string>
rename_and_replace "$1" "$2" "$3"
