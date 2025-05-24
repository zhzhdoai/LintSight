// 正则表达式模式，用于从泛型签名中提取约束注解类型和被验证类型
val CONSTRAINT_VALIDATOR_PATTERN = "LConstraintValidator<L([^;]+);L([^;]+);>".r

/**
 * 查找带有指定注解的字段赋值操作
 * @param annotationField 要查找的注解名称
 * @param parameterTypePairs 参数及其类型全名的映射列表
 * @return 参数与相关赋值操作的配对列表
 */
def findAnnotatedFieldAssignments(
                                   annotationField: String,
                                   parameterTypePairs: List[(MethodParameterIn, String)]
                                 ): List[(MethodParameterIn, io.shiftleft.semanticcpg.language.operatorextension.OpNodes.Assignment)] = {
  parameterTypePairs.flatMap { case (param, typeName) =>
    cpg.typeDecl.fullNameExact(typeName).l.flatMap { typeDecl =>
      typeDecl.assignment
        .where(_.fieldAccess.where(_.member.annotation.name(annotationField)))
        .map(assignment => (param, assignment))
    }
  }
}

/**
 * 查找带有指定注解类型的声明
 * @param annotationType 要查找的注解类型
 * @param parameterTypePairs 参数及其类型全名的映射列表
 * @return 参数与相关赋值操作的配对列表
 */
def findAnnotatedTypeAssignments(
                                  annotationType: String,
                                  parameterTypePairs: List[(MethodParameterIn, String)]
                                ): List[(MethodParameterIn, io.shiftleft.semanticcpg.language.operatorextension.OpNodes.Assignment)] = {
  parameterTypePairs.flatMap { case (param, typeName) =>
    cpg.typeDecl.fullNameExact(typeName)
      .where(_.annotation.name(annotationType))
      .flatMap(_.assignment)
      .map(assignment => (param, assignment))
  }
}

/**
 * 从泛型签名中提取约束注解类型和被验证类型
 * @param validator 验证器类型声明
 * @return 可选元组(注解类型, 被验证类型)
 */
def extractValidationTypes(validator: TypeDecl): Option[(String, String)] = {
  Option(validator.genericSignature)  // 确保不为null
    .collect { case s: String => s }  // 确保是String类型
    .flatMap { signature =>
      CONSTRAINT_VALIDATOR_PATTERN.findFirstMatchIn(signature).flatMap { m =>
        if (m.groupCount >= 2) Some((m.group(1), m.group(2))) else None
      }
    }
}

@main def analyzeBeanValidation(code:String): Unit = {
  // 加载CPG
  importCode(code)

  // 准备差异图
  val diffGraph = Cpg.newDiffGraphBuilder

  // 获取所有带有Mapping注解且非基本类型的参数
  val mappingParameters = cpg.method
    .where(_.annotation.name(".*Mapping"))
    .parameter
    .filterNot(_.typeFullName.matches(".*(java\\.lang\\.Long|java\\.lang\\.Integer).*"))

  // 构建参数类型映射表(只包含带Valid/Validated注解的参数)
  val validatedParameters = mappingParameters.isParameter
    .filter(_.annotation.name("Valid|Validated").nonEmpty)
    .map(param => (param, param.typeFullName))
    .toList

  // 查找所有ConstraintValidator实现类
  val validatorImplementations = cpg.typeDecl
    .filter(_.inheritsFromTypeFullName.contains("javax.validation.ConstraintValidator"))
    .l
  println(s"找到 ${validatorImplementations.size} 个 ConstraintValidator 实现类")

  // 构建验证器映射关系
  val validatorMappings = validatorImplementations.flatMap { validator =>
    extractValidationTypes(validator).map { case (annotationType, _) =>
      val isValidMethod = validator.method.name("isValid").headOption
        .getOrElse(throw new NoSuchElementException(s"Validator ${validator.name} 缺少isValid方法"))

      val fieldAssignments = findAnnotatedFieldAssignments(annotationType, validatedParameters)
      val typeAssignments = findAnnotatedTypeAssignments(annotationType, validatedParameters)

      (isValidMethod, fieldAssignments ++ typeAssignments)
    }
  }

  // 构建调用图边
  validatorMappings.foreach { case (isValidMethod, assignments) =>
    assignments.foreach { case (_, assignment) =>
      diffGraph.addEdge(assignment, isValidMethod, EdgeTypes.CALL)
    }
  }

  // 应用差异图
  flatgraph.DiffGraphApplier.applyDiff(cpg.graph, diffGraph)

  // 查找验证路径
  val validationPaths = validatorMappings.flatMap { case (_, assignments) =>
    val violationTemplates = cpg.call
      .methodFullName(".*buildConstraintViolationWithTemplate.*")
      .argument
      .l

    assignments.flatMap { case (param, assignment) =>
      violationTemplates.reachableByFlows(assignment.fieldAccess)
        .distinctBy { flow =>
          (flow.elements.headOption.map(_.asInstanceOf[CfgNode].method.fullName),
            flow.elements.lastOption.map(_.asInstanceOf[CfgNode].method.fullName))
        }
        .sortBy(_.elements.size)(Ordering[Int].reverse)
        .map(flow => Path(param :: flow.elements))
    }
  }

  // 输出结果
  println(validationPaths.p)
}