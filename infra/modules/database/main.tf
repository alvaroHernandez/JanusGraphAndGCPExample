resource "google_bigtable_instance" "bigtable-development-instance" {
  name          = "graph-database-backend"
  instance_type = "DEVELOPMENT"

  cluster {
    cluster_id   = "graph-database-backend-cluster"
    zone         = "${var.gcp_region}-${var.availability_zone}"
    storage_type = "HDD"
  }
}

resource "google_container_cluster" "janusgraph-cluster" {
  name     = "janusgraph-cluster"
  location = var.gcp_region

  remove_default_node_pool = true
  initial_node_count = 1

  master_auth {
    username = ""
    password = ""

    client_certificate_config {
      issue_client_certificate = false
    }
  }
}

resource "google_container_node_pool" "janusgraph-cluster-nodes" {
  name       = "janusgraph-cluster-node-pool"
  location   = var.gcp_region
  cluster    = "${google_container_cluster.janusgraph-cluster.name}"
  node_count = 1

  node_config {
    preemptible  = true
    #should be "n1-standard-4" but not enough quota with google trial version :(
    machine_type = "n1-standard-2"

    metadata = {
      disable-legacy-endpoints = "true"
    }

    oauth_scopes = [
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
      "https://www.googleapis.com/auth/bigtable.admin",
      "https://www.googleapis.com/auth/bigtable.data"
    ]
  }
}

resource "google_service_account" "janusgraph_service_account" {
  account_id = "janusgraph"
  display_name = "Janusgraph service account"
}

resource "google_project_iam_member" "janusgraph_list_role" {
  project     = var.project_id
  role        = "roles/bigtable.admin"
  member  = "serviceAccount:${google_service_account.janusgraph_service_account.email}"
}

