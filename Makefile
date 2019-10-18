include .env
export

.PHONY: clean build test

NAME=`basename $$PWD`

clean:
	@echo "[$(NAME)] Cleaning"
	@mvn -q clean

build:
	@echo "[$(NAME)] Building"
	@mvn -q -DskipTests=true clean package

test: build
	@echo "[$(NAME)] Testing"
	@java -jar target/filldb.jar -c $(URL) -r -d -s -i --allow-remote --allow-humor --allow-nsfw

help: build
	@echo "[$(NAME)] Testing"
	@java -jar target/filldb.jar -h
