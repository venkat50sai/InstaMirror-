# Deployment Quick Reference Checklist

**Project:** E-Commerce Microservices Platform  
**Date:** ________________  
**Deployed By:** ________________  
**Environment:** [ ] Development [ ] Staging [ ] Production

---

## 🔍 Pre-Deployment Verification (1-2 Hours)

### Code & Repository
- [ ] All code committed to Git (`git status` shows clean)
- [ ] Version tag created (`git tag v1.0.0`)
- [ ] No hardcoded credentials in code
- [ ] README.md updated with deployment info
- [ ] DEPLOYMENT_* documents reviewed and current

### Backend Services Verification
```bash
# Verify Java
java -version        # Should show Java 21.x.x
$JAVA_HOME/bin/javac -version

# Verify Maven
mvn -version        # Should show Maven 3.8.1+

# Build & test
cd backend/Utility && mvn clean install
cd ../Eureka && mvn clean package
cd ../Gate && mvn clean package
# ... verify all services build successfully

# Check JAR files exist
ls backend/*/target/*.jar
```
- [ ] Utility library builds & installs
- [ ] Eureka builds successfully
- [ ] Gate builds successfully
- [ ] User service builds successfully
- [ ] Post service builds successfully
- [ ] Comment service builds successfully
- [ ] Like service builds successfully
- [ ] Friend service builds successfully
- [ ] Product service builds successfully
- [ ] Order service builds successfully
- [ ] Cart service builds successfully
- [ ] All unit tests pass
- [ ] All JAR files in target/ directories

### Frontend Verification
```bash
cd frontend/myapp
npm install          # All dependencies install
npm test            # Tests pass
npm run build       # Production build succeeds
```
- [ ] Node.js 18+ installed (`node -v`)
- [ ] NPM 9+ installed (`npm -v`)
- [ ] Dependencies install without errors
- [ ] Unit tests pass
- [ ] No TypeScript compilation errors
- [ ] Production build completes
- [ ] dist/myapp/browser/ folder created
- [ ] index.html exists in dist/
- [ ] No console warnings/errors

### Database Verification
```bash
# Test MySQL connection
mysql -h localhost -u app_user -p -e "SHOW DATABASES;"

# Verify databases exist
SHOW DATABASES;
```
- [ ] MySQL 8.0+ installed and running
- [ ] user_db created
- [ ] post_db created
- [ ] comment_db created
- [ ] like_db created
- [ ] friend_db created
- [ ] product_db created
- [ ] order_db created
- [ ] cart_db created
- [ ] app_user user created
- [ ] app_user has correct permissions
- [ ] Database charset is UTF-8MB4
- [ ] Backup created and verified

### Infrastructure Verification
- [ ] Cloud platform account active
- [ ] Compute resources available (CPU, RAM, Storage)
- [ ] Network configured (VPC/VNet, subnets)
- [ ] Security groups configured (firewall rules)
- [ ] SSH keys generated and stored securely
- [ ] Load balancer provisioned (if needed)
- [ ] DNS records ready to update
- [ ] SSL/TLS certificates obtained
- [ ] Certificates installed/ready for upload
- [ ] Monitoring service available
- [ ] Logging service available
- [ ] Secrets manager configured
- [ ] Environment variables prepared

### Documentation Verification
- [ ] Deployment guide written
- [ ] Architecture diagram created
- [ ] Rollback procedure documented
- [ ] Troubleshooting guide prepared
- [ ] API documentation generated
- [ ] Database schema documented
- [ ] Team trained on deployment process
- [ ] On-call runbook prepared

---

## 🚀 Deployment Steps

### Phase 1: Eureka Service Registry (5 mins)
```bash
# Server 1 - Eureka
java -Deureka.server.enable-self-preservation=false \
  -Dserver.port=8761 \
  -jar backend/Eureka/target/eureka-0.0.1-SNAPSHOT.jar &

# Verify
curl http://localhost:8761/actuator/health
# Response: {"status":"UP"}

curl http://localhost:8761/eureka/apps
# Response: XML with apps
```
- [ ] Eureka process started
- [ ] Port 8761 listening
- [ ] Health endpoint responds 200 OK
- [ ] No errors in logs

