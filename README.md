# aem-sample-exporter

These files are examples of custom page exporters that can be used in the preparing on-prem experiences. Additional customizations are required to the code files to align with the desired file path of your local AEM instance in order to register the packages appropriately in your AEM experience. Two main changes are required to the sample files included in this repo:

1. Package Path:
Line 1 of each file should be readjusted to match the file path where the file is stored within the AEM project. Additional code lines that should be modified are indiciated by {{REPLACE ME}} placeholders within the code file.

2. Data Properties:
Adjust the data properties that are being returned as required. Your AEM instance may return more or less data properties than what is available in the example file.
