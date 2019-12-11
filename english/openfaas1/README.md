# faasguru
Software, tips and labs about FaaS and Serverless technology

**Cleuton Sampaio** 

![](../../faasguru1.jpeg)

![](../../images/openfaas.png)

# Open FaaS: Your own Serverless platform

[**Open FaaS**] (https://www.openfaas.com/) is a **Kubernetes** application to serve **Serverless** code. It's simple, practical and with everything needed to expose functions in Java, Python, Go and any other language, with minimal configuration.

I will show you how to install an Open FaaS environment on your personal computer so you can start building your FaaS apps.

I will install Open FaaS using:
- [**Minikube**] (https://kubernetes.io/docs/tasks/tools/install-minikube/#confirm-installation);
- [**Docker**] (https://phoenixnap.com/kb/how-to-install-docker-on-ubuntu-18-04);

## Installation

Start by installing docker  [**per these instructions**](https://phoenixnap.com/kb/how-to-install-docker-on-ubuntu-18-04). It should be as simple as:
```
sudo apt-get update
sudo apt-get remove docker docker-engine docker.io
sudo apt install docker.io
```

Start docker service: 

```
sudo systemctl start docker
sudo systemctl enable docker
```

Then, install [**kubectl**](https://kubernetes.io/docs/tasks/tools/install-kubectl/#install-kubectl-on-linux):

```
curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl

chmod +x ./kubectl

sudo mv ./kubectl /usr/local/bin/kubectl
```

Now, download and install [**Minikube**](https://kubernetes.io/docs/tasks/tools/install-minikube/#confirm-installation): 

```
curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 \
  && chmod +x minikube

sudo mkdir -p /usr/local/bin/
sudo install minikube /usr/local/bin/
```

## Creating a Kubernetes cluster

Now is the time to create  a cluster with Minikube. Here, I will only use **docker**, but if you want you can use **Virtualbox** to create VMs:

```
minikube start --vm-driver=none
```

This command takes **a very long time** as it downloads the images needed to upload your cluster. After it finishes, you can verify that the cluster is live:

```
kubectl cluster-info
...
Kubernetes master is running at https://10.29.131.104:8443
KubeDNS is running at https://10.29.131.104:8443/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.

```

If you want to stop Minikube: 

```
minikube stop
```

## Open FaaS installation

Let's install Open FaaS with [**k3sup**](https://github.com/alexellis/k3sup), since it's so easy. First, install k3sup:

```
curl -SLsf https://get.k3sup.dev/ | sudo sh
```

Then, proceed to install Open FaaS:

```
k3sup app install openfaas
```

After this command, you will see some messages on the console, for example: 

```
# Get the faas-cli
curl -SLsf https://cli.openfaas.com | sudo sh

# Forward the gateway to your machine
kubectl rollout status -n openfaas deploy/gateway
kubectl port-forward -n openfaas svc/gateway 8080:8080 &

# If basic auth is enabled, you can now log into your gateway:
PASSWORD=$(kubectl get secret -n openfaas basic-auth -o jsonpath="{.data.basic-auth-password}" | base64 --decode; echo)
echo -n $PASSWORD | faas-cli login --username admin --password-stdin

faas-cli store deploy figlet
faas-cli list

# For Raspberry Pi
faas-cli store list \
 --platform armhf

faas-cli store deploy figlet \
 --platform armhf

# Find out more at:
# https://github.com/openfaas/faas
```

These are the next steps!

## FaaS cli

Install Open FaaS command line interface: 

```
curl -SLsf https://cli.openfaas.com | sudo sh
```

## Create a port forward 

Now you can browse to the [**Open FaaS console**](http://localhost:8080/ui/): 

```
kubectl rollout status -n openfaas deploy/gateway
kubectl port-forward -n openfaas svc/gateway 8080:8080 &
```
## Using the CLI

To use the CLI you need credentials. The deployment generated a password and we can capture it with this command: 

```
PASSWORD=$(kubectl get secret -n openfaas basic-auth -o jsonpath="{.data.basic-auth-password}" | base64 --decode; echo)
```

Then just **login** on CLI: 

```
echo -n $PASSWORD | faas-cli login --username admin --password-stdin
```

That's it! Your FaaS environment is ready to receive functions!

## Deploy functions using CLI

Let's deploy our first function (figlet): 

```
faas-cli store deploy figlet
```

If all went well, you can list it:

```
faas-cli list
```

Or you can search it in the UI: http://localhost:8080/ui. In this case, you need to enter the **username** admin and the password you got from the deployment (the same one you used to login to faas-cli):

![](../../images/openfaas1.png)

To invoke this function using CLI: 

```
cleuton@cleuton:~/template/go$ echo "OpenFaaS!" | faas-cli invoke figlet
  ___                   _____           ____  _ 
 / _ \ _ __   ___ _ __ |  ___|_ _  __ _/ ___|| |
| | | | '_ \ / _ \ '_ \| |_ / _` |/ _` \___ \| |
| |_| | |_) |  __/ | | |  _| (_| | (_| |___) |_|
 \___/| .__/ \___|_| |_|_|  \__,_|\__,_|____/(_)
      |_|                                       
```

## Face detection

Open FaaS has a **function store** with several interesting examples:

```
faas-cli store list
```

There is a very interesting function which is this: 

```
faas-cli store deploy "Face Detection with Pigo"
```

It is a very interesting face detector in images. We can invoke it from the UI:

![](../../images/openfaas2.png)

Just mark **download** and enter an image URL that contains faces. The function will mark the detected faces.

## Conclusion

Open FaaS is an interesting alternative to creating your own **Serverless** environment. In the next articles, I will show you how to create functions for it and various programming languages. 