### Phase 2: API Gateway (5 mins)
```bash
# Server 2 - Gate
java -Dserver.port=8888 \
  -Deureka.client.service-url.defaultzone=http://eureka-host:8761/eureka \
  -jar backend/Gate/target/gate-0.0.1-SNAPSHOT.jar &

# Verify
curl http://localhost:8888/actuator/health
# Response: {"status":"UP"}
```
- [ ] Gate process started
- [ ] Port 8888 listening
- [ ] Health endpoint responds 200 OK
- [ ] No errors in logs

### Phase 3: Microservices (Each: 5 mins)

**User Service (Port 8081):**
```bash
java -Dserver.port=8081 \
  -Dspring.datasource.url=jdbc:mysql://mysql-host:3306/user_db \
  -Dspring.datasource.username=app_user \
  -Dspring.datasource.password=${DB_PASSWORD} \
  -Deureka.client.service-url.defaultzone=http://eureka-host:8761/eureka \
  -jar backend/User/target/user-0.0.1-SNAPSHOT.jar &
```
- [ ] User service started (Port 8081)
- [ ] Registered with Eureka
- [ ] Database connected
- [ ] Health endpoint OK

**Post Service (Port 8082):**
```bash
java -Dserver.port=8082 \
  -Dspring.datasource.url=jdbc:mysql://mysql-host:3306/post_db \
  -Dspring.datasource.username=app_user \
  -Dspring.datasource.password=${DB_PASSWORD} \
  -Deureka.client.service-url.defaultzone=http://eureka-host:8761/eureka \
  -jar backend/Post/target/post-0.0.1-SNAPSHOT.jar &
```
- [ ] Post service started (Port 8082)
- [ ] Registered with Eureka
- [ ] Database connected
- [ ] Health endpoint OK

**Repeat for:**
- [ ] Comment service (Port 8083)
- [ ] Like service (Port 8084)
- [ ] Friend service (Port 8085)
- [ ] Product service (Port 8086)
- [ ] Order service (Port 8087)
- [ ] Cart service (Port 8088)

### Phase 4: Frontend (10 mins)
```bash
# Build frontend
cd frontend/myapp
npm install
npm run build

# Copy dist to web server
cp -r dist/myapp/browser /var/www/html/myapp

# Verify
curl http://localhost/index.html
```
- [ ] Frontend built successfully
- [ ] Files copied to web server
- [ ] Web server started
- [ ] index.html loads

### Phase 5: Reverse Proxy/Load Balancer (5 mins)
```bash
# Configure Nginx (if using)
sudo cp nginx.conf /etc/nginx/sites-available/default
sudo nginx -t     # Test configuration
sudo systemctl restart nginx
```
- [ ] Nginx configuration valid
- [ ] Nginx service running
- [ ] HTTPS working
- [ ] SSL certificate valid

---

## ✅ Post-Deployment Verification (15-30 mins)

### Service Health Checks
```bash
# Eureka
curl http://eureka:8761/actuator/health
# Expected: {"status":"UP"}

# Gateway
curl http://gateway:8888/actuator/health
# Expected: {"status":"UP"}

# Each microservice
curl http://user:8081/actuator/health
curl http://post:8082/actuator/health
curl http://comment:8083/actuator/health
curl http://like:8084/actuator/health
curl http://friend:8085/actuator/health
curl http://product:8086/actuator/health
curl http://order:8087/actuator/health
curl http://cart:8088/actuator/health
# Expected: All return {"status":"UP"}
```
- [ ] Eureka health: UP
- [ ] Gate health: UP
- [ ] User service health: UP
- [ ] Post service health: UP
- [ ] Comment service health: UP
- [ ] Like service health: UP
- [ ] Friend service health: UP
- [ ] Product service health: UP
- [ ] Order service health: UP
- [ ] Cart service health: UP

