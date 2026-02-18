#!/bin/bash
V_DIR="./examples/"
V_FAILED=()
# run validation for all example files
for i in $(find "$V_DIR" -maxdepth 3 -iname '*.mzid'); do
  echo -e "################################################################################"
  echo -e "# Starting basic validation of $i"

  # Extract version from XML file's version attribute (e.g., version="1.2.0")
  VERSION=$(grep -oP 'version="\K[0-9]+\.[0-9]+\.[0-9]+' "$i" | head -1)

  # Default to 1.3.0 if not found
  if [ -z "$VERSION" ]; then
    VERSION="1.3.0"
    echo "# Warning: Could not determine version from file, using default 1.3.0"
  fi

  SCHEMA="./schema/mzIdentML${VERSION}.xsd"

  # Verify schema file exists
  if [ ! -f "$SCHEMA" ]; then
    echo "# Warning: Schema $SCHEMA not found, using default 1.3.0"
    SCHEMA="./schema/mzIdentML1.3.0.xsd"
  fi

  # Run validation and capture output
  echo "# Using schema: $SCHEMA"
  ERRORS=$(xmllint --noout --schema "$SCHEMA" "$i" 2>&1)
  RESULT=$?

  if [ $RESULT -ne 0 ]; then
    echo -e "# VALIDATION FAILED"
    echo -e "# Errors:"
    echo "$ERRORS" | while IFS= read -r line; do
      echo "#   $line"
    done
    V_FAILED+=($i)
  else
    echo -e "# Validation successful"
  fi
  echo -e "################################################################################"
done

if [ ${#V_FAILED[@]} -ne 0 ]; then
  echo -e "################################################################################"
  echo -e "# Validation failed for ${#V_FAILED[@]} files! Please check the following files:"
  for i in "${V_FAILED[@]}"
  do
   echo "# $i"
  done
  echo -e "################################################################################"
  exit 3
else
  echo -e "################################################################################"
  echo -e "# Validation of all files successful!"
  echo -e "################################################################################"
  exit 0
fi
