# Oracle Cloud Free Deployment Guide - InstaMirror

**Status:** ✅ Free Forever | No Credit Card Needed | Unlimited Time

---

## 📋 What You'll Get

- **2 × ARM VMs** (4GB RAM each) - for all Java microservices
- **20GB MySQL Database** (Autonomous DB - completely free)
- **Networking** - Load balancer, VPN, DNS (all free)
- Total cost: **$0/month forever**

---

## 🎯 Step-by-Step Deployment

### **PART 1: Oracle Cloud Account Setup (5 mins)**

1. **Create Free Account**
   - Go to: https://www.oracle.com/cloud/free/
   - Click "Start for free"
   - Provide email, password, region (choose closest to you)
   - Verify email
   - **NO credit card required for free tier**

2. **Verify Account**
   - Check email for verification link
   - Complete identity verification (SMS code)
   - Account ready in ~10 mins

---

### **PART 2: Create Compute VM (10 mins)**

1. **Launch Compute Instance**
   - Go to Oracle Cloud Console
   - Menu → **Compute** → **Instances**
   - Click **Create Instance**

2. **Configure Instance**
   - **Name:** `instamirror-backend`
   - **Image:** Oracle Linux 8 (free tier eligible)
   - **Shape:** Ampere ARM A1 (always free tier)
   - **Cores:** 4 cores (max free)
   - **RAM:** 24GB (max free)
   - **Root volume:** 100GB

3. **Networking**
   - Create new VCN (Virtual Cloud Network) or use default
   - Subnet: Public (needs internet access)
   - Auto-assign public IP: **ON**

4. **Key Pair**
   - Download private key (`.key` file)
   - **SAVE THIS SAFELY** - you'll need it to SSH

5. **Create** - Takes ~3 mins to boot

---

### **PART 3: Create MySQL Database (15 mins)**

1. **Create Autonomous Database**
   - Menu → **Database** → **Autonomous Database**
   - Click **Create Autonomous Database**

2. **Configure Database**
   - **Workload type:** Transaction Processing
   - **Deployment type:** Shared Infrastructure
   - **Database name:** `instamirror_db`
   - **DB version:** MySQL 8.0.35
   - **Admin password:** (save this!)
   - **Network access:** Secure external access (configure firewall)

3. **Security (Important)**
   - **Require mutual TLS:** OFF (easier to connect)
   - **IP Whitelist:** Add your IP + Compute VM IP
   - Click **Create** - Takes ~5 mins

4. **Get Connection Details**
   - Go to Database Details
   - Copy **Connection String**: `mysql://<hostname>:3306`
   - Copy **Username**: `admin`
   - Note the **password** you set

---

### **PART 4: Connect to Compute VM (10 mins)**

