# enigma

Before running the program, compile all the files with  
`javac -g -Xlint:unchecked enigma/*.java`  
or, if you have it, just  
`make`  

After compilation, use  
`java -ea enigma.Main [configuration file] [input file] [output file]`  
to run the program.

There is an included `germany-navy.conf` configuration file and two input files that will encrypt to each other.  
The spec for config files, input files, as well as a general description of the Enigma machine mechanisms is [here](https://inst.eecs.berkeley.edu/~cs61b/sp20/materials/proj/proj1/index.html)
