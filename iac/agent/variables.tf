# ── OpenStack credentials ────────────────────────────────────────────────────

variable "os_auth_url" {
  description = "Keystone auth URL"
  type        = string
}

variable "os_username" {
  description = "OpenStack username"
  type        = string
}

variable "os_password" {
  description = "OpenStack password"
  type        = string
  sensitive   = true
}

variable "os_tenant_name" {
  description = "OpenStack project"
  type        = string
}

variable "os_region_name" {
  description = "OpenStack region"
  type        = string
}

# ── Compute settings ─────────────────────────────────────────────────────────

variable "image_name" {
  description = "mage name to boot from"
  type        = string
  default     = "Ubuntu 22.04"
}

variable "key_pair" {
  description = "Name of the SSH key pair already imported in OpenStack"
  type        = string
}

variable "network_name" {
  description = "Name of the private network to attach the instance to"
  type        = string
}

variable "security_groups" {
  description = "List of security group names to apply"
  type        = list(string)
  default     = ["default"]
}

variable "agent_count" {
  description = "Number of agent VMs to create"
  type        = number
  default     = 1
}

# ── TeamCity agent settings ───────────────────────────────────────────────────

variable "tc_server_url" {
  description = "TeamCity server URL, e.g. https://my-org.teamcity.com"
  type        = string
  default     = "https://tdg-hxgn-dl.teamcity.com"
}

variable "tc_agent_token" {
  description = "Optional unattended agent token"
  type        = string
  sensitive   = true
  default     = ""
}
