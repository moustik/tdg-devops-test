terraform {
  required_providers {
    teamcity = {
      source  = "jetbrains/teamcity"
    }
  }
}

provider "teamcity" {
  host  = var.teamcity_host
  token = var.teamcity_token
}

resource "teamcity_project" "calculator" {
  name      = "TDG - calculator"
}

resource "teamcity_vcsroot" "github" {
  project_id = teamcity_project.calculator.id
  name       = "gh-repo-tdg-devops-test"

  git = {
    # The repo URL
    url         = var.github_repo_url
    branch      = var.github_default_branch
    branch_spec = "*"

    # Auth via GitHub App connection (configured once in TC Cloud UI)
    # The connection ID comes from: Administration → Connections → your GitHub App
    auth_method = "PASSWORD"
    username    = var.github_username
    password    = var.github_app_id

    # Fetch strategy
    submodules      = "IGNORE"
    convert_crlf    = true
  }
}


resource "teamcity_versioned_settings" "calculator" {
  project_id       = teamcity_project.calculator.id
  vcsroot_id       = teamcity_vcsroot.github.id
  settings         = "useFromVCS"
  allow_ui_editing = false
  show_changes     = true
}

