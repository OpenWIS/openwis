---
layout: page
title: OpenWIS Project Charter for OpenAM/OpenDS replacement by Keycloak identity server 
---


---


# Project Charter for Openwis v4.0 
## Aim

To maintain open-source nature associated with the security service. With Forgerock closing repo, we need to eliminate the need for reliance on community version of OpenAM/OpenDJ

Make the security service installation more user friendly and easier. Current process is cumbersome

Continue to support SAML

## Scope

Consider alternatives to the OpenAM/OpenDJ security service solution to maintain features use by currently in use by existing OpenWIS versions and possibility eliminate unneeded features

Re-align security service with a well established and supported open source community

Consider WIS 2 aspects involving security when examining alternatives for security service

What's important in an alternative:

Ease of use
maintenance
community support
Integration with existing code base
No loss of main functionality
Opensource solution
Run under Linux

Key dependencies to overcome:
Deployment difficulty
Migration an existing bulk of users to the new middleware
Converting existing Openwis code to the new system


## Deliverables

1/ A new identity and access management solution for Openwis (possibly Keycloak https://www.keycloak.org/).
2/ Documentations (installation, user, configuration)
3/ Scripts to migrate actual users/groups database to the new identity servers 

## Sponsor and funding source

Meteo-France and NOAA will lead the project.
A certain portion of funding from the middleware upgrade could be apply to this effort.

## Milestones & Schedule

_Please list the milestones that will be needed to achieve each of the deliverables that are included within the scope of this project._

|  Project Milestone  |  Begin Date  |  End Date  |
| ---------------------|---------------|-------------|
|  Project   start        |  April 2020 |
|  Keycloak evaluation | April 2020 | Summer 2020
|  _insert milestone 2_|
|  _insert milestone n_|
|  Project closure |

## Roles and Responsibilities

_Please list the people to involve or equipment (such as hardware and/or software) in this project; what their role in the project is; and what they will deliver (relate this to a milestone where relevant)_

Resource Name  |  Organisation  |  Role  | What this resource will be do in relation to a listed milestone | Time period resource needed for|
| ---------------------|---------------|-------------|-------------|-------------|
|  Resource 1 |    |    |  | |
|  Resource 2 |    |    |  | |
|  Resource n |    |    |  | |

## Communication and Reporting

_Please state the communication and reporting arrangements for interactions with:_  
_•	The Project Team_  
_•	Project Management MCommittee(s) Please list if more than one._  
_•	Technical Committee (TC)  and Steering Committee (SC)_  
_•	Delivery of the final report/briefing on the project outcome or study findings and recommendations_  
_•	Another relevant communication or interactions_  

## Configuration Management

_Please state how any of artefacts needed for each of the deliverables will be managed, referring to the relevant rules and rules supplements of the OpenWIS Association._