1. **SSH into VM**
   ```bash
   chmod 600 /path/to/private-key.key
   ssh -i /path/to/private-key.key ubuntu@<VM-PUBLIC-IP>
   ```
   (Replace with your VM's public IP from Console)

2. **Install Docker**
   ```bash
   sudo yum update -y
   sudo yum install -y docker-engine
   sudo systemctl start docker
   sudo usermod -aG docker $USER
   ```

3. **Install Git**
   ```bash
   sudo yum install -y git
   ```

4. **Clone Your Repo**
   ```bash
   git clone https://github.com/venkat50sai/InstaMirror-.git
   cd InstaMirror-/backend
   ```

---

### **PART 5: Update Database Configuration (5 mins)**

1. **Edit Docker startup script**
   ```bash
   nano backend-start.sh
   ```

2. **Update these lines to match Oracle MySQL**
   ```bash
   export MYSQL_HOST=<oracle-mysql-hostname>  # from Connection String
   export MYSQL_PORT=3306
   export MYSQL_DB=instamirror_db
   export MYSQL_USER=admin
   export MYSQL_PASS=<your-password>          # from step 3.2
   ```

3. **Save & Exit** (Ctrl+X → Y → Enter)

---

### **PART 6: Build & Deploy Docker (15 mins)**

1. **Build Docker Image**
   ```bash
   sudo docker build -t instamirror:latest .
   ```
   (Wait ~10 mins for Maven build)

2. **Run Container**
   ```bash
   sudo docker run -d \
     --name instamirror-container \
     -p 8761:8761 \
     -p 8888:8888 \
     -p 8081-8088:8081-8088 \
     -e MYSQL_HOST=<oracle-mysql-hostname> \
     -e MYSQL_PORT=3306 \
     -e MYSQL_DB=instamirror_db \
     -e MYSQL_USER=admin \
     -e MYSQL_PASS=<your-password> \
     instamirror:latest
   ```

3. **Check Logs**
   ```bash
   sudo docker logs -f instamirror-container
   ```
   Wait for all services to start (look for "Successfully registered with Eureka")

---

### **PART 7: Configure Firewall (5 mins)**

1. **Open Ports on Oracle Firewall**
   - Go to Console → **Networking** → **Virtual Cloud Networks**
   - Select your VCN
   - Go to **Security Lists** → **Default Security List**
   - Click **Add Ingress Rules**

2. **Add these rules:**
   - **Port 8761** (Eureka)
   - **Port 8888** (API Gateway)
   - **Ports 8081-8088** (Microservices)
   - Allow from: `0.0.0.0/0` (public internet)

3. **Save**

---

### **PART 8: Deploy Frontend (Angular) - Optional (10 mins)**

**Option A: Host on same VM**
```bash
cd frontend
npm install
npm run build
sudo docker build -t instamirror-frontend:latest .
sudo docker run -d \
  --name frontend \
  -p 80:80 \
  -e API_URL=http://<vm-public-ip>:8888 \
  instamirror-frontend:latest
```

**Option B: Deploy to Vercel (Recommended - easier)**
1. Go to https://vercel.com
2. Sign up with GitHub
3. Import repository
4. Set environment variables:
   ```
   REACT_APP_API_URL=http://<vm-public-ip>:8888
   ```
5. Deploy - done!

---

## 🔍 Verification Checklist

- [ ] Oracle Cloud account created
- [ ] Compute VM running (status: Running)
- [ ] MySQL Database created (status: Available)
- [ ] Docker image built successfully
- [ ] Container running without errors
- [ ] Eureka dashboard accessible: `http://<vm-public-ip>:8761`
- [ ] API Gateway responding: `http://<vm-public-ip>:8888/user/`
- [ ] All services showing in Eureka dashboard (USER-SERVICE, POST-SERVICE, etc.)

---

## 🐛 Troubleshooting

**Problem: Docker build fails (Maven timeout)**
```bash
# Increase Docker build memory
sudo dockerd --memory 4g
```

**Problem: MySQL connection refused**
- Check Oracle Database firewall rules
- Verify IP whitelist includes VM IP
- Test connection: `mysql -h <host> -u admin -p`

**Problem: Container exits immediately**
```bash
# Check logs for errors
sudo docker logs instamirror-container
```

**Problem: Eureka not accessible**
- Check VM firewall rules (port 8761 open?)
- Check container logs: `sudo docker logs instamirror-container`

---

## 📱 Access Your Application

Once deployed:

**Eureka Dashboard:** `http://<vm-public-ip>:8761/eureka/web`
**API Gateway:** `http://<vm-public-ip>:8888/user/`
**Frontend:** `https://<vercel-url>` (if using Vercel)

---

## 💾 Summary

| Step | Time | What You Get |
|------|------|-------------|
| Account + VM | 15 mins | Running Linux server |
| Database | 15 mins | MySQL with 20GB storage |
| Docker Deploy | 20 mins | All services running |
| Frontend | 10 mins | Complete app online |
| **Total** | **~60 mins** | **Complete deployment** |

**Total Cost: $0 forever**

---

## ⚠️ Important Notes

1. **Free tier has limits:**
   - Max 2 ARM VMs (you need 1)
   - Max 20GB database (you have plenty)
   - Max 100GB storage per VM (enough)

2. **Always-free means:**
   - No credit card ever
   - No expiration date
   - Services never stop (unless you delete them)

3. **Best practices:**
   - Keep VM updated: `sudo yum update -y`
   - Monitor database usage
   - Set up backups for production

---

**Ready to deploy? Follow these steps in order. Good luck! 🚀**
