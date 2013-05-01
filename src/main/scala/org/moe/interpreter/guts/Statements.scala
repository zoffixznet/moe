package org.moe.interpreter.guts

import org.moe.interpreter._
import org.moe.runtime._
import org.moe.runtime.nativeobjects._
import org.moe.ast._
import org.moe.parser._

import InterpreterUtils._

import scala.io.Source

object Statements {

  private val stub = new MoeObject()

  def apply (i: Interpreter, r: MoeRuntime, env: MoeEnvironment): PartialFunction[AST, MoeObject] = {
    case UseStatement(name) => {
      val path = r.findFilePathForPackageName(name).getOrElse(
        throw new MoeErrors.MoeProblems(
          "Could not find module " + name + " in @INC [" + r.getIncludeDirs.mkString("; ") + "]"
        )       
      )

      env.getAs[MoeHashObject]("%INC").get.bind_key(
        r, 
        r.NativeObjects.getStr(name), 
        r.NativeObjects.getStr(path.toString)
      )

      val result = i.eval(
        r, 
        env, 
        MoeParser.parseFromEntry(Source.fromFile(path).mkString)
      )

      result match {
        case (p: MoePackage) => env.getCurrentPackage.get.importSubroutines(
          MoePackage.findPackageByName(name, r.getRootPackage).getOrElse(
            throw new MoeErrors.PackageNotFound(name)
          ).getExportedSubroutines
        )
        case _ => ()
      }

      result
    }

    case IfNode(if_node) => {
      if (i.eval(r, env, if_node.condition).isTrue) {
        i.eval(r, env, if_node.body)
      } else if (if_node.else_node.isDefined) {
        i.eval(r, env, IfNode(if_node.else_node.get))
      } else {
        r.NativeObjects.getUndef
      }
    }
        
    case UnlessNode(unless_condition, unless_body) => {
      i.eval(r, env,
        UnlessElseNode(
          unless_condition,
          unless_body,
          UndefLiteralNode()
        )
      )
    }
    case UnlessElseNode(unless_condition, unless_body, else_body) => {
      var if_node = new IfStruct(
        PrefixUnaryOpNode(unless_condition, "!"), 
        unless_body, 
        Some(
          new IfStruct(
            BooleanLiteralNode(true), 
            else_body
          )
        )
      )
      i.eval(r, env, IfNode(if_node))
    }

    case TryNode(body, catch_nodes, finally_nodes) => stub
    case CatchNode(type_name, local_name, body) => stub
    case FinallyNode(body) => stub

    case WhileNode(condition, body) => {
      val newEnv = new MoeEnvironment(Some(env))
      while (i.eval(r, newEnv, condition).isTrue) {
        i.eval(r, newEnv, body)
      }
      r.NativeObjects.getUndef // XXX
    }

    case DoWhileNode(condition, body) => {
      val newEnv = new MoeEnvironment(Some(env))
      do {
        i.eval(r, newEnv, body)
      } while (i.eval(r, newEnv, condition).isTrue)
      r.NativeObjects.getUndef // XXX
    }

    case ForeachNode(topic, list, body) => {
      i.eval(r, env, list) match {
        case objects: MoeArrayObject => {
          val applyScopeInjection = {
            (
              newEnv: MoeEnvironment, 
              name: String, 
              obj: MoeObject, 
              f: (MoeEnvironment, String, MoeObject) => Any
            ) =>
            f(env, name, obj)
            i.eval(r, newEnv, body)
          }

          val newEnv = new MoeEnvironment(Some(env))
          for (o <- objects.getNativeValue) // XXX - fix this usage of getNativeValue
            topic match {
              // XXX ran into issues trying to i.eval(r, env, ScopeNode(...))
              // since o is already i.evaluated at this point
              case VariableDeclarationNode(name, expr) =>
                applyScopeInjection(newEnv, name, o, (_.create(_, _)))
              // Don't do anything special here, env access will just walk back
              case VariableAccessNode(name) =>
                applyScopeInjection(newEnv, name, o, (_.set(_, _)))
            }
          r.NativeObjects.getUndef // XXX
        }
      }
    }

    case ForNode(init, condition, update, body) => {
      val newEnv = new MoeEnvironment(Some(env))
      i.eval(r, newEnv, init)
      while (i.eval(r, newEnv, condition).isTrue) {
        i.eval(r, newEnv, body)
        i.eval(r, newEnv, update)
      }
      r.NativeObjects.getUndef
    }
  }
}