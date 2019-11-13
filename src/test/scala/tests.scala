
import java.util.Calendar

import CloudSim1.{createCloudlets, createDatacenter, createVMList, toJList}
import com.typesafe.config
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.DatacenterBroker
import org.cloudbus.cloudsim.core.CloudSim
import org.scalatest._

class tests extends FunSuite with Matchers {

  test("CloudSim1.createDatacenter"){
    val configFile: config.Config = ConfigFactory.load("sim1.conf")
    val numUsers = configFile.getInt("jdbc.numUsers")
    val cal = Calendar.getInstance()

    //Initializing the cloudsim with the number of brokers as a parameter
    CloudSim.init(numUsers, cal, false)

    //Creating x amount of datacenters. They don't need to be saved or anything so I don't do that.
    createDatacenter(configFile.getString("jdbc.datacentername1"), configFile, 1) should not be null
  }

  test("CloudSim.createVMList"){
    val configFile: config.Config = ConfigFactory.load("sim1.conf")
    createVMList(1, 1, configFile, 0) should not be null
  }

  test("CloudSim.createCloudletList"){
    val configFile: config.Config = ConfigFactory.load("sim1.conf")
    createCloudlets(1, 1, configFile, 0) should not be null
  }

  test("CloudSim.createVMList size"){
    val configFile: config.Config = ConfigFactory.load("sim1.conf")
    val list = createVMList(1, 1, configFile, 0)
    list.size shouldBe configFile.getInt("jdbc.broker1numVM")
  }

  test("CloudSim.createCloudletList size"){
    val configFile: config.Config = ConfigFactory.load("sim1.conf")
    val list = createCloudlets(1, 1, configFile, 0)
    list.size shouldBe configFile.getInt("jdbc.broker1numCloudlet")
  }

  test("Broker creation"){
    val configFile: config.Config = ConfigFactory.load("sim1.conf")
    var brokers = Map[String, DatacenterBroker]()
    for(i <- 1 to configFile.getInt("jdbc.numBroker")) {
      val newBroker = new DatacenterBroker(configFile.getString("jdbc.broker" + i + "name"))
      val vmList = createVMList(i, newBroker.getId, configFile, 0)
      val cloudletList = createCloudlets(i, newBroker.getId, configFile, 0)
      val vmJList = toJList(vmList)
      val cloudletJList = toJList(cloudletList)
      newBroker.submitVmList(vmJList)
      newBroker.submitCloudletList(cloudletJList)
      brokers += (configFile.getString("jdbc.broker" + i + "name") ->
        newBroker)
    }

    brokers.size shouldBe configFile.getInt("jdbc.numBroker")
  }

}
