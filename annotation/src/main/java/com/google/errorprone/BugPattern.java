/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.Comparator;

/**
 * Describes a bug pattern detected by error-prone.  Used to generate compiler error messages,
 * for @SuppressWarnings, and to generate the documentation that we host on our web site.
 *
 * @author eaftan@google.com (Eddie Aftandilian)
 */
@Retention(RUNTIME)
public @interface BugPattern {
  /**
   * A unique identifier for this bug, used for @SuppressWarnings and in the compiler error
   * message.
   */
  String name();

  /**
   * Alternate identifiers for this bug, which may also be used in @SuppressWarnings.
   */
  String[] altNames() default {};

  /**
   * The type of link to generate in the compiler error message.
   */
  LinkType linkType() default LinkType.AUTOGENERATED;

  /**
   * The link URL to use if linkType() is LinkType.CUSTOM.
   */
  String link() default "";

  public enum LinkType {
    /**
     * Link to autogenerated documentation, hosted on the error-prone web site.
     */
    AUTOGENERATED,
    /**
     * Custom string.
     */
    CUSTOM,
    /**
     * No link should be displayed.
     */
    NONE
  }

  /**
   * The class of bug this bug checker detects.
   */
  Category category();

  public enum Category {
    /**
     * General Java or JDK errors.
     */
    JDK,
    /**
     * Errors specific to Google Guava.
     */
    GUAVA,
    /**
     * Errors specific to Google Guice.
     */
    GUICE,
    /**
     * Errors specific to Dagger.
     */
    DAGGER,
    /**
     * Errors specific to JUnit.
     */
    JUNIT,
    /**
     * One-off matchers that are not general errors.
     */
    ONE_OFF,
    /**
     *  JSR-330 errors not specific to Guice.
     */
    INJECT,
    /**
     * Errors specific to Mockito.
     */
    MOCKITO,
    /**
     * Errors specific to JMock
     */
    JMOCK;
  }

  /**
   * A short summary of the problem that this checker detects.  Used for the default compiler error
   * message and for the short description in the generated docs.  Should not end with a period,
   * to match javac warning/error style.
   *
   * <p>Markdown syntax is not allowed for this element.
   */
  String summary();

  /**
   * A longer explanation of the problem that this checker detects.  Used as the main content
   * in the generated documentation for this checker.
   *
   * <p>Markdown syntax is allowed for this element.
   */
  String explanation() default "";

  SeverityLevel severity();

  public enum SeverityLevel {
    ERROR(true),
    WARNING(true),
    SUGGESTION(true),
    /**
     * Should not be used for general code.
     */
    NOT_A_PROBLEM(false);

    private final boolean enabled;

    SeverityLevel(boolean enabled) {
      this.enabled = enabled;
    }

    public boolean enabled() {
      return enabled;
    }
  }

  MaturityLevel maturity();

  public enum MaturityLevel {
    MATURE("On by default"),
    EXPERIMENTAL("Experimental");

    final String description;
    MaturityLevel(String description) {
      this.description = description;
    }
  }

  /**
   * Whether this checker should be suppressible, and if so, by what means.
   */
  Suppressibility suppressibility() default Suppressibility.SUPPRESS_WARNINGS;

  public enum Suppressibility {
    /**
     * Can be suppressed using the standard {@code SuppressWarnings("foo")} mechanism. This
     * setting should be used unless there is a good reason otherwise, e.g. security.
     */
    SUPPRESS_WARNINGS(true),
    /**
     * Can be suppressed with a custom annotation on a parent AST node.
     */
    CUSTOM_ANNOTATION(false),
    /**
     * Cannot be suppressed.
     */
    UNSUPPRESSIBLE(false);

    private final boolean disableable;

    Suppressibility(boolean disableable) {
      this.disableable = disableable;
    }

    public boolean disableable() {
      return disableable;
    }
  }

  /**
   * A custom suppression annotation type to use if suppressibility is
   * Suppressibility.CUSTOM_ANNOTATION.
   */
  Class<? extends Annotation> customSuppressionAnnotation() default NoCustomSuppression.class;

  /**
   * A dummy annotation to use when there is no custom suppression annotation.  The JLS does not
   * allow null as a legal element value, so we have to use a sentinel value.
   */
  public @interface NoCustomSuppression {}

  public class Instance {
    public String name;
    public String summary;
    public String altNames;
    public Category category;
    public MaturityLevel maturity;
    public SeverityLevel severity;
    public Suppressibility suppressibility;
    public String customSuppressionAnnotation;

    public static final Comparator<Instance> BY_SEVERITY = new Comparator<Instance>() {
      @Override
      public int compare(Instance o1, Instance o2) {
        return o1.severity.compareTo(o2.severity);
      }
    };

    public static final Comparator<Instance> BY_NAME = new Comparator<Instance>() {
      @Override
      public int compare(Instance o1, Instance o2) {
        return o1.name.compareTo(o2.name);
      }
    };
  }
}
