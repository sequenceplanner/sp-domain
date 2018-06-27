# Sequence Planner 2
Sequence Planner (SP) is a micro service architecture for modelling and analyzing automation systems. Initially, the focus was on supporting engineers in developing control code for programmable logical controllers (PLCs). 

During the first years, algorithms to handle product and automation system interaction, and to visualize complex operation sequences using multiple projections, was developed. 

Over the years, other use cases have been integrated, like formal verification and synthesis using Supremica, restart support, cycle time optimization, energy optimization and hybrid systems, online monitoring and control (the tweeting factory), as well as emergency department online planning.

## SP-DOMAIN
This is the sub-project of SP with origin at https://github.com/kristoferB/SP

Now we have split up the SP source code and published each sub-project to Sonatype. To each new SP-project, let the project add our libraries needed as dependencies. Domain is used as a dependecy, but it's not dependent on any other SP Repository.

In sp-domain we have defined different structures used in SP. Take SOP as an example, where we define the logic behind to update and make new SOP:s. 

## Wiki
Watch our wiki for information about Sequence Planner and how to use.

### Setup
Download and install Simple Build Tool (SBT). Files and instructions for your platform are available at the project website, http://www.scala-sbt.org/.

### Run
SP is a set of micro services that communicates via json messages in an akka cluster. 

You have to start each service or group of services in a seperate terminal.
