import com.alertme.zoo.shared.awsloadtesting.scrty.SecurityTools;
import com.github.mnogu.gatling.mqtt.protocol.MqttProtocol;
import com.github.mnogu.gatling.mqtt.protocol.MqttProtocolBuilder;
import io.gatling.commons.validation.Validation;
import io.gatling.core.Predef;
import io.gatling.core.scenario.Simulation;
import io.gatling.core.structure.ScenarioBuilder;
import org.fusesource.mqtt.client.QoS;
import scala.Function0;
import scala.Function1;
import scala.runtime.BoxedUnit;

public class MqttSimulationJava extends Simulation {

    static Validation<String> buildValidation(final String str) {
        return new Validation<String>() {
            @Override
            public <A> Validation<A> flatMap(final Function1<String, Validation<A>> f) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Validation<String> filter(final Function1<String, Object> p) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <A> Validation<A> recover(final Function0<A> v) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String get() {
                return str;
            }

            @Override
            public Validation<String> withFilter(final Function1<String, Object> p) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onFailure(final Function1<String, Object> f) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onSuccess(final Function1<String, Object> f) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Validation<String> mapError(final Function1<String, String> f) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void foreach(final Function1<String, Object> f) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <A> Validation<A> map(final Function1<String, A> f) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public MqttSimulationJava() {
        final MqttProtocolBuilder mqtt = com.github.mnogu.gatling.mqtt.Predef.mqtt(io.gatling.core.Predef.configuration());
        final MqttProtocol mqttProtocol = mqtt.mqttProtocol();
        final MqttProtocol mqttConf = mqttProtocol
                .host((session) -> buildValidation("tls://a3gemn1nf8j60e.iot.eu-west-1.amazonaws.com:8883"))
                .clientId((session) -> buildValidation("ALESSIO'S TEST"));

        final String pld = "{\n" +
                "    \"state\": {\n" +
                "        \"desired\" : {\n" +
                "            \"color\" : { \"b\" : 10 },\n" +
                "            \"engine\" : \"OFF\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        final ScenarioBuilder scn = Predef.scenario("MQTT Test")
                .exec(com.github.mnogu.gatling.mqtt.Predef.mqtt((session) -> buildValidation("request"))
                        .publish(session -> buildValidation("$aws/things/alessio-test/shadow/update"),
                                session -> buildValidation(pld),
                                QoS.AT_LEAST_ONCE, false)).exitHereIfFailed();

//        Predef.dur
//        setUp(
//                scn
//                        .inject(null)
////                        .inject(constantUsersPerSec(10)during(90seconds)))
//                        .protocols(mqttConf)
    }

    @Override
    public void before(final Function0<BoxedUnit> step) {
        System.out.println("***** My simulation is about to begin! *****");
        SecurityTools.initDefaultSSLContextForTest();
    }

    @Override
    public void after(final Function0<BoxedUnit> step) {
        System.out.println("***** My simulation has ended! ******");
    }

//    static final class MyValidation<String> implements Validation {
//        private final String str;
//
//        public MyValidation(final String str) {
//            this.str = str;
//        }
//
//        @Override
//        public String get() {
//            return str;
//        }
//
//        @Override
//        public Validation map(final Function1 f) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public Validation flatMap(final Function1 f) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public Validation<java.lang.String> mapError(final Function1<java.lang.String, java.lang.String> f) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public void foreach(final Function1 f) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public Validation withFilter(final Function1 p) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public Validation filter(final Function1 p) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public void onSuccess(final Function1 f) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public void onFailure(final Function1<java.lang.String, Object> f) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public Validation recover(final Function0 v) {
//            throw new UnsupportedOperationException();
//        }
//    }

//    @Override
//    public SetUp setUp(final Seq<PopulationBuilder> populationBuilders) {
//        return super.setUp(populationBuilders);
//    }
//
//    @Override
//    public SetUp setUp(final List<PopulationBuilder> populationBuilders) {
//        return super.setUp(populationBuilders);
//    }
//
//    @Override
//    public void executeBefore() {
//        super.executeBefore();
//    }
//
//    @Override
//    public void executeAfter() {
//        super.executeAfter();
//    }
}
