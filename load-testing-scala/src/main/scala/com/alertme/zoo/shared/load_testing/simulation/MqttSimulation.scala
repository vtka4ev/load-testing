package com.alertme.zoo.shared.load_testing.simulation

import com.github.mnogu.gatling.mqtt.Predef._
import io.gatling.core.Predef._
import org.fusesource.mqtt.client.QoS
import com.alertme.zoo.shared.awsloadtesting.scrty.SecurityTools

import scala.concurrent.duration._

class MqttSimulation extends Simulation {

  before {
    println("***** My simulation is about to begin! *****")
    SecurityTools.initDefaultSSLContextForTest()
  }

  after {
    println("***** My simulation has ended! ******")
  }

  val mqttConf = mqtt
    .host("ssl://ay308tk1woz4x.iot.eu-west-1.amazonaws.com:8883")
    .clientId("vtkachevSDKThing1")

  val pld = "{}"

  val scn = scenario("MQTT Test")
    .exec(mqtt("request")
    .publish("$aws/things/vtkachevSDKThing1/shadow/update", pld, QoS.AT_LEAST_ONCE, retain = false))

  setUp(
    scn
      .inject(constantUsersPerSec(10) during(90 seconds)))
    .protocols(mqttConf)
}