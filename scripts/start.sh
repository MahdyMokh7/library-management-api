#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -euo pipefail

# Text formatting colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}==================================================${NC}"
echo -e "${BLUE}        Library API Local Automation Tool         ${NC}"
echo -e "${BLUE}==================================================${NC}"

show_help() {
    echo "Usage: ./start.sh [command]"
    echo ""
    echo "Commands:"
    echo "  run      Apply code formatting, run all verification tests, compile"
    echo "           the dynamic POM versioned image, and start the Docker stack."
    echo "  clean    Stop all containers and completely wipe database data off disk."
    echo "  help     Show this help menu."
}

if [ $# -eq 0 ]; then
    show_help
    exit 1
fi

case "$1" in
    run)
        echo -e "${YELLOW}➔ Step 1/4: Automatically applying code styling formatting...${NC}"
        mvn spotless:apply

        echo -e "${YELLOW}➔ Step 2/4: Running all Unit/Integration tests, Jacoco coverage tests, and PMD analysis...${NC}"
        mvn clean verify pmd:check

        echo -e "${YELLOW}➔ Step 3/4: Dynamically extracting version from pom.xml...${NC}"
        APP_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
        echo -e "${GREEN}✔ Detected project version: ${APP_VERSION}${NC}"

        echo -e "${YELLOW}➔ Step 4/4: Packaging Docker image and starting full stack...${NC}"
        docker build -t "library-api:${APP_VERSION}" .

        IMAGE_VERSION=${APP_VERSION} docker compose up -d

        echo -e "${GREEN}==================================================${NC}"
        echo -e "${GREEN}✔ Application stack is running flawlessly inside Docker!${NC}"
        echo -e "➔ Swagger UI Dashboard: ${BLUE}http://localhost:8080/swagger-ui/index.html${NC}"
        echo -e "➔ Watch application logs:  ${BLUE}docker compose logs -f app${NC}"
        echo -e "${GREEN}==================================================${NC}"
        ;;

    clean)
        echo -e "${RED}⚠️  Wiping all container environments and data volumes from disk...${NC}"
        docker compose down -v
        echo -e "${GREEN}✔ Local storage system state completely reset.${NC}"
        ;;

    help|*)
        show_help
        ;;
esac