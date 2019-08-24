terraform {
 backend "gcs" {
   bucket  = "tfstate-${var.project_id}"
   prefix  = "terraform/state/event-broker"
 }
}

provider "google" {
  project = var.project_id
  region  = var.gcp_region
  zone    = var.availability_zone
}

module "event-broker" {
  source = "../../modules/event-broker"
  project_id = var.project_id
}