//CS 441 - Homework 1 - By: Anas Aleid
import java.util
import java.util.Calendar
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.log4j.BasicConfigurator
import com.typesafe.config
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.Cloudlet
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.Datacenter
import org.cloudbus.cloudsim.DatacenterBroker
import org.cloudbus.cloudsim.DatacenterCharacteristics
import org.cloudbus.cloudsim.Host
import org.cloudbus.cloudsim.Log
import org.cloudbus.cloudsim.Pe
import org.cloudbus.cloudsim.Storage
import org.cloudbus.cloudsim.UtilizationModel
import org.cloudbus.cloudsim.UtilizationModelFull
import org.cloudbus.cloudsim.Vm
import org.cloudbus.cloudsim.VmAllocationPolicySimple
import org.cloudbus.cloudsim.VmSchedulerTimeShared
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple

import scala.collection.mutable.ListBuffer

object CloudSim1{
    //Logger logger = LoggerFactory.getLogger()
    def main(args: Array[String]): Unit = {

        //Loading the config file
        val configFile: config.Config = ConfigFactory.load("sim1.conf")
        val numUsers = configFile.getInt("jdbc.numUsers")
        val cal = Calendar.getInstance()


        //Initializing the cloudsim with the number of brokers as a parameter
        CloudSim.init(numUsers, cal, false)

        //Creating x amount of datacenters. They don't need to be saved or anything so I don't do that.
        for(i <- 1 to configFile.getInt("jdbc.numDatacenters")){
            Log.printLine("Creating Datacenter " + i)
            val datacenter = createDatacenter(configFile.getString("jdbc.datacentername" + i), configFile, i)

            }

        //I need to make this map mutable since I can't make x brokers dynamically without a loop and adding them one by one
        //If I wasn't using a config file, I could do with using val
        var brokers = Map[String, DatacenterBroker]()
        //In order to reduce the number of loops I use, I do a lot of the broker work as soon as I create a broker
        //This will create broker x, create the vmList and cloudletList with broker x's parameters, submit both those lists to the broker, and insert that broker to a map containing brokers with the broker name as the key
        //Later on I will use this list to print out each brokers cloudlet logs
        for(i <- 1 to configFile.getInt("jdbc.numBroker")) {
            Log.printLine("Creating Broker " + i)
            val newBroker = new DatacenterBroker(configFile.getString("jdbc.broker" + i + "name"))
            val vmList = createVMList(i, newBroker.getId, configFile, 0)
            val cloudletList = createCloudlets(i, newBroker.getId, configFile, 0)
            val vmJList = toJList(vmList)
            val cloudletJList = toJList(cloudletList)
            Log.printLine("Creating VM List for Broker " + i)
            newBroker.submitVmList(vmJList)
            Log.printLine("Creating Cloudlet List for Broker " + i)
            newBroker.submitCloudletList(cloudletJList)
            brokers += (configFile.getString("jdbc.broker" + i + "name") ->
              newBroker)
            }


        CloudSim.startSimulation()

        CloudSim.stopSimulation()


        //Similar to the cloudlet examples, this is just a fancy way of printing a table showing what happened during the sim
        //I changed it a bit so that it uses a foreach loop and made it print our the processing cost for each cloudlet
        val indent = "    "
        Log.printLine()
        Log.printLine("========== OUTPUT ==========")
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time" + indent + "Cloudlet Cost")
        brokers.foreach{
            case (_, value) =>
            val messages = value.getCloudletReceivedList
                messages.forEach { e : Cloudlet => if(e.getCloudletStatus == Cloudlet.SUCCESS)
                {
                    val resID = e.getResourceId
                    val cost = e.getActualCPUTime(resID) * e.getCostPerSec(resID)
                    Log.printLine(indent + e.getCloudletId + indent + indent + "SUCCESS" + indent + indent + e.getResourceId + indent + indent + indent + indent + e.getVmId + indent + indent + e.getActualCPUTime() + indent + indent + e.getExecStartTime + indent + indent + e.getFinishTime + indent + indent + indent + cost)
                }
                else
                {
                    Log.printLine(indent + e.getCloudletId + indent + indent + "FAILURE" + indent + indent + e.getResourceId + indent + indent + indent + indent + e.getVmId + indent + indent + e.getActualCPUTime() + indent + indent + e.getExecStartTime + indent + indent + e.getFinishTime)
                }
                }
        }
        Log.printLine("Finished")
    }

