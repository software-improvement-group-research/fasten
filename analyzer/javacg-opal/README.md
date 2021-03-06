<p align="center">
    <img src="https://user-images.githubusercontent.com/45048351/89277641-c5592100-d64d-11ea-963e-3d10e8efd7b3.jpg">
</p>
<br/>
<p align="center">
    <a href="https://github.com/fasten-project/fasten/actions" alt="GitHub Workflow Status">
        <img src="https://img.shields.io/github/workflow/status/fasten-project/fasten/Java%20CI?logo=GitHub%20Actions&logoColor=white&style=for-the-badge" /></a>
    <!-- Here should be a link to Maven repo and version should be pulled from there. -->
    <a href="https://github.com/fasten-project/fasten/" alt="GitHub Workflow Status">
                <img src="https://img.shields.io/maven-central/v/fasten/opal?label=version&logo=Apache%20Maven&style=for-the-badge" /></a>
</p>
<br/>

The FASTEN OPAL is a tool for generating call graphs in FASTEN format using [OPAL](https://www.opal-project.de/) call graph generator version '3.0.0'. This tool can also merge the resulted call graphs with their dependencies. The OPAL can be used as a standalone tool for generating call graphs for libraries and application and as a part of the FASTEN server.

## Arguments
- `-a` `--artifact` Artifact, maven coordinate, or a file path
- `-d` `--dependencies` Dependencies: coordinates or files
- `-g` `--generate` Generate call graph for the artifact
    - `-ga` `--genAlgorithm` Algorithm for generating a call graph {RTA,CHA,AllocationSiteBasedPointsTo,TypeBasedPointsTo}
- `-h` `--help` Show this help message and exit.
- `-m` `--mode` Input of algorithms are {FILE or COORD}
    - `-ma` `--mergeAlgorithm` Algorithm for merging call graphs {RA, CHA}
- `-n` `--main` Main class of artifact (Used for analyzing applications. Omit for libraries)
- `-o` `--output` Output directory path
- `-r` A list of Maven repositories to look for the artifact in
- `-s` `--stitch` Stitch artifact CG to dependencies
- `-t` `--timestamp` Release timestamp
- `-V` `--version` Print version information and exit.

## Usage 

#### Generate a call graph for an artifact
```shell script
-a groupId:artifactId:version -g -ga CHA -m COORD -r https://repo.maven.apache.org/maven2/ -o /some/path/to/result/file.json
```

#### Generate a call graph for a file
Can be used for all core Maven archived projects (jar, war, zip) as well as class files.
```shell script
-a /path/to/file.jar -g -ga CHA -m FILE -o /some/path/to/result/file.json
```

#### Merge a call graph for an artifact with its dependencies call graphs
```shell script
-a abbot:costello:1.4.0 -d abbot:abbot:1.4.0 -m COORD -ma CHA -o /some/path/to/result/file.json
```

## Join the community

The FASTEN software package management efficiency relies on an open community contributing to open technologies. Related research projects, R&D engineers, early users and open source contributors are welcome to join the [FASTEN community](https://www.fasten-project.eu/view/Main/Community), to try the tools, to participate in physical and remote worshops and to share our efforts using the project [community page](https://www.fasten-project.eu/view/Main/Community) and the social media buttons below.  
<p>
    <a href="http://www.twitter.com/FastenProject" alt="Fasten Twitter">
        <img src="https://img.shields.io/badge/%20-Twitter-%231DA1F2?logo=Twitter&style=for-the-badge&logoColor=white" /></a>
    <a href="http://www.slideshare.net/FastenProject" alt="GitHub Workflow Status">
                <img src="https://img.shields.io/badge/%20-SlideShare-%230077B5?logo=slideshare&style=for-the-badge&logoColor=white" /></a>
    <a href="http://www.linkedin.com/groups?gid=12172959" alt="Gitter">
            <img src="https://img.shields.io/badge/%20-LinkedIn-%232867B2?logo=linkedin&style=for-the-badge&logoColor=white" /></a>
</p>
