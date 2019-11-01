
.PHONY: clean build check-versions release-notes deploy test help

DATE=`date +'%F'`
NAME=`xmllint --xpath "//project/artifactId/text()" pom.xml`
VERSION=`xmllint --xpath "//project/version/text()" pom.xml`
URL=jdbc:mariadb://localhost:3306/filldb
PREVIOUS_TAG=`git tag | sort -r | head -n 1`

clean:
	@echo "[$(NAME)] Cleaning"
	@mvn -q clean

build:
	@echo "[$(NAME)] Building"
	@mvn -q -DskipTests=true clean package

check-versions:
	@mvn versions:display-dependency-updates
	@mvn versions:display-plugin-updates

release-notes:
	@echo "[$(NAME)] Writing release notes to src/docs/releases/release-$(VERSION).txt"
	@echo "$(VERSION)" > src/docs/releases/release-$(VERSION).txt
	@echo "" >> src/docs/releases/release-$(VERSION).txt
	@git log --pretty="%s" $(PREVIOUS_TAG)... master >> src/docs/releases/release-$(VERSION).txt

install:
	@echo "[$(NAME)] Building"
	@mvn -q -DskipTests=true clean package org.apache.maven.plugins:maven-shade-plugin:3.2.1:shade@shade
	@echo "[$(NAME)] Run with 'java -jar target/filldb.jar' or "

deploy: build
	@echo "[$(NAME)] Tagging and pushing to github"
	@git tag $(NAME)-$(VERSION)
	@git push && git push --tags
	@echo "[$(NAME)] Creating github release"
	@hub release create -d -a target/$(NAME)-$(VERSION).jar -a target/$(NAME)-$(VERSION)-javadoc.jar -a target/$(NAME)-$(VERSION)-sources.jar -F src/docs/releases/release-$(VERSION).txt $(NAME)-$(VERSION)
	@echo "[$(NAME)] Uploading to maven central"
	@mvn clean deploy -P release

test: install
	@echo "[$(NAME)] Testing"
	@java -jar target/filldb.jar -c $(URL) -r -d -s -i --allow-remote --allow-humor --allow-nsfw

help: install
	@echo "[$(NAME)] Testing"
	@java -jar target/filldb.jar -h
