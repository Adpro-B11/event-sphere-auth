#!/bin/bash

# Event Sphere Auth Monitoring Setup Script

echo "🚀 Setting up monitoring for Event Sphere Auth..."

# Create monitoring directory structure
echo "📁 Creating directory structure..."
mkdir -p monitoring/grafana/{provisioning/{datasources,dashboards},dashboards}
mkdir -p monitoring/prometheus

# Create Prometheus configuration
echo "⚙️ Creating Prometheus configuration..."
cat > monitoring/prometheus.yml << 'EOF'
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'event-sphere-auth'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['host.docker.internal:8081']
    scrape_timeout: 5s

  - job_name: 'event-sphere-auth-main'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['host.docker.internal:8080']
    scrape_timeout: 5s
EOF

# Create Grafana datasource configuration
echo "📊 Creating Grafana datasource configuration..."
cat > monitoring/grafana/provisioning/datasources/datasource.yml << 'EOF'
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true
EOF

# Create Grafana dashboard provisioning
echo "📈 Creating Grafana dashboard provisioning..."
cat > monitoring/grafana/provisioning/dashboards/dashboard.yml << 'EOF'
apiVersion: 1

providers:
  - name: 'default'
    orgId: 1
    folder: ''
    type: file
    disableDeletion: false
    updateIntervalSeconds: 10
    allowUiUpdates: true
    options:
      path: /var/lib/grafana/dashboards
EOF

# Create the dashboard JSON file
echo "🎯 Creating Event Sphere Auth dashboard..."
cat > monitoring/grafana/dashboards/event-sphere-auth-dashboard.json << 'EOF'
{
  "dashboard": {
    "id": null,
    "title": "Event Sphere Auth Metrics",
    "tags": ["spring-boot", "auth", "event-sphere"],
    "timezone": "browser",
    "refresh": "5s",
    "schemaVersion": 27,
    "version": 1,
    "panels": [
      {
        "id": 1,
        "title": "HTTP Request Latency",
        "type": "timeseries",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{job=\"event-sphere-auth\"}[5m])) by (le))",
            "legendFormat": "95th percentile"
          },
          {
            "expr": "histogram_quantile(0.50, sum(rate(http_server_requests_seconds_bucket{job=\"event-sphere-auth\"}[5m])) by (le))",
            "legendFormat": "50th percentile"
          }
        ],
        "gridPos": {"h": 8, "w": 12, "x": 0, "y": 0},
        "fieldConfig": {
          "defaults": {
            "unit": "s",
            "min": 0
          }
        }
      },
      {
        "id": 2,
        "title": "HTTP Request Rate",
        "type": "timeseries",
        "targets": [
          {
            "expr": "sum(rate(http_server_requests_total{job=\"event-sphere-auth\"}[1m])) by (status)",
            "legendFormat": "Status {{status}}"
          }
        ],
        "gridPos": {"h": 8, "w": 12, "x": 12, "y": 0}
      },
      {
        "id": 3,
        "title": "User Registrations",
        "type": "stat",
        "targets": [
          {
            "expr": "auth_user_registrations_total",
            "legendFormat": "Total"
          }
        ],
        "gridPos": {"h": 4, "w": 6, "x": 0, "y": 8}
      },
      {
        "id": 4,
        "title": "User Logins",
        "type": "stat",
        "targets": [
          {
            "expr": "auth_user_logins_total",
            "legendFormat": "Total"
          }
        ],
        "gridPos": {"h": 4, "w": 6, "x": 6, "y": 8}
      },
      {
        "id": 5,
        "title": "Login Failures",
        "type": "stat",
        "targets": [
          {
            "expr": "auth_user_login_failures_total",
            "legendFormat": "Total"
          }
        ],
        "gridPos": {"h": 4, "w": 6, "x": 12, "y": 8},
        "fieldConfig": {
          "defaults": {
            "color": {
              "mode": "thresholds"
            },
            "thresholds": {
              "steps": [
                {"color": "green", "value": null},
                {"color": "red", "value": 1}
              ]
            }
          }
        }
      },
      {
        "id": 6,
        "title": "JWT Tokens Generated",
        "type": "stat",
        "targets": [
          {
            "expr": "auth_jwt_tokens_generated_total",
            "legendFormat": "Total"
          }
        ],
        "gridPos": {"h": 4, "w": 6, "x": 18, "y": 8}
      }
    ],
    "time": {
      "from": "now-1h",
      "to": "now"
    }
  }
}
EOF

echo "🐳 Starting monitoring stack with Docker Compose..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Start the monitoring stack
docker-compose up -d

echo "✅ Monitoring stack is starting up!"
echo ""
echo "🔗 Access URLs:"
echo "   📊 Grafana:    http://localhost:3001 (admin/admin123)"
echo "   📈 Prometheus: http://localhost:9090"
echo "   🔍 App Metrics: http://localhost:8081/actuator/prometheus"
echo "   ❤️ Health Check: http://localhost:8080/actuator/health"
echo ""
echo "⏳ Please wait a few moments for all services to start up..."
echo "🎯 The dashboard will be automatically configured in Grafana!"

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check if services are running
if docker-compose ps | grep -q "Up"; then
    echo "✅ Monitoring services are running successfully!"
    echo ""
    echo "🚀 Next steps:"
    echo "   1. Start your Event Sphere Auth application"
    echo "   2. Open Grafana at http://localhost:3001"
    echo "   3. Login with admin/admin123"
    echo "   4. Navigate to Dashboards -> Event Sphere Auth Metrics"
    echo "   5. Generate some traffic to see metrics!"
else
    echo "❌ Some services failed to start. Check with: docker-compose logs"
fi