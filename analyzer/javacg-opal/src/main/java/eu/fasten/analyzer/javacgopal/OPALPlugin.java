/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.fasten.analyzer.javacgopal;

import eu.fasten.core.data.RevisionCallGraph;
import eu.fasten.core.plugins.KafkaConsumer;
import eu.fasten.core.plugins.KafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OPALPlugin implements KafkaConsumer<String>, KafkaProducer {

    private static Logger logger = LoggerFactory.getLogger(OPALMethodAnalyzer.class);

    List<RevisionCallGraph> revisionCallGraphs = new ArrayList<>();
    final String CONSUME_TOPIC = "maven.packages";
    final String PRODUCE_TOPIC = "opal_callgraphs";

    @Override
    public String consumerTopic() {
        return CONSUME_TOPIC;
    }

    @Override
    public void consume(ConsumerRecords<String, String> records) {

        for (ConsumerRecord<String, String> kafkaRecord : records) {

            try {
            JSONObject kafkaConsumedJson = new JSONObject(kafkaRecord.value());

            MavenCoordinate mavenCoordinate = new MavenCoordinate(kafkaConsumedJson.get("groupId").toString(),
                kafkaConsumedJson.get("artifactId").toString(),
                kafkaConsumedJson.get("version").toString());

            revisionCallGraphs.add(
                PartialCallGraph.createRevisionCallGraph("mvn",
                    mavenCoordinate,
                    Long.parseLong(kafkaConsumedJson.get("date").toString()),
                    CallGraphGenerator.generatePartialCallGraph(
                        MavenResolver.downloadJar(mavenCoordinate.getCoordinate()).orElseThrow(RuntimeException::new)
                    )
                )
            );
            }catch (Exception e){
                //TODO
            }
        }
    }

    @Override
    public String producerTopic() {
        return PRODUCE_TOPIC;
    }

    @Override
    public void setKafkaProducer(org.apache.kafka.clients.producer.KafkaProducer<Object, String> producer) {
        for (RevisionCallGraph revisionCallGraph : revisionCallGraphs) {
            ProducerRecord<Object, String> record = new ProducerRecord<>(revisionCallGraph.uri.toString(), revisionCallGraph.toJSON().toString());
            try {
                producer.send(record, ((recordMetadata, e) -> {
                    if (e != null) {
                        logger.error("Error while producing", e);
                        return;
                    }
                    logger.debug("Could not produce artifact {}: ", revisionCallGraph.uri.toString());
                })).get();
            }catch (Exception e){
                //TODO
            }
        }


    }

    @Override
    public String name() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public String description() {
        return "This plugin is a call graph generator. " +
            "It implements a consume method that generates call graphs using OPAL call graph generator for provided Kafka consumed maven coordinates." +
            "It also implements a produce method which produces generated call graphs to a Kafka topic.";
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}