unset DISPLAY
ant clean build clean
java -jar -Djava.awt.headless=true build/PrEPSTI-0.0.1.jar
