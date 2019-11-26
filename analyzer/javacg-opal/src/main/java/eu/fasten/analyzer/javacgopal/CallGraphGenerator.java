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

import org.opalj.ai.analyses.cg.CallGraphFactory;
import org.opalj.ai.analyses.cg.ComputedCallGraph;
import org.opalj.ai.analyses.cg.CHACallGraphAlgorithmConfiguration;

import org.opalj.br.Method;
import org.opalj.br.analyses.Project;
import org.opalj.collection.immutable.ConstArray;
import scala.collection.Iterable;
import scala.collection.JavaConversions;
import scala.collection.Map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * A wrapper class for OPAL call graph generator.
 */
final public class CallGraphGenerator {

    /**
     * Loads a given file, generates call graph and change the format of calls to (source -> target).
     * @param artifactFile Java file that can be a jar or a folder containing jars.
     * @return A partial graph including ResolvedCalls, UnresolvedCalls and CHA.
     */
    static PartialCallGraph generatePartialCallGraph(File artifactFile) {

        Project artifactInOpalFormat = Project.apply(artifactFile);

        ComputedCallGraph callGraphInOpalFormat = CallGraphFactory.create(artifactInOpalFormat,
            JavaToScalaConverter.asScalaFunction0(findEntryPoints(artifactInOpalFormat.allMethodsWithBody())),
            new CHACallGraphAlgorithmConfiguration(artifactInOpalFormat, true));

//        ComputedCallGraph callGraphInOpalFormat = (ComputedCallGraph) AnalysisModeConfigFactory.resetAnalysisMode(artifactInOpalFormat, AnalysisModes.OPA(),false).get(CHACallGraphKey$.MODULE$);

        return ToPartialGraph(callGraphInOpalFormat);

    }

    /**
     * Given a call graph in OPAL format returns a call graph in eu.fasten.analyzer.javacgopal.PartialCallGraph format.
     * @param callGraphInOpalFormat Is an object of OPAL ComputedCallGraph.
     * @return eu.fasten.analyzer.javacgopal.PartialCallGraph includes all the calls(as java List) and ClassHierarchy.
     */
     static PartialCallGraph ToPartialGraph(ComputedCallGraph callGraphInOpalFormat) {

        PartialCallGraph partialCallGraph = new PartialCallGraph();

        callGraphInOpalFormat.callGraph().foreachCallingMethod(JavaToScalaConverter.asScalaFunction2(setResolvedCalls(partialCallGraph.getResolvedCalls())));

        partialCallGraph.setUnresolvedCalls(new ArrayList<>(JavaConversions.asJavaCollection(callGraphInOpalFormat.unresolvedMethodCalls().toList())));

        partialCallGraph.setClassHierarchy(callGraphInOpalFormat.callGraph().project().classHierarchy());

        return partialCallGraph;
    }

    /**
     * Adds resolved calls to its parameter.
     * @param resolvedCallsList An empty ArrayList to get the resolved calls in java format.
     * @return eu.fasten.analyzer.javacgopal.ScalaFunction2 As a fake scala function to be passed to the scala.
     */
     static ScalaFunction2 setResolvedCalls(List<ResolvedCall> resolvedCallsList) {
        return (Method callerMethod, Map<Object, Iterable<Method>> calleeMethodsObject) -> {
            Collection<Iterable<Method>> calleeMethodsCollection =
                JavaConversions.asJavaCollection(calleeMethodsObject.valuesIterator().toList());

            List<Method> calleeMethodsList = new ArrayList<>();
            for (Iterable<Method> i : calleeMethodsCollection) {
                for (Method j : JavaConversions.asJavaIterable(i)) {
                    calleeMethodsList.add(j);
                }
            }
            return resolvedCallsList.add(new ResolvedCall(callerMethod, calleeMethodsList));
        };
    }

    /**
     * Computes the entrypoints as a pre step of call graph generation.
     * @param allMethods Is all of the methods in an OPAL-loaded project.
     * @return An iterable of entrypoints to be consumed by scala-written OPAL.
     */
     static Iterable<Method> findEntryPoints(ConstArray allMethods) {

        return (Iterable<Method>) allMethods.filter(JavaToScalaConverter.asScalaFunction1((Object method) -> (!((Method) method).isAbstract()) && !((Method) method).isPrivate()));

    }

}