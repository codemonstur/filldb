
.PHONY: help clean build check-versions release-notes deploy install test

DATE=`date +'%F'`
NAME=`xmllint --xpath "//project/artifactId/text()" pom.xml`
VERSION=`xmllint --xpath "//project/version/text()" pom.xml`
URL=jdbc:mariadb://localhost:3306/filldb
PREVIOUS_TAG=`git tag | sort -r | head -n 1`

help:
	@echo "Available targets for $(NAME):"
	@echo "\thelp\t\t\tThis help"
	@echo "\tclean\t\t\tDelete everything in ./target"
	@echo "\tbuild\t\t\tCleans the project and rebuilds the code"
	@echo "\tcheck-versions\t\tCheck if the versions of dependencies are up to date"
	@echo "\trelease-notes\t\tCreate release notes for the latest version"
	@echo "\tdeploy\t\t\tClean, build and deploy a version to Github"
	@echo "\tinstall\t\t\tClean, build and install the CLI to a local dir"

clean:
	@echo "[$(NAME)] Cleaning"
	@mvn clean

build:
	@echo "[$(NAME)] Building"
	@mvn -DskipTests=true clean package

check-versions:
	@mvn versions:display-dependency-updates
	@mvn versions:display-plugin-updates

release-notes:
	@echo "[$(NAME)] Writing release notes to src/docs/releases/release-$(VERSION).txt"
	@echo "$(VERSION)" > src/docs/releases/release-$(VERSION).txt
	@echo "" >> src/docs/releases/release-$(VERSION).txt
	@git log --pretty="%s" $(PREVIOUS_TAG)... master >> src/docs/releases/release-$(VERSION).txt

deploy: build
	@echo "[$(NAME)] Tagging and pushing to github"
	@git tag $(NAME)-$(VERSION)
	@git push && git push --tags
	@echo "[$(NAME)] Creating github release"
	@hub release create -a target/$(NAME)-$(VERSION).jar -a target/$(NAME)-$(VERSION)-javadoc.jar -a target/$(NAME)-$(VERSION)-sources.jar -F src/docs/releases/release-$(VERSION).txt $(NAME)-$(VERSION)
	@echo "[$(NAME)] Uploading to maven central"
	@mvn clean deploy -P release

install:
	@echo "[$(NAME)] Building"
	@mvn -q -DskipTests=true clean package org.apache.maven.plugins:maven-shade-plugin:3.2.1:shade@shade
	@echo "[$(NAME)] Run with 'java -jar target/$(NAME).jar' or create an alias"

test: install
	@echo "[$(NAME)] Testing"
	@java -jar target/$(NAME).jar -c $(URL) -r -d -s -i --allow-remote --allow-humor --allow-nsfw
