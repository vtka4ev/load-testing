package com.alertme.zoo.shared.load_testing.simulation

import com.alertme.zoo.shared.awsloadtesting.scrty.SecurityTools
import com.github.mnogu.gatling.mqtt.Predef._
import io.gatling.core.Predef._
import org.fusesource.mqtt.client.QoS

import scala.concurrent.duration._

class MqttSimulation extends Simulation {

  // arn:aws:iot:eu-west-1:886358885274:thing/alessio-test

  before {
    println("***** My simulation is about to begin! *****")
    SecurityTools.initDefaultSSLContextForTest()
  }

  after {
    println("***** My simulation has ended! ******")
  }

  val mqttConf = mqtt
    .host("tls://a3gemn1nf8j60e.iot.eu-west-1.amazonaws.com:8883")
    .clientId("alessio-test")

  val pld =
    """{
      |    "state": {
      |        "desired" : {
      |            "color" : { "b" : 10 },
      |            "engine" : "OFF"
      |        }
      |    }
      |}"""
  val scn = scenario("MQTT Test")
    .exec(mqtt("request")
      .publish("$aws/things/alessio-test/shadow/update", pld, QoS.AT_LEAST_ONCE, retain = false))
    .exitHereIfFailed

  setUp(
    scn
      .inject(constantUsersPerSec(10) during (90 seconds)))
    .protocols(mqttConf)
}