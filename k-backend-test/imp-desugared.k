// Copyright (c) 2014-2017 K Team. All Rights Reserved.
module IMP-DESUGARED-SYNTAX

/*
 This is a version of imp.k which shows the details of
 the semantics more explicitly, by avoiding some convenience
 features of K that can be translated away.
 
 The end of the IMP-DESUGARED module includes the additional
 productions and rules which would be generated from the "strict"
 attributes used in imp.k.

 All rules explicitly show the "k" and "state" cells, rather than
 relying on the K feature called "configuration abstraction".
*/

  syntax AExp  ::= Int | Id
                 | AExp "/" AExp              [left]
                 > AExp "+" AExp              [left]
                 | "(" AExp ")"               [bracket]
  syntax BExp  ::= Bool
                 | AExp "<=" AExp
                 | "!" BExp
                 > BExp "&&" BExp             [left]
                 | "(" BExp ")"               [bracket]
  syntax Block ::= "{" "}"
                 | "{" Stmt "}"
  syntax Stmt  ::= Block
                 | Id "=" AExp ";"
                 | "if" "(" BExp ")"
                   Block "else" Block
                 | "while" "(" BExp ")" Block
                 > Stmt Stmt                  [left]

  syntax Pgm ::= "int" Ids ";" Stmt
  syntax Ids ::= List{Id,","}
endmodule

module IMP-DESUGARED
  imports IMP-DESUGARED-SYNTAX


  syntax KResult ::= Int | Bool

  configuration <T color="yellow">
                  <k color="green"> $PGM:Pgm </k>
                  <state color="red"> .Map </state>
                  <exit-code exit="true"> 0 </exit-code>
                    // RVK requires a cell with exit="true".
                    // exit-code isn't otherwise used in the definition.
                </T>

  rule <k> X:Id => I ...</k> <state>... X |-> I ...</state>

  rule <k> I1 / I2 => I1 /Int I2 ...</k> <state> _ </state>
          requires I2 =/=Int 0
  rule <k> I1 + I2 => I1 +Int I2 ...</k> <state> _ </state>

  rule <k> I1 <= I2 => I1 <=Int I2 ...</k> <state> _ </state>
  rule <k> ! T => notBool T ...</k> <state> _ </state>
  rule <k> true && B => B ...</k> <state> _ </state>
  rule <k> false && _ => false ...</k> <state> _ </state>

  rule {} => .   [structural]
  rule {S} => S  [structural]

  rule <k> X = I:Int; => . ...</k> <state>... X |-> (_ => I) ...</state>

  rule <k> S1:Stmt S2:Stmt => S1 ~> S2 ...</k> <state> _ </state>  [structural]

/*@ \subsubsection{Conditional}
The conditional statement has two semantic cases, corresponding to
when its condition evaluates to \texttt{true} or to \texttt{false}.
Recall that the conditional was annotated with the attribute
\texttt{strict(1)} in the syntax module above, so only its first
argument is allowed to be evaluated. */

  rule if (true)  S else _ => S
  rule if (false) _ else S => S

/*@ \subsubsection{While loop}
We give the semantics of the \texttt{while} loop by unrolling.
Note that we preferred to make the rule below structural. */

  rule while (B) S => if (B) {S while (B) S} else {}  [structural]

/*@ \subsection{Programs}
The semantics of an IMP program is that its body statement is executed
in a state initializing all its global variables to 0.  Since \K's
syntactic lists are internally interpreted as cons-lists (i.e., lists
constructed with a head element followed by a tail list), we need to
distinguish two cases, one when the list has at least one element and
another when the list is empty.  In the first case we initialize the
variable to 0 in the state, but only when the variable is not already
declared (all variables are global and distinct in IMP).  We prefer to
make the second rule structural, thinking of dissolving the residual
empty \texttt{int;} declaration as a structural cleanup rather than as
a computational step. */

  rule <k> int (X,Xs => Xs);_ ...</k> <state> Rho:Map (.Map => X|->0) </state>
    requires notBool (X in keys(Rho))
  rule int .Ids; S => S  [structural]

  /*
   These productions are added by strictness, and are used to
   hold the rest of the subterms of a production while evaluation
  */
  syntax KItem ::= "[]" "/" AExp              [klabel(divL)]
                 | AExp "/" "[]"              [klabel(divR)]
                 | "[]" "+" AExp              [klabel(addL)]
                 | AExp "+" "[]"              [klabel(addR)]
                 | "[]" "<=" AExp             [klabel(leL)]
                 | AExp "<=" "[]"             [klabel(leR)]
                 | "!" "[]"                   [klabel(notF)]
                 | "[]" "&&" BExp             [klabel(andL)]
                 | Id "=" "[]" ";"            [klabel(assignR)]
                 | "if" "(" "[]" ")"
                      Block "else" Block      [klabel(ifC)]

  rule <k> E1 / E2 => E1 ~> [] / E2 ...</k> <state> _ </state>
    requires notBool (isKResult(E1))
  rule <k> R ~> [] / E2 => R / E2 ...</k> <state> _ </state>
     requires isKResult(R)
  rule <k> E1 / E2 => E2 ~> E1 / [] ...</k> <state> _ </state>
    requires isKResult(E1) andBool (notBool (isKResult(E2)))
  rule <k> R ~> E1 / [] => E1 / R ...</k> <state> _ </state>
    requires isKResult(R)
  rule <k> E1 + E2 => E1 ~> [] + E2 ...</k> <state> _ </state>
    requires notBool (isKResult(E1))
  rule <k> R ~> [] + E2 => R + E2 ...</k> <state> _ </state>
    requires isKResult(R)
  rule <k> E1 + E2 => E2 ~> E1 + [] ...</k> <state> _ </state>
    requires isKResult(E1) andBool (notBool (isKResult(E2)))
  rule <k> R ~> E1 + [] => E1 + R ...</k> <state> _ </state>
    requires isKResult(R)
  rule <k> E1 <= E2 => E1 ~> [] <= E2 ...</k> <state> _ </state>
    requires notBool (isKResult(E1))
  rule <k> R ~> [] <= E2 => R <= E2 ...</k> <state> _ </state>
    requires isKResult(R)
  rule <k> E1 <= E2 => E2 ~> E1 <= [] ...</k> <state> _ </state>
    requires isKResult(E1) andBool (notBool (isKResult(E2)))
  rule <k> R ~> E1 <= [] => E1 <= R ...</k> <state> _ </state>
    requires isKResult(R)
  rule <k> ! E => E ~> ! [] ...</k> <state> _ </state>
    requires notBool (isKResult(E))
  rule <k> R ~> ! [] => ! R ...</k> <state> _ </state>
    requires isKResult(R)
  rule <k> Id = E ; => E ~> Id = [] ; ...</k> <state> _ </state>
    requires notBool (isKResult(E))
  rule <k> R ~> Id = [] ; => Id = R ; ...</k> <state> _ </state>
    requires isKResult(R)
  rule <k> if ( C ) T else E => C ~> if ( [] ) T else E ...</k> <state> _ </state>
    requires notBool (isKResult(C))
  rule <k> R ~> if ( [] ) T else E => if ( R ) T else E ...</k> <state> _ </state>
    requires isKResult(R)
endmodule
