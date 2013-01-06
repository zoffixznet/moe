package org.moe.ast

import org.moe.runtime._

abstract class AST

// AST containers

case class CompilationUnitNode ( body : AST ) extends AST
case class ScopeNode           ( body : AST ) extends AST

case class StatementsNode      ( nodes : List[ AST ] ) extends AST

// literals

case class LiteralNode ( value  : MoeObject ) extends AST

case class SelfLiteralNode  () extends AST
case class ClassLiteralNode () extends AST
case class SuperLiteralNode () extends AST

case class PairLiteralNode ( key : String, value : MoeObject ) extends AST

case class ArrayLiteralNode ( values : List[ MoeObject ]       ) extends AST
case class HashLiteralNode  ( map    : List[ PairLiteralNode ] ) extends AST

// unary operators

case class IncrementNode ( reciever : AST ) extends AST
case class DecrementNode ( reciever : AST ) extends AST
case class NotNode       ( reciever : AST ) extends AST

// binary operators

case class AndNode ( lhs : AST, rhs : AST ) extends AST
case class OrNode  ( lhs : AST, rhs : AST ) extends AST

// value lookup, assignment and declaration

case class ClassAccessNode      ( name : String ) extends AST
case class ClassDeclarationNode ( name : String, superclass : String, body : AST ) extends AST

case class PackageDeclarationNode ( name : String, body : AST ) extends AST

case class ConstructorDeclarationNode ( params : List[ String ], body : AST ) extends AST
case class DestructorDeclarationNode  ( params : List[ String ], body : AST ) extends AST

case class MethodDeclarationNode     ( name : String, params : List[ String ], body : AST ) extends AST
case class SubroutineDeclarationNode ( name : String, params : List[ String ], body : AST ) extends AST

case class AttributeAccessNode      ( name : String                   ) extends AST
case class AttributeAssignmentNode  ( name : String, expression : AST ) extends AST
case class AttributeDeclarationNode ( name : String, expression : AST ) extends AST

case class VariableAccessNode      ( name : String                   ) extends AST
case class VariableAssignmentNode  ( name : String, expression : AST ) extends AST
case class VariableDeclarationNode ( name : String, expression : AST ) extends AST

// operations

case class MethodCallNode     ( invocant : AST, method_name : String, args : List[ AST ] ) extends AST
case class SubroutineCallNode ( function_name : String, args : List[ AST ] ) extends AST

// statements

case class IfNode          ( if_condition : AST, if_body : AST ) extends AST
case class IfElseNode      ( if_condition : AST, if_body : AST, else_body : AST ) extends AST
case class IfElsifNode     ( if_condition : AST, if_body : AST, elsif_condition : AST, elsif_body : AST ) extends AST
case class IfElsifElseNode ( if_condition : AST, if_body : AST, elsif_condition : AST, elsif_body : AST, else_body : AST ) extends AST

case class UnlessNode     ( unless_condition : AST, unless_body : AST ) extends AST
case class UnlessElseNode ( unless_condition : AST, unless_body : AST, else_body : AST ) extends AST

case class TryNode     ( body : AST, catch_nodes : List[ CatchNode ], finally_nodes : List[ FinallyNode ] ) extends AST
case class CatchNode   ( type_name : String, local_name : String, body : AST ) extends AST
case class FinallyNode ( body : AST ) extends AST

case class WhileNode   ( condition : AST, body : AST ) extends AST
case class DoWhileNode ( condition : AST, body : AST ) extends AST

case class ForeachNode ( topic : AST, list : AST, body : AST ) extends AST
case class ForNode ( init : AST, condition : AST, update : AST, body : AST ) extends AST






