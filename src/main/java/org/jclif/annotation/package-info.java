/**
 * Provides the classes necessary to annotate POJO classes as command line input handler.
 * <p>
 * The JCLIF framework uses these annotations to check if a given class can handle command line
 * inputs. POJO classes are annotated using the <code>Command</code> and <code>Handler</code>
 * annotation to mark a class as able to handler command line inputs if it matches the specified
 * identifier. Each of these Command annotated classes may also have fields which are annotated with
 * Parameer or Option annotation.
 *
 * @since 1.0
 */
package org.jclif.annotation;