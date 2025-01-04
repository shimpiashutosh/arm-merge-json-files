# CLI utility to combine multiple JSON files into a single JSON file, order records first by vendor and then by name

## Purpose
This is a command-line tool designed to combine JSON data from multiple source files. It allows users to merge JSON files in a single JSON file order by vendor followed by name.

---

## Prerequisites

Before you can build and run this project, ensure that the following tools/softwares are installed on your system:

### 1. **Java Development Kit (JDK)**
- **Version**: Java 19 or later (Tested on version 19).
- **Installation**:
    - [Download JDK from Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://openjdk.java.net/).
    - Set `JAVA_HOME` environment variable to java installation path. You can check whether it has already been set. 
      ```
      # Unix system
      echo $JAVA_HOME
      
      # Windows system
      echo %JAVA_HOME%
      ```
      if it is empty, then follow below steps according to your system. This is shell/commandline local variable.
      ```
      # Unix system
      export JAVA_HOME=/path/to/jdk/installation
      
      # Windows system
      set JAVA_HOME=/path/to/jdk/installation
      ```
      In case you want more details, you can follow [Set JAVA_HOME Variable](https://www.baeldung.com/java-home-on-windows-mac-os-x-linux).
- **Verify installation**:
  ```bash
  java -version
  ```

### 2. **Apache Maven** [optional]
(This is an optional step, you can use maven wrapper included in this project, refer to [Build the project](#how-to-build) section)
- **Version**: Maven 3.9.1 or later (Tested on version 3.9.1, you can try lower version if it works).
- **Installation**:
    - [Download Apache Maven](https://maven.apache.org/download.cgi).
    - Follow the installation steps and set the `M2_HOME` and `JAVA_HOME` environment variables if not set.
- **Verify installation**:
  ```bash
  mvn -v
  ```

### 3. **Git**
- **Version**: git 2.39 or later (Tested on version 2.39.5, you can try lower version if it works).
- **Installation**:
    - [Download Git](https://git-scm.com/downloads).
    - Follow the installation steps.
- **Verify installation**:
  ```bash
  git -v
  ```
---

## How to build?

Follow these steps to build the project:

1. **Clone the repository**:
   If you haven't already, clone the project repository to your local machine.
   ```bash
   # Clone git repo.
   git clone https://github.com/shimpiashutosh/arm-merge-json-files.git
   
   # Get into cloned directory.
   cd arm-merge-json-files
   ```

2. **Build the project**: [2 options, either can be used]
   1. Use Maven bundled in this project to compile and package the project (preferred).
      ```bash
      # Unix system
      ./mvnw clean install
      
      # Windows system
      mvnw.cmd clean install
      ``` 
      OR
   2. Use Maven installed on your system to compile and package the project. 
      ```bash
      mvn clean install
      ```
   This command will download necessary dependencies, compile the source code, and generate a `.jar` file in the `target/` directory.

---

## Assumptions
1. Structure of the JSON file remains the same across all the files.
2. As mentioned in problem statement about error files will be tested against the solution, assuming quite
   possible errors like, JSON file structure is not appropriate, field names have been misspelled or fields having empty/null values etc.
    1. In case of structure issues, JSON file will be skipped during the merging process.
    2. If there are issues like misspelled field names e.g. vendor, name, core & has_wifi, those records will be skipped during merging process (not the entire file).
3. Each vendor will have unique Board names, in case there are duplicates (same vendor & name, excludes core & has_wifi), record of the file having name appears earlier in ascending order will be picked in case duplicate records
present in diff. files. If duplicate records appear in the same file i.e. same vendor & name, record which appears first in the file will be picked; whereas diff. vendors having same names will be included in the list.

---

## How to run?

After building the project, you can run the tool in the following ways:

### **CLI arguments**

The application accepts command-line arguments, including the input folder path & output file path.

```
unix system absolute path looks like => /Users/ashutosh/projects/arm-merge-json-files/json-example-files/output-file/combined-json-file-small-data.json
windows system absolute path looks like => C:/Users/ashutosh/Desktop/arm-merge-json-files/json-example-files/output-file/combined-json-file-small-data.json
```

Example:
   ```bash
   java -jar target/arm-merge-json-files-cli-<version>.jar --source-path=/folder/path/to/json/files --output-path=/path/to/output.json
   ```
   OR
   ```bash
   # --output-path is optional. If not provided then uutput file will be generated inside source-path with prefix 'combined-json-file-' followed by random UUID.
   java -jar target/arm-merge-json-files-cli-<version>.jar --source-path=/folder/path/to/json/files
   ```

Replace `<version>` with latest built jar version e.g. `1.0.0`, `--source-path` with the folder path to your input JSON files and `--output-path` with the desired output file path for the combined output.

### **Run CLI application on example files provided inside 'json-example-files'**
```bash
   # Small set of records, generates combined json records file inside source directory
   java -jar target/arm-merge-json-files-cli-1.0.0.jar --source-path=json-example-files/input-files/small
   ```
```bash
   # Small set of records, generates combined json records file & saves to desired output file
   java -jar target/arm-merge-json-files-cli-1.0.0.jar --source-path=json-example-files/input-files/small --output-file-path=json-example-files/output-file/combined-json-file-small-data.json
   ```
```bash
   # Large set of records, generates combined json records file inside source directory
   java -jar target/arm-merge-json-files-cli-1.0.0.jar --source-path=json-example-files/input-files/large
   ```
```bash
   # Large set of records, generates combined json records file & saves to desired output file
   java -jar target/arm-merge-json-files-cli-1.0.0.jar --source-path=json-example-files/input-files/large --output-file-path=json-example-files/output-file/combined-json-file-large-data.json
   ```
```bash
   # Duplicate set of records, generates combined json records file inside source directory
   java -jar target/arm-merge-json-files-cli-1.0.0.jar --source-path=json-example-files/input-files/duplicates
   ```
```bash
   # Duplicate set of records, generates combined json records file & saves to desired output file
   java -jar target/arm-merge-json-files-cli-1.0.0.jar --source-path=json-example-files/input-files/duplicates --output-file-path=json-example-files/output-file/combined-json-file-skipped-duplicate-records.json
   ```
