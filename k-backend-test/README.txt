The major project for your prospective job will be
working on an improved compiler for the K language
(http://www.kframework.org)
This compiler will translate a K definition of
a language into an interpreter that can run programs
in that language.

For this project we have a single simple K definition, imp.k,
and instead of a compiler you will directly write
a few translations of that K definition into other languages.
We included two examples showing the sort of translation we want,
written in Standard ML and Maude.
Contact us with any questions. Contact information is at
the end of this file.

This project is very open ended, so don't worry about
trying for an optimal solution. Several parts of this
project are things that we expect to work seriously on
improving for several months in the real job, so don't go overboard.
This is expected to take 2-3 days if you have no
experience with K, and might be finished in a day if you do.

We mostly want to see how you think about these problems.
We also want to see a bit of newly written code demonstrating
some skill in a few languages of interest.
Just getting everything installed and running the examples
demonstrates plenty of skill in setting up a development
environment.

You will need a Linux system (perhaps in a VM).
We officially support Ubuntu 16 LTS, but if you
prefer another Linux distribution you can probably
get it working (anything Debian-based should be easy).

You will:

1. Write three programs implementing very straightforward
   translations of the K semantics into
 - Java
 - Scala
 - A functional language in the ML family
   (Haskell, OCaml, F#, Clean, etc.)
   Use a language other than Standard ML,
   because that's what we used for the example.

   Read the Maude and SML to see the sort of translation
   we want. The rules of the IMP definition should
   be translated very directly into a function that
   takes a single execution step.
   Part 3 will be improving the translation. For now,
   make a very straightforward "small step" implementation.
   Also, we are interested in semantics rather than parsing,
   so hardcoding the AST of the example program(s) is expected.

   This exercise is to get familiar with the K semantics
   and to show your skill in a few languages of interest.
   Without pattern matching the Java program will be
   somewhat tedious. Please bear with us.

   You may include programs in additional languages
   (including if you want to demonstrate skill in SML),
   but we will almost certainly be more interested
   in work toward part 3.

2. Compare performance of your handwritten IMP interpreters above,
   the provided example IMP interpreters, and the UIUC and RV
   implementations of K on imp.k.
   Use in all cases the sum program.

3. Think of a few ways to improve on the very direct translation
   and the interpreters from #1, and test these possible improvements.

   You will ultimately be working on a K compiler, so you
   should have some idea how these improved translations
   could be automated to work on most K definitions.
   (you do not need to include a written explanation,
   but be prepared to discuss your improvements).

   It is fine if you would need some annotations in the K
   definition to help automatically generate code like
   the improved interpreters you write.
   But, just writing a fast interpreter from scratch
   without any idea how to get it automatically from
   a K definition would not be interesting
   (it might be useful as a comparison in part 4).

   The example interpreter imp.sml is also the fastest
   we came up with while designing this project.
   See if you can do better!
   
4. Make a final comparison of performance.

Included Files:
===============

We have a K definition `imp.k` of a simple programming
language called IMP, taken directly from the K tutorial.
The file `imp-desugared.k` defines the same language but
explicitly shows additional details that were abbreviated
using certain K features in `imp.k`.

The definition of IMP is Part 2 of the K Tutorial, found at
tutorial/1_k/2_imp/ in a distribution of K.
Lessons 3 and 4 in particular explain "strictness"
and "configuration abstraction".
Videos for the tutorial can be found at
http://www.kframework.org/index.php/K_Tutorial

A few simple programs written in this language are provided:
 `sum.imp` sums numbers from 1 to n
 `collatz.imp` runs the "collatz sequence" from a given number
 `primes.imp` counts the number of primes in the range
    from 2 to m.
These programs all hardcode the input size, because IMP has
no IO or argument parsing. Your interpreters should instead
take a command line argument.

We also include some ports of sum.imp into other languages.
These were written to give some idea how efficiently the sum.imp
program might run if a custom interpreter or compiler were
written for the IMP language (as a reference for improving the
K backends).
sum.py in Python (works in Python 3 or 2)
sum.lua in Lua for the main implementation (http://lua.org)
sum.luajit in Lua but for luajit  (http://luajit.org - impressively fast)
sum.c in C
sum.ml in OCaml

`imp.maude` is an example translation of `imp.k` into Maude
(http://maude.cs.illinois.edu),
`imp.sml`, `imp.mlb` and `maps.sml` are an example translation
of the definition into SML, written to work with the MLton compiler
Both work on the latest release.

These are all meant to be fairly straightforward translations of the
K definition, and give some idea how literally you should implement
the K rules, and how flexibly you can implement the AST.

You will also need to install the K implementations from
UIUC and Runtime Verification (instructions below).

The Makefile will compile everything if you have
all the necessary tools, and have edited it to
set the paths to the RV and UIUC K installations
(with two versions we cannot simply rely on finding
`kompile` and `krun` in PATH).

To run the IMP programs with K you will need to
pass `-d imp-rvk` to the RV version of krun and
pass `-d imp-uiuck` to the UIUC version of krun.
see the Makefile for commands to recompile the K
definitions.

Installing K:
=============

For UIUC K, see https://github.com/kframework/k
You can use 4.0 release (which is a year old but still
functional), or compile from source.

For RV K, see RV: https://github.com/runtimeverification/k
You can compile from source, or use a recent snapshot from
http://office.runtimeverification.com:8888/repository/snapshots/com/runtimeverification/k/k-distribution/1.0-SNAPSHOT/
(there is no nicer URL for prebuilt snapshots, sorry).

The installation instructions in the runtimeverification/k
repository are out of date when it comes to required non-java libraries,
but you can install everything required by running

   sudo apt-get install git maven build-essential diffutils libxml-libxml-perl libstring-escape-perl libgetopt-declare-perl opam openjdk-8-jdk libgmp-dev libmpfr-dev m4 pkg-config libffi-dev flex cmake clang-3.9 libclang-3.9-dev zlib1g-dev

and then setting up OCaml/opam by running the included script

   k-distribution/src/main/scripts/bin/k-configure-opam-dev

Please let us know if there are any problems with the
installation/build instructions for these projects

Contact information
===================
Runtime Verification, Inc.
  email: contact@runtimeverification.com
Grigore Rosu (UIUC)
  email: grosu@illinois.edu
  office phone: +1 (217) 244-7431
  homepage: http://fsl.cs.illinois.edu/index.php/Grigore_Rosu
