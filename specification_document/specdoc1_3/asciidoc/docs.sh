#!/bin/bash
clear
INPUT_DIR="."
OUTPUT_DIR="out"
COMMIT_HASH="*COMMITHASH*"
BUILD_DATE="*BUILDDATE*"
MAIN_ADOC_FILE="mzidentml.adoc"
EXT_ADOC_FILE="crosslinking_ext.adoc"

# Check if the output directory exists, if not, create it
mkdir -p "$OUTPUT_DIR"
# Clear the output directory safely
rm -R "${OUTPUT_DIR:?}"/*
echo "Cleared directory: $OUTPUT_DIR"
# Copy image files to the output directory
mkdir -p $OUTPUT_DIR/img
cp -R $INPUT_DIR/img/* $OUTPUT_DIR/img/
echo "Copied image files to $OUTPUT_DIR/img"
echo
##Copy css files to the output directory
cp $INPUT_DIR/asciidoctor-default.css $OUTPUT_DIR/asciidoctor-default.css
#echo "Copied css files to $OUTPUT_DIR"

echo "Building main specification doc."

echo
echo "Building HTML5 version of $INPUT_DIR/$MAIN_ADOC_FILE in $OUTPUT_DIR"
asciidoctor -v -w --attribute="allow-uri-read" --attribute="missing-image-warning" \
    -d book --attribute="commit-hash=$COMMIT_HASH" --attribute="build-date=$BUILD_DATE" \
    -D "$OUTPUT_DIR"  -a stylesheet=crosslinking_ext.css  "$MAIN_ADOC_FILE"
ECODE=$?
if [ ! $ECODE -eq 0 ]; then
  echo "Build failed with exit code $ECODE"
  exit $ECODE
fi

echo
echo "Building PDF version of $INPUT_DIR/$MAIN_ADOC_FILE in $OUTPUT_DIR"
asciidoctor-pdf -v -w --attribute="allow-uri-read" --attribute="missing-image-warning" \
    -d book --attribute="commit-hash=$COMMIT_HASH" --attribute="build-date=$BUILD_DATE" \
    -D "$OUTPUT_DIR" -a pdf-theme=crosslinking_ext.yml "$MAIN_ADOC_FILE"
ECODE=$?
if [ ! $ECODE -eq 0 ]; then
  echo "Build failed with exit code $ECODE"
  exit $ECODE
fi

echo
echo "Building docbook and DOCX version of $INPUT_DIR/$MAIN_ADOC_FILE in $OUTPUT_DIR"
OUTPUT_DOCBOOK="${MAIN_ADOC_FILE%.*}.xml"
OUTPUT_DOCX="${MAIN_ADOC_FILE%.*}.docx"
asciidoctor -v -w -d book --backend docbook --attribute="commit-hash=$COMMIT_HASH" --attribute="build-date=$BUILD_DATE" -D $OUTPUT_DIR $MAIN_ADOC_FILE
echo "Running pandoc to convert from $OUTPUT_DOCBOOK to $OUTPUT_DOCX in $OUTPUT_DIR"
CDIR="$(pwd)"
cd $OUTPUT_DIR
pandoc --from docbook --to docx --output $OUTPUT_DOCX $OUTPUT_DOCBOOK
cd $CDIR
ECODE=$?
if [ ! $ECODE -eq 0 ]; then
  echo "Build failed with exit code $ECODE"
  exit $ECODE
fi



echo "Building crosslinking extension doc."

echo
echo "Building HTML5 version of $INPUT_DIR/$EXT_ADOC_FILE in $OUTPUT_DIR"
asciidoctor -v -w --attribute="allow-uri-read" --attribute="missing-image-warning" \
    -d book --attribute="commit-hash=$COMMIT_HASH" --attribute="build-date=$BUILD_DATE" \
    -D "$OUTPUT_DIR" -a stylesheet=crosslinking_ext.css "$EXT_ADOC_FILE"
OUTPUT_HTML="${EXT_ADOC_FILE%.*}.html"
OUTPUT_TXT="${EXT_ADOC_FILE%.*}.txt"
# pandoc -s -t plain '../mzIdentML 1.3.0 Crosslinking Extension 1.0.0 release/mzIdentML1.3.0CrosslinkingExtension1.0.0release.html' -o extorig.txt
pandoc -s -t plain "$OUTPUT_DIR/$OUTPUT_HTML" -o "$OUTPUT_DIR/$OUTPUT_TXT"
ECODE=$?
if [ ! $ECODE -eq 0 ]; then
  echo "Build failed with exit code $ECODE"
  exit $ECODE
fi

echo
echo "Building PDF version of $INPUT_DIR/$EXT_ADOC_FILE in $OUTPUT_DIR"
#rm cl_ext_no_default_import.css
#tail -n +3 crosslinking_ext.css > cl_ext_no_default_import.css
asciidoctor-pdf -v --attribute="allow-uri-read" --attribute="missing-image-warning" \
    -d book --attribute="commit-hash=$COMMIT_HASH" --attribute="build-date=$BUILD_DATE" \
    -D "$OUTPUT_DIR" -a pdf-theme=crosslinking_ext.yml "$EXT_ADOC_FILE"
ECODE=$?
if [ ! $ECODE -eq 0 ]; then
  echo "Build failed with exit code $ECODE"
  exit $ECODE
fi

echo
echo "Building docbook and DOCX version of $INPUT_DIR/$EXT_ADOC_FILE in $OUTPUT_DIR"
OUTPUT_DOCBOOK="${EXT_ADOC_FILE%.*}.xml"
OUTPUT_DOCX="${EXT_ADOC_FILE%.*}.docx"
asciidoctor -v -w -d book --backend docbook --attribute="commit-hash=$COMMIT_HASH" --attribute="build-date=$BUILD_DATE" -D $OUTPUT_DIR $EXT_ADOC_FILE
echo "Running pandoc to convert from $OUTPUT_DOCBOOK to $OUTPUT_DOCX in $OUTPUT_DIR"
CDIR="$(pwd)"
cd $OUTPUT_DIR
pandoc --from docbook --to docx --output $OUTPUT_DOCX $OUTPUT_DOCBOOK
cd $CDIR
ECODE=$?
if [ ! $ECODE -eq 0 ]; then
  echo "Build failed with exit code $ECODE"
  exit $ECODE
fi


