CS 441 - Homework 1 - By: Anas Aleid

How to run my simulation:
1- Clone my private repo "homework1"
2- Using intellij, import the folder "homework1" as a project
3- In the terminal within intellij, type "sbt clean compile run"
4- Select either "CloudSim1" or "CloudSim2" by typing in a number. They each have a main function so it makes you choose.
I decided to do it this way so you don't have to change the config file name being read within the code. Both "CloudSim1" and
"CloudSim2" do the same exact thing, but they read from a different config file. The config files are within the "resources" folder.
If you'd like to change anything in them, go ahead. "sim1.conf" is used in "CloudSim1" and "sim2.conf" is used in "CloudSim2".
5- The selected sim should run right after you input a number selecting which you you'd like. If you'd like to run the other sim,
repeat steps 1-4 but select the other one when prompted.


Documentation:
1- First I start off by running "CloudSim.init()" in order to initialize the cloudsim. It takes in number of people, a calendar and a trace flag as parameters.
2- Second I create the datacenters. The number of datacenters created is determined by the number set in the config file.
When creating the datacenters, I grab all the specifications for the host from the config file.
3- Third, I create the brokers. The number of brokers created is determined by the number set in the config file. While creating
the brokers, I create the VMs and Cloudlets for that specific broker. The number of VMs and Cloudlets created is determined by
the config file. I submit the list of VMs and Cloudlets to the broker and move on to the next broker if another needs to be created.
The specifications of the VMs and the Cloudlets are taken from the config file.
4- I run "CloudSim.startSimulation" and "Cloudsim.stopSimulation" in order to run the actual simulation.
5- I created a foreach loop that loops through the brokers one at a time and outputs the statistics for each of their cloudlets.


CloudSim1 - Explanation:
For this simulation, I simulated having multiple datacenters with half the specs and costs as the datacenter in CloudSim2.
It runs one broker with 4 VMs and 20 Cloudlets. The VMs and Cloudlets end up being split between the two datacenters resulting in the
in the two sets of cloudlets to be processed simultaneously, cutting the process time in half. (Simulating cheaper and less expensive machines)

CloudSim2 - Explanation: 
This sim is similar to CloudSim1, but with just one datacenter with double the resources. 

CloudSim3 - Explanation:
This simulation does the same thing as CloudSim1 but with more datacenters. The extra datacenters end up not being used
due to there not being enough demand. The cost of the sim is even lower than CloudSim1 since more parallelism is introduced.

CloudSim1 ends up having half the cost of CloudSim2 due to halving multiple datacenters. The fact that each datacenter is
equivalent to only half the single datacenter in CloudSim2 seems to not be a drawback. This simulation shows that running
processes in parallel is a lot more important than running them all linearly at a faster pace.

