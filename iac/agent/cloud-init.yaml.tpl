#cloud-config

# Installs a TeamCity build agent with the same toolchain used by the project
# (cmake + ninja + g++ + git).  The agent connects to ${tc_server_url}.

package_update: true
package_upgrade: false

packages:
  - openjdk-17-jre-headless
  - cmake
  - ninja-build
  - g++
  - git
  - curl
  - unzip

runcmd:
  # Create a dedicated system user
  - useradd --system --create-home --shell /bin/bash teamcity

  # agent from the TC server 
  - mkdir -p /opt/teamcity-agent
  - curl -fsSL "${tc_server_url}/update/buildAgent.zip" -o /tmp/buildAgent.zip
  - unzip -q /tmp/buildAgent.zip -d /opt/teamcity-agent
  - rm /tmp/buildAgent.zip
  - chmod +x /opt/teamcity-agent/bin/agent.sh
  - chown -R teamcity:teamcity /opt/teamcity-agent

  # buildAgent.properties 
  - cp /opt/teamcity-agent/conf/buildAgent.dist.properties /opt/teamcity-agent/conf/buildAgent.properties
  - |
    cat >> /opt/teamcity-agent/conf/buildAgent.properties << 'PROPS'
    serverUrl=${tc_server_url}
    name=${agent_name}
    %{ if tc_agent_token != "" ~}
    authorizationToken=${tc_agent_token}
    %{ endif ~}
    PROPS

  # Install as a systemd service 
  - |
    cat > /etc/systemd/system/teamcity-agent.service << 'UNIT'
    [Unit]
    Description=TeamCity Build Agent
    After=network-online.target
    Wants=network-online.target

    [Service]
    Type=forking
    User=teamcity
    WorkingDirectory=/opt/teamcity-agent
    ExecStart=/opt/teamcity-agent/bin/agent.sh start
    ExecStop=/opt/teamcity-agent/bin/agent.sh stop
    PIDFile=/opt/teamcity-agent/logs/teamcity-agent.pid
    Restart=on-failure
    RestartSec=30

    [Install]
    WantedBy=multi-user.target
    UNIT
  - systemctl daemon-reload
  - systemctl enable teamcity-agent
  - systemctl start teamcity-agent
