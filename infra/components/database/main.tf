terraform {
 backend "gcs" {
   bucket  = "tfstate-${var.project_id}"
   prefix  = "terraform/state/database"
 }
}

provider "google" {
  project = var.project_id
  region  = var.gcp_region
  zone    = var.availability_zone
}

module "database" {
  source = "../../modules/database"
  gcp_region  = var.gcp_region
  availability_zone    = var.availability_zone
  project_id = var.project_id
}