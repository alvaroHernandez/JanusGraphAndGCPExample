resource "google_pubsub_topic" "example_topic" {
  name = "example"

  labels = {
    training = "quickstart-guide"
  }
}

resource "google_pubsub_subscription" "example_subscription" {
  name  = "example"
  topic = "${google_pubsub_topic.example_topic.name}"

  ack_deadline_seconds = 20

  message_retention_duration = "1200s"
  retain_acked_messages = true

  expiration_policy {
    ttl = "300000.5s"
  }

  labels = {
    training = "quickstart-guide"
  }
}

resource "google_service_account" "example_service_account" {
  account_id = "pubsub-quickstart"
  display_name = "Pub sub service account"
}

resource "google_project_iam_member" "pubsub_publisher_subscriber" {
  project     = var.project_id
  role        = "roles/pubsub.subscriber"
  member  = "serviceAccount:${google_service_account.example_service_account.email}"
}

resource "google_project_iam_member" "pubsub_publisher_publisher" {
  project = var.project_id
  role    = "roles/pubsub.publisher"
  member  = "serviceAccount:${google_service_account.example_service_account.email}"
}