# Test script for MAI Microservices

Write-Host "Testing MAI Microservices API" -ForegroundColor Cyan

# Wait for services to start up
Write-Host "Waiting for services to start up..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Test Discovery Server
Write-Host "`nTesting Discovery Server..." -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8761/actuator/health" -Method Get
    Write-Host "Discovery Server Status: $($response.status)" -ForegroundColor Green
} catch {
    Write-Host "Error connecting to Discovery Server: $_" -ForegroundColor Red
}

# Test Auth Service Login
Write-Host "`nTesting Auth Service Login..." -ForegroundColor Green
try {
    $body = @{
        email = "test@example.com"
        password = "password123"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method Post -Body $body -ContentType "application/json"
    Write-Host "Auth Service Login Response: Token received" -ForegroundColor Green
    $token = $response.token
    Write-Host "JWT Token: $token" -ForegroundColor Gray
} catch {
    Write-Host "Error connecting to Auth Service: $_" -ForegroundColor Red
}

# Test User Service with the token
Write-Host "`nTesting User Service Health Check..." -ForegroundColor Green
try {
    $headers = @{
        Authorization = "Bearer $token"
    }
    
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/users/health" -Method Get -Headers $headers
    Write-Host "User Service Health Check: $($response.status)" -ForegroundColor Green
} catch {
    Write-Host "Error connecting to User Service: $_" -ForegroundColor Red
}

Write-Host "`nTest completed!" -ForegroundColor Cyan
