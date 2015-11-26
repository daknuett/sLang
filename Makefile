comp=javac
CP= ./sLangCompiler/src/
SRC_DIR=./sLangCompiler/src/slang/

all: parser

# empty...
slang:
	$(comp) -cp $(CP) $(SRC_DIR)*.java
parser:
	$(comp) -cp $(CP) $(SRC_DIR)parser/*.java
parser.statements:
	$(comp) -cp $(CP) $(SRC_DIR)parser/statements/*.java
parser.literals:
	$(com) -cp $(CP)  $(SRC_DIR)parser/literals/*.java

