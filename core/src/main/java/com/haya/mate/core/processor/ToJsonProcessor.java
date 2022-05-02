package com.haya.mate.core.processor;

//import com.google.auto.service.AutoService;

import com.haya.mate.core.annotation.ToString;
import com.haya.mate.core.template.ToStringTemplateGenerator;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("com.haya.mate.core.annotation.ToString")
public class ToJsonProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            processWrap(annotations, roundEnv);
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void processWrap(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        JavacTrees trees = JavacTrees.instance(processingEnv);
        JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();
        TreeMaker treeMaker = TreeMaker.instance(context);
        Names names = Names.instance(context);
        roundEnv.getElementsAnnotatedWith(ToString.class)
                .stream()
                .map(item -> {
                    TreePath path = trees.getPath(item);
                    ToString annotation = item.getAnnotation(ToString.class);
                    return List.of(annotation, elementUtils.getTree(item), path);
                })
                .forEach(list -> {
                    ToString annotation = (ToString) list.get(0);
                    JCTree classDef = (JCTree) list.get(1);
                    TreePath path = (TreePath) list.get(2);

                    classDef.accept(new JCTree.Visitor() {
                        @Override
                        public void visitClassDef(JCTree.JCClassDecl classDecl) {
                            var methodTemplate = ToStringTemplateGenerator.getMethodTemplate(
                                    path, elementUtils, annotation, treeMaker, names
                            );
                            classDecl.defs = classDecl.defs.append(methodTemplate);
                        }
                    });
                });
    }


}
