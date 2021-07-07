[![Build And Test](https://github.com/KvalitetsIT/medcom-vdx-audit-api/actions/workflows/build.yml/badge.svg)](https://github.com/KvalitetsIT/medcom-vdx-audit-api/actions/workflows/build.yml) ![Test Coverage](.github/badges/jacoco.svg)

# medcom-vdx-audit-api
Java API for the Medcom Audit Service

Include it in your Spring Boot project and Spring will do its magic to create a bean of type
dk.medcom.audit.client.AuditClient. Use it in your application to create audit events. 

## Configuration

Below configuration properties can/must be set. 

| Environment variable       | Description                                                                                    |           Required  |
| -------------------------- |------------------------------------------------------------------------------------------------| -----------------------------|
| audit.nats.cluster.id      | Nats cluster id | Yes, if audit.nats.disabled is not set to true. |
| audit.nats.client.id       | Nats client id | Yes, if audit.nats.disabled is not set to true. |
| audit.nats.curl            | Nats url to connect to | Yes, if audit.nats.disabled is not set to true. |
| audit.nats.subject         | Nats subject to publish to | Yes, if audit.nats.disabled is not set to true. |
| audit.nats.disabled        | Disable NATS audit integration. Must be true or false. If set to true integration to nats is disabled and audit is debug logged through logging framework. | No |

## Release

To create a new release run `mvn release:prepare`. This will create a new tag in Git. CI/CD pipeline will then do its magic to push a new version to GitHub Maven repository. 
