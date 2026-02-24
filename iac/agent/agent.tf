terraform {
  required_providers {
    openstack = {
      source  = "terraform-provider-openstack/openstack"
      version = "~> 1.54"
    }
  }
}

provider "openstack" {
  auth_url    = var.os_auth_url
  user_name   = var.os_username
  password    = var.os_password
  tenant_name = var.os_tenant_name
  region      = var.os_region_name
}

data "openstack_images_image_v2" "agent_image" {
  name        = var.image_name
  most_recent = true
}

data "openstack_compute_flavor_v2" "agent_flavor" {
  name = "a1-ram1024-disk10"
}

resource "openstack_compute_instance_v2" "tc_agent" {
  count = var.agent_count

  name            = "tc-agent-${count.index + 1}"
  image_id        = data.openstack_images_image_v2.agent_image.id
  flavor_id       = data.openstack_compute_flavor_v2.agent_flavor.id
  key_pair        = var.key_pair
  security_groups = var.security_groups

  network {
    name = var.network_name
  }

  user_data = templatefile("${path.module}/cloud-init.yaml.tpl", {
    tc_server_url  = var.tc_server_url
    tc_agent_token = var.tc_agent_token
    agent_name     = "tc-agent-${count.index + 1}"
  })

  metadata = {
    role = "teamcity-agent"
  }
}

resource "openstack_compute_floatingip_associate_v2" "tc_agent_fip" {
  count       = var.agent_count
  floating_ip = "72.29.249.154"
  instance_id = openstack_compute_instance_v2.tc_agent[count.index].id
}

output "agent_ips" {
  description = "Private IP addresses of the agent VMs"
  value       = [for inst in openstack_compute_instance_v2.tc_agent : inst.access_ip_v4]
}

output "agent_floating_ip" {
  description = "Floating IP attached to the agent"
  value       = "72.29.249.154"
}
