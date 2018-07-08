# Jenjinn2
Version 2 of my chess engine written in Java and built on top of my JFlow library. I wrote it with the aim of demonstrating how adopting a more functional style of coding leads to higher code quality overall. Particularly in terms of reduced number of bugs, more concise yet more readable code and maybe most importantly a more enjoyable experience for anyone who interacts with the code, either reading it or writing it. To this end I wanted to write a reasonably powerful but easy to understand engine utilising JFlow which I think I have achieved. I've provided a simple JavaFX wrapper application for playing against Jenjinn, to get it working you will need to clone this repository and build the executable jar from source (I apologise for the size of the repository ~150Mb. The vast majority is made up of resource files for testing, the jar will end up at about 2Mb which is mainly made up of resources for chess openings).

Work has ceased for now on this project. I will not be making further changes (maybe except a lil refactoring) to the core engine code for a long while, I do however plan to write a web interface for playing Jenjinn at some point.


To play do the following (if windows swap terminal for command prompt):

1. Run `java -version` in the terminal to check the Java 8 runtime environment is setup.
2. Clone this repository to your local machine and set it as the pwd in the terminal.
3. Run the command `./gradlew clean build` (or `gradlew clean build` on Windows).
5. Run `java -jar build/libs/Jenjinn2.jar` 
