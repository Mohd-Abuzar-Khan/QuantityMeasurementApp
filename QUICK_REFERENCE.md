# Quick Deployment Reference

## 🚀 One-Command Build & Deploy

### Build All Services
```bash
# From project root directory
cd quantitymeasurement

# Build all backend services
cd api-gateway && mvn clean install -DskipTests && cd ..
cd authentication-sevice && mvn clean install -DskipTests && cd ..
cd quantity-service && mvn clean install -DskipTests && cd ..

# Build frontend
cd QuantityMeasurement-Frontend
npm install
npm run build:prod
cd ..
```

## 📦 Generated Artifacts

| Service | JAR Location | Size |
|---------|-------------|------|
| API Gateway | `api-gateway/target/api-gateway-3.5.7.jar` | ~50MB |
| Auth Service | `authentication-sevice/target/auth-service-0.0.1-SNAPSHOT.jar` | ~45MB |
| Quantity Service | `quantity-service/target/quantity-0.0.1-SNAPSHOT.jar` | ~48MB |
| Frontend | `QuantityMeasurement-Frontend/dist/qma-frontend/` | - |

## 🌐 Live Service URLs

```
┌─────────────────────────────────────────────────┐
│  LIVE PRODUCTION ENDPOINTS                      │
├─────────────────────────────────────────────────┤
│ Frontend        → https://qma-frontend.app      │
│ API Gateway     → https://api-gateway.onrender  │
│ Auth Service    → https://auth-server.onrender  │
│ Quantity Svc    → https://quantity-app.onrender │
│ Eureka          → https://eureka-server.onrender│
│ Database        → PostgreSQL on Render          │
└─────────────────────────────────────────────────┘
```

## 🔗 Service Discovery Flow

```
Frontend
    ↓
https://api-gateway-g587.onrender.com
    ↓
[CORS Check] ✅ Allowed
    ↓
Routes via Eureka Load Balancer
    ↓
    ├─→ /api/v1/auth/**        → auth-service:8082
    ├─→ /api/v1/quantities/**  → quantity-service:8081
    └─→ /oauth2/**             → auth-service:8082
    ↓
PostgreSQL Database (qma_auth)
```

## 🔐 Database Configuration

**PostgreSQL (Shared by both services)**
```
Host:     dpg-d795ahffte5s739fton0-a.singapore-postgres.render.com
Port:     5432
Database: qma_auth
User:     qma_auth_user
Pass:     [See environment variables]
```

## 🧪 Test Endpoints

### Health Checks
```bash
# API Gateway Health
curl https://api-gateway-g587.onrender.com/actuator/health

# Auth Service Health
curl https://auth-server-sks7.onrender.com/actuator/health

# Quantity Service Health
curl https://quantitymeasurementapp-jivh.onrender.com/actuator/health
```

### API Endpoints
```bash
# List quantities
curl https://api-gateway-g587.onrender.com/api/v1/quantities/all \
  -H "Authorization: Bearer <jwt_token>"

# Create quantity
curl -X POST https://api-gateway-g587.onrender.com/api/v1/quantities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt_token>" \
  -d '{"value": 100, "unit": "METER"}'
```

### OAuth2 Endpoints
```bash
# Google OAuth2
https://api-gateway-g587.onrender.com/oauth2/authorization/google

# GitHub OAuth2
https://api-gateway-g587.onrender.com/oauth2/authorization/github
```

## 📊 Configuration Summary

| Component | Configuration |
|-----------|---------------|
| **Frontend Build** | Production with Angular optimization |
| **API Routes** | 5 routes configured with load balancing |
| **CORS** | Enabled for production domains |
| **Database** | PostgreSQL with connection pooling |
| **Service Discovery** | Eureka (all services registered) |
| **Authentication** | JWT + OAuth2 (Google, GitHub) |
| **SSL/TLS** | HTTPS enabled on all endpoints |

## 🛠️ Debugging Commands

### Check Service Status
```bash
# Eureka Dashboard
https://eureka-server-lsch.onrender.com/

# Verify service registration
curl https://eureka-server-lsch.onrender.com/eureka/apps
```

### View Logs
```bash
# View on Render dashboard
# Services → Select Service → Logs tab
```

### Test Database Connection
```bash
# Connection string for pgAdmin or CLI
postgresql://qma_auth_user:PASSWORD@dpg-d795ahffte5s739fton0-a.singapore-postgres.render.com/qma_auth
```

## 📝 Important Notes

1. **Shared Database**: Both auth-service and quantity-service use the same PostgreSQL database
2. **CORS Enabled**: Frontend can make cross-origin requests to API Gateway
3. **Load Balancing**: Eureka handles service discovery and load balancing
4. **Auto-scaling**: Services can be scaled independently through Render dashboard
5. **Environment Variables**: All sensitive data stored in Render environment config

## 🔄 Deployment Workflow

### 1. Local Testing
```bash
npm run start  # Frontend development
mvn spring-boot:run  # Individual service testing
```

### 2. Build Artifacts
```bash
npm run build:prod  # Frontend production build
mvn clean install -DskipTests  # Backend services
```

### 3. Deploy to Render
```
git push origin main
# Render auto-deploys on git push
# Monitor deployment in Render dashboard
```

### 4. Verify Deployment
```bash
# Check health endpoints
# Verify Eureka registration
# Test API from frontend
# Monitor logs for errors
```

## 🎯 Key Metrics to Monitor

- API response times
- Database query performance
- Service uptime
- Error rates
- Request throughput
- Resource utilization (CPU, Memory)

## 📞 Support Resources

| Issue | Resolution |
|-------|-----------|
| Service not starting | Check Render logs for errors |
| Database not connected | Verify PostgreSQL is running on Render |
| CORS errors | Add domain to API Gateway allowed-origins |
| OAuth2 fails | Verify credentials and redirect URIs |
| Service discovery fails | Check Eureka dashboard registration |

## 🚨 Emergency Rollback

```bash
# Revert to previous commit
git revert <commit_hash>
git push origin main

# Services will auto-redeploy with previous version
```

## 📅 Last Updated
April 5, 2026

## ✅ Configuration Status: COMPLETE

All services configured for smooth production deployment!

