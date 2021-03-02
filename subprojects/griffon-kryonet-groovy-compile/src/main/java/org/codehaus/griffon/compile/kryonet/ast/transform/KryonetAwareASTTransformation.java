/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2021 The author and/or original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing pekryonetssions and
 * limitations under the License.
 */
package org.codehaus.griffon.compile.kryonet.ast.transform;

import griffon.annotations.core.Nonnull;
import griffon.plugins.kryonet.KryonetHandler;
import griffon.transform.kryonet.KryonetAware;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.ast.transform.AbstractASTTransformation;
import org.codehaus.griffon.compile.kryonet.KryonetAwareConstants;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;

/**
 * Handles generation of code for the {@code @KryonetAware} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(KryonetAware.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class KryonetAwareASTTransformation extends AbstractASTTransformation implements KryonetAwareConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(KryonetAwareASTTransformation.class);
    private static final ClassNode Kryonet_HANDLER_CNODE = makeClassSafe(KryonetHandler.class);
    private static final ClassNode Kryonet_AWARE_CNODE = makeClassSafe(KryonetAware.class);

    /**
     * Convenience method to see if an annotated node is {@code @KryonetAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasKryonetAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (Kryonet_AWARE_CNODE.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes  the ast nodes
     * @param source the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);
        addKryonetHandlerIfNeeded(source, (AnnotationNode) nodes[0], (ClassNode) nodes[1]);
    }

    public static void addKryonetHandlerIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode) {
        if (needsDelegate(classNode, source, METHODS, KryonetAware.class.getSimpleName(), Kryonet_HANDLER_TYPE)) {
            LOG.debug("Injecting {} into {}", Kryonet_HANDLER_TYPE, classNode.getName());
            apply(classNode);
        }
    }

    /**
     * Adds the necessary field and methods to support dataSource handling.
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(@Nonnull ClassNode declaringClass) {
        injectInterface(declaringClass, Kryonet_HANDLER_CNODE);
        Expression kryonetHandler = injectedField(declaringClass, Kryonet_HANDLER_CNODE, Kryonet__HANDLER_FIELD_NAME);
        addDelegateMethods(declaringClass, Kryonet_HANDLER_CNODE, kryonetHandler);
    }
}