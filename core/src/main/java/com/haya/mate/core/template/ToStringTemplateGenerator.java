package com.haya.mate.core.template;

import com.haya.mate.core.annotation.ToString;
import com.haya.mate.core.annotation.ToStringEnum;
import com.haya.mate.core.service.JavaMateJsonService;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.Objects;
import java.util.Set;

public class ToStringTemplateGenerator {

    public static JCTree.JCMethodDecl getMethodTemplate(
            TreePath trees,
            JavacElements elementUtils,
            ToString annotationInfo,
            TreeMaker treeMaker,
            Names names
    ) {
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees.getCompilationUnit();
        ListBuffer<JCTree> imports = new ListBuffer<JCTree>();
        imports.append(compilationUnit.defs.get(0));

        for (var item : Set.of(JavaMateJsonService.class)) {
            String name = item.getPackage().getName();
            String simpleName = item.getSimpleName();
            JCTree.JCIdent packageIdent = treeMaker.Ident(names.fromString(name));
            JCTree.JCFieldAccess fieldAccess = treeMaker.Select(packageIdent,
                    names.fromString(simpleName));
            imports.append(treeMaker.Import(fieldAccess, false));
        }
        for (int i = 1; i < compilationUnit.defs.size(); i++) {
            imports.append(compilationUnit.defs.get(i));
        }
        compilationUnit.defs = imports.toList();
        if (Objects.equals(annotationInfo.type(), ToStringEnum.JSON)) {
            return getToJsonMethodTemplate(elementUtils, treeMaker, names);
        }
        throw new RuntimeException("!!!!!!!type no value!!!!!!!!");
    }

    public static JCTree.JCMethodDecl getToJsonMethodTemplate(JavacElements elementUtils, TreeMaker treeMaker, Names names) {
        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);
//        Name name = names.fromString("toJson");
        Name name = names.fromString("toString");
        // 生成return语句，return this.xxx
//        JCTree.JCReturn returnStatement = treeMaker.Return(treeMaker.Literal("Haya!!!"));
        JCTree.JCReturn returnStatement = treeMaker.Return(
                treeMaker.Apply(
                        List.nil(),
                        treeMaker.Select(
                                treeMaker.Ident(
                                        elementUtils.getName("JavaMateJsonService")
                                ),
                                elementUtils.getName("toJson")
                        ),
                        List.of(treeMaker.Ident(names.fromString("this")))
                )
        );
        JCTree.JCBlock body = treeMaker.Block(0, List.of(returnStatement));
        JCTree.JCExpression returnType = treeMaker.Ident(names.fromString("String"));
        // 泛型参数列表
        List<JCTree.JCTypeParameter> methodGenericParamList = List.nil();
        // 参数值列表
        List<JCTree.JCVariableDecl> parameterList = List.nil();
        // 异常抛出列表
        List<JCTree.JCExpression> throwCauseList = List.nil();
        return treeMaker.MethodDef(modifiers, name, returnType,
                // 泛型参数列表
                methodGenericParamList,
                //参数值列表
                parameterList,
                // 异常抛出列表
                throwCauseList,
                // 方法默认体
                body, null);
    }

}
