variable "teamcity_host" {
  description = "Your TeamCity Cloud URL, e.g. https://your-org.teamcity.com"
  type        = string
}

variable "teamcity_token" {
  description = "TeamCity API access token"
  type        = string
  sensitive   = true
}

variable "github_app_id" {
  description = "TeamCity connection ID of your GitHub App connection (set up once in TC UI under Connections)"
  type        = string
}

variable "github_repo_url" {
  description = "HTTPS URL of your GitHub repo, e.g. https://github.com/your-org/your-repo"
  type        = string
}

variable "github_default_branch" {
  description = "Default branch to track"
  type        = string
  default     = "main"
}

variable "github_username" {
  description = "GitHub username for VCS root authentication"
  type        = string
}