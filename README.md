# Scaling on Satisfaction: Automated Rollouts Driven by User Feedback

![dependencies](https://dependency-track.devex.thomasvitale.dev/api/v1/badge/vulns/project/scaling-on-satisfaction/main)
![policies](https://dependency-track.devex.thomasvitale.dev/api/v1/badge/violations/project/scaling-on-satisfaction/main)

As the old saying goes, “the customer is always right”. When it comes to GenAI, the end users of our apps are indeed always right, because we can’t fully trust an LLM on its own. What if we used a new mechanism to guide the routing and rollout of new application versions? How about user feedback? Imagine that: the more an app variant gets up-voted, the more traffic is sent to it.

In this session, Whitney and Thomas demo a platform that enables app developers to define success criteria for business operations involving GenAI, and to capture users’ feedback using OpenTelemetry - including how to correlate it with other observability data. Then comes the twist. They’ll expand the platform using Flagger and Knative so that the users control the rollout and routing of new apps. And today, YOU are the user, the audience!

You’ll see how this technique can be applied beyond GenAI, and take part in an interactive story that evolves and changes course in real time based on your feedback!

## Project

This demo application is a Java variant of the original Node.js application developed by Whitney Lee. Check out the original project [here](https://github.com/wiggitywhitney/scaling-on-satisfaction).

## Stack

* Java 25 (with GraalVM)
* Spring Boot 4.0
* Arconia

## Development environment

This project uses [Flox](https://flox.dev/) to manage the development and build environment via [Nix](https://nixos.org). After [installing](https://flox.dev/docs/install-flox/install/) the Flox CLI (open-source), activate the environment:

```shell
flox activate
```

Alternatively, ensure you have Java 25 installed.

Either way, you'll need a container runtime like Podman or Docker installed on your machine.

## Mistral AI

The application consumes models from the [Mistral AI](https://mistral.ai) platform.

### Create an account

Visit [console.mistral.ai](https://console.mistral.ai) and sign up for a new account.
You can choose the "Experiment" plan, which gives you access to the Mistral APIs for free.

### Configure API Key

In the Mistral AI console, navigate to _API Keys_ and generate a new API key.
Copy and securely store your API key on your machine as an environment variable.
The application will use it to access the Mistral AI API.

```shell
export MISTRAL_AI_API_KEY=<YOUR-API-KEY>
```

## Anthropic

The application also consumes models from the [Anthropic](https://www.anthropic.com) platform.

### Create an account

Visit [console.anthropic.com](https://console.anthropic.com) and sign up for a new account.

### Configure API Key

In the Anthropic console, navigate to _API Keys_ and generate a new API key.
Copy and securely store your API key on your machine as an environment variable.
The application will use it to access the Anthropic API.

```shell
export ANTHROPIC_API_KEY=<YOUR-API-KEY>
```

## Running the application

Run the application.

```shell
./gradlew bootRun
```

Alternatively, you can use the [Arconia CLI](https://docs.arconia.io/arconia-cli/latest/index.html):

```shell
arconia dev
```

Under the hood, the Arconia framework will automatically spin up the needed backing services using [Arconia Dev Services](https://arconia.io/docs/arconia/latest/dev-services/) and Testcontainers:

* [Grafana LGTM](https://arconia.io/docs/arconia/latest/dev-services/lgtm/) OpenTelemetry-based observability platform
* [PostgreSQL](https://arconia.io/docs/arconia/latest/dev-services/postgresql/) database.

The application will be accessible at http://localhost:8080.

## Accessing Grafana

The application logs will show you the URL where you can access the Grafana observability platform.

```logs
...o.t.grafana.LgtmStackContainer: Access to the Grafana dashboard: http://localhost:<port>
```

By default, logs, metrics, and traces are exported via OTLP using the HTTP/Protobuf format.

In Grafana, you can query the telemetry from the "Drilldown" and "Explore" sections.
