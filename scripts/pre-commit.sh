#!/bin/bash

# Pre-commit hook for BarberShop project
# This script runs before each commit to ensure code quality

set -e

echo "Running pre-commit checks..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    print_error "pom.xml not found. Please run this script from the project root."
    exit 1
fi

# Check if Maven is available
if ! command -v ./mvnw &> /dev/null; then
    print_error "Maven wrapper not found. Please ensure mvnw is executable."
    exit 1
fi

# Make Maven wrapper executable
chmod +x ./mvnw

print_status "Running code quality checks..."

# Run Checkstyle
print_status "Running Checkstyle..."
if ./mvnw checkstyle:check; then
    print_status "Checkstyle passed"
else
    print_error "Checkstyle failed. Please fix the issues before committing."
    exit 1
fi

# Run SpotBugs
print_status "Running SpotBugs..."
if ./mvnw spotbugs:check; then
    print_status "SpotBugs passed"
else
    print_error "SpotBugs found issues. Please fix them before committing."
    exit 1
fi

# Run tests
print_status "Running tests..."
if ./mvnw test; then
    print_status "All tests passed"
else
    print_error "Tests failed. Please fix the failing tests before committing."
    exit 1
fi

# Check for TODO comments in production code
print_status "Checking for TODO comments..."
TODO_COUNT=$(find src/main/java -name "*.java" -exec grep -l "TODO\|FIXME" {} \; | wc -l)
if [ "$TODO_COUNT" -gt 0 ]; then
    print_warning "Found $TODO_COUNT files with TODO/FIXME comments:"
    find src/main/java -name "*.java" -exec grep -l "TODO\|FIXME" {} \;
    print_warning "Consider addressing these before committing."
fi

# Check for console.log or System.out.println in production code
print_status "Checking for debug statements..."
DEBUG_COUNT=$(find src/main/java -name "*.java" -exec grep -l "System\.out\.println\|console\.log" {} \; 2>/dev/null | wc -l)
if [ "$DEBUG_COUNT" -gt 0 ]; then
    print_warning "Found $DEBUG_COUNT files with debug statements:"
    find src/main/java -name "*.java" -exec grep -l "System\.out\.println\|console\.log" {} \; 2>/dev/null
    print_warning "Consider removing debug statements before committing."
fi

# Check file sizes
print_status "Checking file sizes..."
LARGE_FILES=$(find src -name "*.java" -size +100k)
if [ -n "$LARGE_FILES" ]; then
    print_warning "Found large files (>100KB):"
    echo "$LARGE_FILES"
    print_warning "Consider splitting large files."
fi

# Check for proper line endings
print_status "Checking line endings..."
if command -v dos2unix &> /dev/null; then
    # Check for Windows line endings
    WINDOWS_FILES=$(find src -name "*.java" -exec file {} \; | grep "CRLF" | wc -l)
    if [ "$WINDOWS_FILES" -gt 0 ]; then
        print_warning "Found $WINDOWS_FILES files with Windows line endings."
        print_warning "Consider converting to Unix line endings."
    fi
fi

# Check for trailing whitespace
print_status "Checking for trailing whitespace..."
TRAILING_WHITESPACE=$(find src -name "*.java" -exec grep -l "[[:space:]]$" {} \; | wc -l)
if [ "$TRAILING_WHITESPACE" -gt 0 ]; then
    print_warning "Found $TRAILING_WHITESPACE files with trailing whitespace."
    print_warning "Consider removing trailing whitespace."
fi

# Check commit message format (if available)
if [ -n "$1" ]; then
    COMMIT_MSG_FILE="$1"
    if [ -f "$COMMIT_MSG_FILE" ]; then
        print_status "Checking commit message format..."
        COMMIT_MSG=$(cat "$COMMIT_MSG_FILE")
        
        # Check for conventional commit format
        if ! echo "$COMMIT_MSG" | grep -qE "^(feat|fix|docs|style|refactor|test|chore)(\(.+\))?: .+"; then
            print_warning "Commit message doesn't follow conventional commit format."
            print_warning "Consider using: type(scope): description"
            print_warning "Types: feat, fix, docs, style, refactor, test, chore"
        fi
        
        # Check message length
        MSG_LENGTH=${#COMMIT_MSG}
        if [ "$MSG_LENGTH" -lt 10 ]; then
            print_warning "Commit message is very short. Consider adding more detail."
        fi
    fi
fi

print_status "Pre-commit checks completed successfully!"
print_status "Ready to commit."

exit 0
