# git ddit

A distributed and decentralised issue tracking system for Git.

## Dissertation Abstract

Git is a distributed version control system used primarily for source-code management. One of the 
main features of Git is that it is decentralised. Each repository is self-contained, and data can be 
synchronised between repositories by pushing and pulling, without the need for a central repository. 

GitHub is a popular Git repository hosting service that developers commonly use for the convenience 
of sharing changes to the project codebase over the internet. Services like GitHub often offer their 
own features such as issue tracking and continuous integration pipelines. The problem with using an 
issue tracking system on GitHub or a similar service is that it breaks the decentralised nature of Git.

This project aims to create an ITS that takes advantage of Git’s distributed and decentralised nature. 
Issues will exist within the self-contained Git repository giving every developer a full copy of the issue 
history. These issues will then be able to be pushed, pulled, merged, and synchronised along with all 
the other data stored in the repository.


## How to use
To use a command the basic format: `git ddit <command> [args]` is used. An example is `git ddit show –id myissue`  
For the full list of commands and how to use them read [here.](https://github.com/blackjackz151/git-ddit/tree/master/src/main/resources/help)


## How to install
### Obtaining JAR
To install the ITS project from source code the following prerequisites must be installed: 


*  JDK version 10+ 
*  Apache Maven version 3.5.2+ 

Once these are installed, navigate a shell’s working directory to the directory the source code is stored.  
Now run the following command: `mvn clean compile assembly:single`  
The jar will then be created inside the `target` directory. 
This JAR can then be placed in any directory of user’s choice. 

### JAR Setup

To execute the JAR the Java Runtime Environment version 10+ must be installed (this can be installed separately but is also packaged with the JDK).  

Other than the JRE there are no other prerequisites to use the JAR. However, it is **extremely** recommended to have the users name and email specified in the `.gitconfig` file located in the user’s home directory. Usually this will already be set up as the JAR is used from within Git repositories and to obtain a project’s repository, users will have already set up Git. 

Next create a shell script named `git-ddit` in a directory of user’s choice or download a copy of the script [here](https://github.com/blackjackz151/git-ddit/blob/master/git-ddit). 

Inside this script enter the following (changing the “path/to/jar” to the fully qualified file path of the JAR): 
```bash
#!/bin/bash
# Usage: git ddit 

if [ -d .git ]; then   
    java -jar /path/to/jar/ddit.jar "$@" 
else   
    git rev-parse --git-dir 2> /dev/null;  
    echo "Not in a git directory"; 
fi; 
```

Next this script must be given permissions to be an executable. This can be done with the command `chmod +x /path/to/script/git-ddit`. 

The last thing is to add the directory that contains this script to the users `$PATH` variable. This can be done by adding: `export PATH="directory/of/script:$PATH"`  to the `~/.bashrc` or `~/.profile` file.  

To check everything is set up correctly, navigate into a Git repository and type `git ddit`. The main menu help text of the system should be printed in the terminal. 
