package com.maxifier.cliche.guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.annotation.Annotation;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class ShellModule extends AbstractModule {


    @Override
    protected void configure() {
        bindListener(AnnotatedClassMatcher.with(CommandHandler.class),
                new TypeListener() {
                    @Override
                    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {

                    }
                });
    }

    static class AnnotatedClassMatcher extends AbstractMatcher<TypeLiteral<?>> {

        public static AnnotatedClassMatcher with(Class<? extends Annotation> annotationClass) {
            return new AnnotatedClassMatcher(annotationClass);
        }

        AnnotatedClassMatcher(Class<? extends Annotation> annotations) {
            this.annotation = annotations;
        }

        Class<? extends Annotation> annotation;

        @Override
        public boolean matches(TypeLiteral<?> typeLiteral) {
            return typeLiteral.getRawType().isAnnotationPresent(annotation);
        }
    }
}
