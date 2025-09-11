@echo off
echo Starting PetCare Backend with Redis...
echo.

echo [1/3] Checking Redis connection...
redis-cli ping >nul 2>&1
if %errorlevel% neq 0 (
    echo Redis is not running. Please start Redis server first.
    echo.
    echo To start Redis:
    echo 1. Download Redis from: https://github.com/microsoftarchive/redis/releases
    echo 2. Or use Docker: docker run -d -p 6379:6379 --name redis redis:latest
    echo 3. Run: redis-server.exe
    echo.
    pause
    exit /b 1
)
echo Redis is running! ✓
echo.

echo [2/3] Cleaning and compiling project...
call mvnw.cmd clean compile -q
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful! ✓
echo.

echo [3/3] Starting Spring Boot application...
echo Server will be available at: http://localhost:8080
echo.
echo Available endpoints:
echo - GET  /api/test/health
echo - POST /api/auth/register
echo - POST /api/auth/verify-otp
echo - POST /api/auth/login
echo.
call mvnw.cmd spring-boot:run
