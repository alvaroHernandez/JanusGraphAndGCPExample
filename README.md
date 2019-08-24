# Example with Twitter Stream, GCP PubSub, JanusGraph, BigTable and Elasticsearch

This example show how to connect to Twitter Stream API, publish messages to Google Pub/Sub and consume them in order to add to a JanusGraph database.

## How to run?

Choose a project name, for example: bigDataTraining

### Infra

gcloud auth login
export GOOGLE_PROJECT=bigDataTraining
export CLOUDSDK_CORE_PROJECT=$GOOGLE_PROJECT
export GOOGLE_APPLICATION_CREDENTIALS=~/.config/gcloud/$GOOGLE_PROJECT-terraform-admin.json
gcloud iam service-accounts create terraform --display-name "Terraform admin account"
export TF_CREDS=~/.config/gcloud/$GOOGLE_PROJECT-terraform-admin.json
gcloud iam service-accounts keys create ${TF_CREDS} --iam-account terraform@${GOOGLE_PROJECT}.iam.gserviceaccount.com


gcloud projects add-iam-policy-binding ${GOOGLE_PROJECT} \
  --member serviceAccount:terraform@${GOOGLE_PROJECT}.iam.gserviceaccount.com \
  --role roles/editor

gcloud projects add-iam-policy-binding ${GOOGLE_PROJECT} \
  --member serviceAccount:terraform@${GOOGLE_PROJECT}.iam.gserviceaccount.com \
  --role roles/storage.admin

#install helm
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get | bash

gcloud services enable cloudresourcemanager.googleapis.com
gcloud services enable cloudbilling.googleapis.com
gcloud services enable iam.googleapis.com
gcloud services enable compute.googleapis.com
gcloud services enable serviceusage.googleapis.com
gcloud services enable pubsub.googleapis.com
gcloud services enable bigtable.googleapis.com
gcloud services enable bigtableadmin.googleapis.com

gsutil mb -p ${GOOGLE_PROJECT} gs://${GOOGLE_PROJECT}
gsutil versioning set on gs://${GOOGLE_PROJECT}





export PROJECT=metadatatraining
gcloud config set $PROJECT


#download kubectl credentials and configure helm with new kubectl service account 
gcloud container clusters get-credentials janusgraph-cluster --zone southamerica-east1
kubectl create serviceaccount tiller --namespace kube-system
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin \
    --serviceaccount=kube-system:tiller
helm init --service-account=tiller
until (timeout 7 helm version > /dev/null 2>&1); do echo "Waiting for tiller install..."; done


#install janusgraph and elasticsearch with helm
export GOOGLE_PROJECT=metadatatraining
export INSTANCE_ID=graph-database-backend
helm install --wait --timeout 600 --name janusgraph stable/janusgraph -f helm-dependencies/janusgraph.yaml

helm ls --all janusgraph
helm del --purge janusgraph

