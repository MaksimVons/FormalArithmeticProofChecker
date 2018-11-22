SOURCES = $(shell find src -type f -name "*.java")
CLASSES = $(patsubst src/%.java,out/%.class,$(SOURCES))
MAINCLASS = FAProofChecker

all: $(CLASSES)

run:
	java -cp out ${MAINCLASS}

clean:
	rm -rf out


out/%.class: src/%.java out
	javac -cp src $< -d out

out:
	mkdir -p out