// Generated by delombok at Sun Feb 26 12:31:38 KST 2017
package scouter.bytebuddy.implementation.attribute;

import scouter.bytebuddy.description.annotation.AnnotationDescription;
import scouter.bytebuddy.description.annotation.AnnotationList;
import scouter.bytebuddy.description.type.TypeDescription;
import scouter.bytebuddy.description.type.TypeList;
import scouter.bytebuddy.jar.asm.ClassVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An appender that writes attributes or annotations to a given ASM {@link ClassVisitor}.
 */
public interface TypeAttributeAppender {
    /**
     * Applies this type attribute appender.
     *
     * @param classVisitor          The class visitor to which the annotations of this visitor should be written to.
     * @param instrumentedType      A description of the instrumented type that is target of the ongoing instrumentation.
     * @param annotationValueFilter The annotation value filter to apply when writing annotations.
     */
    void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter);


    /**
     * A type attribute appender that does not append any attributes.
     */
    enum NoOp implements TypeAttributeAppender {
        /**
         * The singleton instance.
         */
        INSTANCE;

        @Override
        public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
            /* do nothing */
        }
    }


    /**
     * An attribute appender that writes all annotations that are found on a given target type to the
     * instrumented type this type attribute appender is applied onto. The visibility for the annotation
     * will be inferred from the annotations' {@link java.lang.annotation.RetentionPolicy}.
     */
    enum ForInstrumentedType implements TypeAttributeAppender {
        /**
         * The singleton instance.
         */
        INSTANCE;

        @Override
        public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender annotationAppender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnType(classVisitor));
            annotationAppender = AnnotationAppender.ForTypeAnnotations.ofTypeVariable(annotationAppender, annotationValueFilter, AnnotationAppender.ForTypeAnnotations.VARIABLE_ON_TYPE, instrumentedType.getTypeVariables());
            TypeDescription.Generic superClass = instrumentedType.getSuperClass();
            if (superClass != null) {
                annotationAppender = superClass.accept(AnnotationAppender.ForTypeAnnotations.ofSuperClass(annotationAppender, annotationValueFilter));
            }
            int interfaceIndex = 0;
            for (TypeDescription.Generic interfaceType : instrumentedType.getInterfaces()) {
                annotationAppender = interfaceType.accept(AnnotationAppender.ForTypeAnnotations.ofInterfaceType(annotationAppender, annotationValueFilter, interfaceIndex++));
            }
            for (AnnotationDescription annotation : instrumentedType.getDeclaredAnnotations()) {
                annotationAppender = annotationAppender.append(annotation, annotationValueFilter);
            }
        }


        /**
         * A type attribute appender that writes all annotations of the instrumented but excludes annotations up to
         * a given index.
         */
        public static class Differentiating implements TypeAttributeAppender {
            /**
             * The index of the first annotations that should be directly written onto the type.
             */
            private final int annotationIndex;
            /**
             * The index of the first type variable for which type annotations should be directly written onto the type.
             */
            private final int typeVariableIndex;
            /**
             * The index of the first interface type for which type annotations should be directly written onto the type.
             */
            private final int interfaceTypeIndex;

            /**
             * Creates a new differentiating type attribute appender.
             *
             * @param typeDescription The type for which to resolve all exclusion indices.
             */
            public Differentiating(TypeDescription typeDescription) {
                this(typeDescription.getDeclaredAnnotations().size(), typeDescription.getTypeVariables().size(), typeDescription.getInterfaces().size());
            }

            /**
             * Creates a new differentiating type attribute appender.
             *
             * @param annotationIndex    The index of the first annotations that should be directly written onto the type.
             * @param typeVariableIndex  The index of the first interface type for which type annotations should be directly written onto the type.
             * @param interfaceTypeIndex The index of the first interface type for which type annotations should be directly written onto the type.
             */
            protected Differentiating(int annotationIndex, int typeVariableIndex, int interfaceTypeIndex) {
                this.annotationIndex = annotationIndex;
                this.typeVariableIndex = typeVariableIndex;
                this.interfaceTypeIndex = interfaceTypeIndex;
            }

            @Override
            public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
                AnnotationAppender annotationAppender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnType(classVisitor));
                AnnotationAppender.ForTypeAnnotations.ofTypeVariable(annotationAppender, annotationValueFilter, AnnotationAppender.ForTypeAnnotations.VARIABLE_ON_TYPE, typeVariableIndex, instrumentedType.getTypeVariables());
                TypeList.Generic interfaceTypes = instrumentedType.getInterfaces();
                int interfaceTypeIndex = this.interfaceTypeIndex;
                for (TypeDescription.Generic interfaceType : interfaceTypes.subList(this.interfaceTypeIndex, interfaceTypes.size())) {
                    annotationAppender = interfaceType.accept(AnnotationAppender.ForTypeAnnotations.ofInterfaceType(annotationAppender, annotationValueFilter, interfaceTypeIndex++));
                }
                AnnotationList declaredAnnotations = instrumentedType.getDeclaredAnnotations();
                for (AnnotationDescription annotationDescription : declaredAnnotations.subList(annotationIndex, declaredAnnotations.size())) {
                    annotationAppender = annotationAppender.append(annotationDescription, annotationValueFilter);
                }
            }

            @java.lang.Override
            @java.lang.SuppressWarnings("all")
            @javax.annotation.Generated("lombok")
            public boolean equals(final java.lang.Object o) {
                if (o == this) return true;
                if (!(o instanceof TypeAttributeAppender.ForInstrumentedType.Differentiating)) return false;
                final TypeAttributeAppender.ForInstrumentedType.Differentiating other = (TypeAttributeAppender.ForInstrumentedType.Differentiating) o;
                if (!other.canEqual((java.lang.Object) this)) return false;
                if (this.annotationIndex != other.annotationIndex) return false;
                if (this.typeVariableIndex != other.typeVariableIndex) return false;
                if (this.interfaceTypeIndex != other.interfaceTypeIndex) return false;
                return true;
            }

            @java.lang.SuppressWarnings("all")
            @javax.annotation.Generated("lombok")
            protected boolean canEqual(final java.lang.Object other) {
                return other instanceof TypeAttributeAppender.ForInstrumentedType.Differentiating;
            }

            @java.lang.Override
            @java.lang.SuppressWarnings("all")
            @javax.annotation.Generated("lombok")
            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                result = result * PRIME + this.annotationIndex;
                result = result * PRIME + this.typeVariableIndex;
                result = result * PRIME + this.interfaceTypeIndex;
                return result;
            }
        }
    }


    /**
     * An attribute appender that appends a single annotation to a given type. The visibility for the annotation
     * will be inferred from the annotation's {@link java.lang.annotation.RetentionPolicy}.
     */
    class Explicit implements TypeAttributeAppender {
        /**
         * The annotations to write to the given type.
         */
        private final List<? extends AnnotationDescription> annotations;

        /**
         * Creates a new annotation attribute appender for explicit annotation values.
         *
         * @param annotations The annotations to write to the given type.
         */
        public Explicit(List<? extends AnnotationDescription> annotations) {
            this.annotations = annotations;
        }

        @Override
        public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
            AnnotationAppender appender = new AnnotationAppender.Default(new AnnotationAppender.Target.OnType(classVisitor));
            for (AnnotationDescription annotation : annotations) {
                appender = appender.append(annotation, annotationValueFilter);
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof TypeAttributeAppender.Explicit)) return false;
            final TypeAttributeAppender.Explicit other = (TypeAttributeAppender.Explicit) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$annotations = this.annotations;
            final java.lang.Object other$annotations = other.annotations;
            if (this$annotations == null ? other$annotations != null : !this$annotations.equals(other$annotations)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof TypeAttributeAppender.Explicit;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $annotations = this.annotations;
            result = result * PRIME + ($annotations == null ? 43 : $annotations.hashCode());
            return result;
        }
    }


    /**
     * A compound type attribute appender that concatenates a number of other attribute appenders.
     */
    class Compound implements TypeAttributeAppender {
        /**
         * The type attribute appenders this compound appender represents in their application order.
         */
        private final List<TypeAttributeAppender> typeAttributeAppenders;

        /**
         * Creates a new compound attribute appender.
         *
         * @param typeAttributeAppender The type attribute appenders to concatenate in the order of their application.
         */
        public Compound(TypeAttributeAppender... typeAttributeAppender) {
            this(Arrays.asList(typeAttributeAppender));
        }

        /**
         * Creates a new compound attribute appender.
         *
         * @param typeAttributeAppenders The type attribute appenders to concatenate in the order of their application.
         */
        public Compound(List<? extends TypeAttributeAppender> typeAttributeAppenders) {
            this.typeAttributeAppenders = new ArrayList<TypeAttributeAppender>();
            for (TypeAttributeAppender typeAttributeAppender : typeAttributeAppenders) {
                if (typeAttributeAppender instanceof Compound) {
                    this.typeAttributeAppenders.addAll(((Compound) typeAttributeAppender).typeAttributeAppenders);
                } else if (!(typeAttributeAppender instanceof NoOp)) {
                    this.typeAttributeAppenders.add(typeAttributeAppender);
                }
            }
        }

        @Override
        public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
            for (TypeAttributeAppender typeAttributeAppender : typeAttributeAppenders) {
                typeAttributeAppender.apply(classVisitor, instrumentedType, annotationValueFilter);
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof TypeAttributeAppender.Compound)) return false;
            final TypeAttributeAppender.Compound other = (TypeAttributeAppender.Compound) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$typeAttributeAppenders = this.typeAttributeAppenders;
            final java.lang.Object other$typeAttributeAppenders = other.typeAttributeAppenders;
            if (this$typeAttributeAppenders == null ? other$typeAttributeAppenders != null : !this$typeAttributeAppenders.equals(other$typeAttributeAppenders)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof TypeAttributeAppender.Compound;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @javax.annotation.Generated("lombok")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $typeAttributeAppenders = this.typeAttributeAppenders;
            result = result * PRIME + ($typeAttributeAppenders == null ? 43 : $typeAttributeAppenders.hashCode());
            return result;
        }
    }
}
