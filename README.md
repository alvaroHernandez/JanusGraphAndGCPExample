
# Example with Twitter Stream, GCP PubSub, JanusGraph, BigTable and Elasticsearch

This example show how to connect to Twitter Stream API, publish messages to Google Pub/Sub and consume them in order to add to a JanusGraph database.

## How to run

### Preare GCP Project

1. Create project:
```
gcloud auth login
export GOOGLE_PROJECT=janusgraph-and-gcp-example
gcloud projects create $GOOGLE_PROJECT
```
2. Setup Billing account for project

Go to [https://console.cloud.google.com/billing](https://console.cloud.google.com/billing) and link your recently created project to a billing account.

3. Run the following commands to setup the project.
```
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
gcloud projects add-iam-policy-binding ${GOOGLE_PROJECT} \
  --member serviceAccount:terraform@${GOOGLE_PROJECT}.iam.gserviceaccount.com \
  --role roles/iam.securityAdmin

gcloud services enable cloudresourcemanager.googleapis.com
gcloud services enable cloudbilling.googleapis.com
gcloud services enable iam.googleapis.com
gcloud services enable compute.googleapis.com
gcloud services enable serviceusage.googleapis.com
gcloud services enable pubsub.googleapis.com
gcloud services enable bigtable.googleapis.com
gcloud services enable bigtableadmin.googleapis.com
gcloud services enable kubernetesengine.googleapis.com

gsutil mb -p ${GOOGLE_PROJECT} gs://${GOOGLE_PROJECT}
gsutil versioning set on gs://${GOOGLE_PROJECT}
```

### Deploy Pub/Sub

1. Go to infra/components/event-broker
2. Run:

```
export GCP_REGION=southamerica-east1
export GCP_AZ=a
terraform init -backend-config="bucket=$GOOGLE_PROJECT"
terraform apply -var gcp_region=$GCP_REGION -var availability_zone=$GCP_AZ -var project_id=$GOOGLE_PROJECT
```
### Deploy JanusGraph

1. Go to infra/components/database
2. Run:

```
export GCP_REGION=southamerica-east1
export GCP_AZ=a
terraform init -backend-config="bucket=$GOOGLE_PROJECT"
terraform apply -var gcp_region=$GCP_REGION -var availability_zone=$GCP_AZ -var project_id=$GOOGLE_PROJECT
```
3. Go to project root and run the following commands to configure helm and Kubernetes:

```
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get | bash
gcloud container clusters get-credentials janusgraph-cluster --zone $GCP_REGION
kubectl create serviceaccount tiller --namespace kube-system
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin \
    --serviceaccount=kube-system:tiller
helm init --service-account=tiller
until (gtimeout 7 helm version > /dev/null 2>&1); do echo "Waiting for tiller install..."; done
```
4. Install Janusgraph and Elasticsearch using Helm:
```
export INSTANCE_ID=graph-database-backend
helm install --wait --timeout 600 --name janusgraph helm-dependencies/janusgraph -f helm-dependencies/janusgraph/override-values.yaml
```

Notes: 
1. I couldn't set elasticsearch service type LoadBalancer from janusgraph chart values, so I'm installing it from local chart
2. `helm-dependencies/janusgraph/override-values.yaml` has defined `storage.hbase.ext.google.bigtable.project.id: janusgraph-and-gcp-example`, if you are using a different project name you should change that value.


### Run GraphCompositor App

1. Go to Google Console and create and download keys for PubSub Service account and JanusGraph Service Account created with terraform.
2. Go to graphcompositor folder and run:
```
export GOOGLE_PROJECT=janusgraph-and-gcp-example
export GCP_CREDENTIALS_LOCATION={locationOfYourPubSubServiceAccountKey}
export GOOGLE_APPLICATION_CREDENTIALS={locationOfYourJanusGraphServiceAccountKey}
export GCP_BIGTABLE_INSTANCE_ID=graph-database-backend
export ELASTICSEARCH_HOST={hostNameOfYourElasticsearchLoadBalancerOnKubernetesEngine}

./gradlew bootRun
```

### Run TwitterIngester App
1. Go to Google Console and create and download keys for PubSub Service account and JanusGraph Service Account created with terraform.
2. Go to twiteringester
3. Add your twitter account secrets on twitter4j.properties
4. Run:
```
export GOOGLE_PROJECT=janusgraph-and-gcp-example
export GCP_CREDENTIALS_LOCATION={locationOfYourPubSubServiceAccountKey}
./gradlew bootRun
```