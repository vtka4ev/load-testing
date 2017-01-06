package com.alertme.zoo.shared.load_testing.simulation

import com.github.mnogu.gatling.mqtt.Predef._
import io.gatling.core.Predef._
import org.fusesource.mqtt.client.QoS
import com.alertme.zoo.shared.awsloadtesting.scrty.SecurityTools

import scala.concurrent.duration._

class MqttSimulation extends Simulation {

  /* Place for arbitrary Scala code that is to be executed before the simulation begins. */
  before {
    println("***** My simulation is about to begin! *****")
    SecurityTools.initDefaultSSLContextForTest()
  }

  /* Place for arbitrary Scala code that is to be executed after the simulation has ended. */
  after {
    println("***** My simulation has ended! ******")
  }

  val mqttConf = mqtt
    .host("ssl://ay308tk1woz4x.iot.eu-west-1.amazonaws.com:8883")
    .clientId("vtkachevSDKThing1")

  val pld = "{}"

  val scn = scenario("MQTT Test")
    // The content of mqtt.csv would be like this:
    //
    //   client,topic,payload
    //   clientId1,topic1,payload1
    //   clientId2,topic2,payload2
    //   ...
//    .feed(csv("mqtt.csv").circular)
    .exec(mqtt("request")
      // topic: the values of "topic" column in mqtt.csv
      // payload: the values of "payload" column in mqtt.csv
      // QoS: AT_LEAST_ONCE
      // retain: false
//      .publish("${topic}", "${payload}", QoS.AT_LEAST_ONCE, retain = false))
    .publish("$aws/things/vtkachevSDKThing1/shadow/update", pld, QoS.AT_LEAST_ONCE, retain = false))

  setUp(
    scn
      .inject(constantUsersPerSec(10) during(90 seconds)))
    .protocols(mqttConf)
}