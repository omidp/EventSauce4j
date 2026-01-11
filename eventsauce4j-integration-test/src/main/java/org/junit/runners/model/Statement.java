package org.junit.runners.model;

/**
 * A dummy interface resembling a Junit 4 interface with the same name.
 * to prevent adding Junit 4 dependency transitively to our projects.
 * currently TestContainers needs this class to be on the classpath for:
 * <ul>
 *     <li>/org/testcontainers/containers/Network.java</li>
 *     <li>org/testcontainers/containers/FailureDetectingExternalResource.java</li>
 * </ul>
 * Since we do not use Junit 4 hooks for starting and destroying containers the implementations can be empty
 * @deprecated - can be removed when TestContainers removes JUnit 4 dependencies
 */
@Deprecated
public interface Statement {

}