    //Both createVMList and createCloudlets are kind of self explanitory. I am just reading in input from the config file and putting them into a list.
    def createVMList(brokerNumber: Int, brokerID : Int, configFile : config.Config, offset : Int) : List[Vm] = {
        val vms = new ListBuffer[Vm]
        val mips = configFile.getInt("jdbc.vmmips")
        val size = configFile.getInt("jdbc.vmsize")
        val ram = configFile.getInt("jdbc.vmram")
        val bw = configFile.getInt("jdbc.vmbw")
        val pesNumber = configFile.getInt("jdbc.vmpesNumber")
        val vmm = configFile.getString("jdbc.vmvmm")

        //Just like before, I use a loop here and in createCloudlets because I don't know how many VM's each broker wants and I can't create them all at once dynamically
        for(j <- 1 to configFile.getInt("jdbc.broker" + brokerNumber + "numVM")){
            vms += new Vm(j + offset, brokerID, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared())
        }
        vms.toList
    }

    //Read comments above and inside createVMList
    def createCloudlets(brokerNumber: Int, brokerID : Int, configFile : config.Config, offset : Int) : List[Cloudlet] = {
        val cloudlets = new ListBuffer[Cloudlet]
        val length = configFile.getInt("jdbc.cloudletLength")
        val fileSize = configFile.getInt("jdbc.cloudletFileSize")
        val outputSize = configFile.getInt("jdbc.cloudletOutputSize")
        val pesNumber = configFile.getInt("jdbc.cloudletPesNumber")
        val utilizationModel = new UtilizationModelFull()
        for(j <- 1 to configFile.getInt("jdbc.broker" + brokerNumber + "numCloudlet")){
            val newCloudlet = new Cloudlet(j + offset, length, pesNumber, fileSize, outputSize, utilizationModel,utilizationModel,utilizationModel)
            newCloudlet.setUserId(brokerID)
            cloudlets += newCloudlet
        }
        cloudlets.toList
    }

    def createDatacenter(datacenterName : String, configFile : config.Config, datacenterNumber : Int) : Datacenter = {
        //Creating the machine and all its properties
        //First loop is for creating each processor. The second loop is creating the cores within each processor
        val mips = configFile.getInt("jdbc.datacenterMips")
        val hostList = new ListBuffer[Host]
        for(_ <- 1 to configFile.getInt("jdbc.NumPe")){
            val peList = new ListBuffer[Pe]
            for(i <- 1 to configFile.getInt("jdbc.NumCoresPerPe")){
                peList += new Pe(i, new PeProvisionerSimple(mips))
            }
            val hostId = datacenterNumber
            val ram = configFile.getInt("jdbc.datacenterRam")
            val storage = configFile.getInt("jdbc.datacenterStorage")
            val bw = configFile.getInt("jdbc.datacenterBw")
            val peJList = toJList(peList.toList)
            hostList +=
                new Host(
                    hostId,
                    new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(bw),
                    storage,
                    peJList,
                    new VmSchedulerTimeShared(peJList)
                )

        }
        //Finished Machine

        //Creating the Datacenter characteristics object that stores the datacenter's properties
        val arch = configFile.getString("jdbc.datacenterArch")
        val os = configFile.getString("jdbc.datacenterOs")
        val vmm = configFile.getString("jdbc.datacenterVmm")
        val time_zone = configFile.getInt("jdbc.datacenterTime_zone")
        val cost = configFile.getInt("jdbc.datacenterCost")
        val costPerMem = configFile.getInt("jdbc.datacenterCostPerMem")
        val costPerStorage = configFile.getInt("jdbc.datacenterCostPerStorage")
        val costPerBw = configFile.getInt("jdbc.datacenterCostPerBw")

        val storageList = new util.LinkedList[Storage]
        // we are not adding SAN
        val hostJList = toJList(hostList.toList)
        val datacenterChar = new DatacenterCharacteristics(arch, os, vmm, hostJList, time_zone, cost, costPerMem, costPerStorage, costPerBw)
        //Finished datacenter characteristics

        //Creating the datacenter
        val datacenter = new Datacenter(datacenterName, datacenterChar, new VmAllocationPolicySimple(hostJList), storageList, 0)

        datacenter
    }

    def toJList[T](l: List[T]): util.List[T] = {
        val a = new util.ArrayList[T]
        l.map(a.add(_))
        a
    }
}