### Service Discovery Verification
```bash
# Check all services registered
curl http://eureka:8761/eureka/apps
```
- [ ] All 8 microservices registered
- [ ] Gate registered
- [ ] Each service shows healthy status

### API Gateway Routing Verification
```bash
# Test routing to each service
curl http://gateway:8888/user/health
curl http://gateway:8888/post/health
curl http://gateway:8888/comment/health
curl http://gateway:8888/like/health
curl http://gateway:8888/friend/health
curl http://gateway:8888/product/health
curl http://gateway:8888/order/health
curl http://gateway:8888/cart/health
```
- [ ] /user/** routes correctly
- [ ] /post/** routes correctly
- [ ] /comment/** routes correctly
- [ ] /like/** routes correctly
- [ ] /friend/** routes correctly
- [ ] /product/** routes correctly
- [ ] /order/** routes correctly
- [ ] /cart/** routes correctly

### Frontend Verification
```bash
# Check frontend loads
curl https://yourdomain.com
# Should return HTML with status 200

# Check assets load
curl https://yourdomain.com/assets/logo.png
# Should return image with status 200

# Check env.js loads
curl https://yourdomain.com/env.js
# Should return JavaScript with gateway URL
```
- [ ] Frontend accessible at domain
- [ ] index.html loads (HTTP 200)
- [ ] Static assets loading
- [ ] CSS/JS files loading
- [ ] Images loading
- [ ] env.js loads with correct API URL
- [ ] No console errors in browser

### Database Connectivity
```bash
# Test from each service
mysql -h mysql-host -u app_user -p user_db -e "SELECT 1;"
mysql -h mysql-host -u app_user -p post_db -e "SELECT 1;"
# ... test all databases
```
- [ ] User database accessible
- [ ] Post database accessible
- [ ] Comment database accessible
- [ ] Like database accessible
- [ ] Friend database accessible
- [ ] Product database accessible
- [ ] Order database accessible
- [ ] Cart database accessible

### API Endpoint Testing
```bash
# Test sample API calls
curl http://gateway:8888/user/all
curl http://gateway:8888/post/all
curl http://gateway:8888/product/all
# Should return JSON responses
```
- [ ] User endpoints working
- [ ] Post endpoints working
- [ ] Comment endpoints working
- [ ] Like endpoints working
- [ ] Friend endpoints working
- [ ] Product endpoints working
- [ ] Order endpoints working
- [ ] Cart endpoints working

### Performance Checks
```bash
# Test response times
time curl http://gateway:8888/user/all
# Should complete in < 500ms
```
- [ ] Response times acceptable (< 500ms)
- [ ] No high latency observed
- [ ] Database queries fast
- [ ] CPU usage normal
- [ ] Memory usage normal
- [ ] Disk I/O normal

### Security Verification
- [ ] HTTPS enabled (check lock icon in browser)
- [ ] SSL certificate valid (check cert details)
- [ ] CORS headers present in API responses
- [ ] JWT authentication working
- [ ] No sensitive data in logs
- [ ] Firewall rules in place
- [ ] Database accessible only from app servers

### Monitoring & Logging
- [ ] Monitoring dashboard accessible
- [ ] All services showing in monitoring
- [ ] Metrics being collected
- [ ] Logs being aggregated
- [ ] Alerts configured and active
- [ ] Health checks running

---

## 🔧 Common Issues & Quick Fixes

### Service Won't Start
```bash
# Check port availability
lsof -i :8081

# Check Java version
java -version

# Check logs
tail -f logs/user.log

# Check environment variables
echo $JAVA_HOME
echo $EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE
```

### Database Connection Fails
```bash
# Test MySQL connectivity
mysql -h mysql-host -u app_user -p -e "SELECT 1;"

# Check credentials
grep spring.datasource application.properties

# Check MySQL is running
sudo systemctl status mysql
```

### Service Not Registering with Eureka
```bash
# Check Eureka URL
curl http://eureka:8761/actuator/health

# Check service properties
grep eureka.client application.properties

# Check service is running on correct port
lsof -i :8081
```

### API Calls Failing
```bash
# Test gateway
curl http://gateway:8888/actuator/health

# Check gateway routes
curl http://gateway:8888/actuator/gateway/routes

# Test direct service
curl http://user:8081/health
```

### Frontend Not Loading
```bash
# Check web server running
sudo systemctl status nginx

# Check dist folder
ls dist/myapp/browser/

# Check env.js
cat dist/myapp/browser/env.js

# Check logs
sudo tail -f /var/log/nginx/error.log
```

---

## 📊 Monitoring During Deployment

### Key Metrics to Watch
```
CPU Usage:         < 70%
Memory Usage:      < 75%
Disk Usage:        < 80%
Response Time P95: < 500ms
Error Rate:        < 1%
Database Connections: < 15/20
```

### Log Lines to Monitor
```bash
# Watch for errors
tail -f logs/app.log | grep -i error

# Watch startup
tail -f logs/eureka.log
tail -f logs/gate.log
tail -f logs/user.log

# Monitor traffic
tail -f /var/log/nginx/access.log
```

### Health Check Frequency
- Every 5 minutes during first hour
- Every 15 minutes for next 4 hours
- Every 30 minutes for rest of day
- Daily for 1 week post-deployment

---

## 📋 Documentation References

Need more information? Check these documents:
- **[DEPLOYMENT_REQUIREMENTS.md](./DEPLOYMENT_REQUIREMENTS.md)** - Overview
- **[BACKEND_DEPLOYMENT_REQUIREMENTS.md](./BACKEND_DEPLOYMENT_REQUIREMENTS.md)** - Backend details
- **[FRONTEND_DEPLOYMENT_REQUIREMENTS.md](./FRONTEND_DEPLOYMENT_REQUIREMENTS.md)** - Frontend details
- **[DEPLOYMENT_REQUIREMENTS_SUMMARY.md](./DEPLOYMENT_REQUIREMENTS_SUMMARY.md)** - Index & summary

---

## ✋ STOP Before Going Live

**Final Checklist (Must be 100% complete):**
- [ ] All services health checks passing
- [ ] All routes verified working
- [ ] Frontend loads without errors
- [ ] API calls return correct data
- [ ] HTTPS working and valid
- [ ] Database backups confirmed
- [ ] Monitoring dashboards active
- [ ] Logging aggregation working
- [ ] Team trained and ready
- [ ] Rollback procedure tested
- [ ] Incident response plan ready
- [ ] Stakeholders notified

**If ANY of the above is not complete, DO NOT proceed to production.**

---

## 📞 Emergency Contacts

**DevOps Lead:** _________________ Phone: _________________  
**Backend Lead:** _________________ Phone: _________________  
**Frontend Lead:** _________________ Phone: _________________  
**Database Admin:** _________________ Phone: _________________  
**Cloud Provider Support:** _________________ Phone: _________________

---

## 📝 Deployment Notes

**What was deployed:** _____________________________________________
**Deployed by:** ______________ **Date/Time:** __________________
**Duration:** ______________ **Issues:** __________________________
**Status:** [ ] Success [ ] Success with Minor Issues [ ] Failure  
**Rollback needed:** [ ] Yes [ ] No

**Post-Deployment Notes:**
_________________________________________________________________
_________________________________________________________________
_________________________________________________________________

---

## 🎉 Deployment Complete!

**Date Completed:** _______________  
**Verified by:** _______________  
**Approval:** _______________  

**Next Steps:**
1. [ ] Notify team of completion
2. [ ] Update status page
3. [ ] Send deployment summary
4. [ ] Schedule post-mortem (if issues)
5. [ ] Document lessons learned
6. [ ] Archive logs

---

**Print this checklist and use it during deployment. Check off each item as you complete it. This ensures nothing is missed!**

**Version:** 1.0 | **Last Updated:** May 2026
