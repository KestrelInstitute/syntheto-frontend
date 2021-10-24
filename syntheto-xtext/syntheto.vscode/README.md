# Midas Syntheto Language

This extension integrates the Syntheto Language into Visual Studio Code.

Supports:

* Syntax Coloring
* Content Assist
* Go To Definition

The language is integrated by a separate java process via the Language Server Protocol.

Here is some examples of the language syntax

## Types

```
subtype positive {
  x:int| x > 0
}

struct rational {
  numerator:int,
  denominator:positive
  | gcd (abs(numerator),abs(denominator)) ==1
}

```

## Functions

```
function abs(x:int) returns (a:int)
         ensures a >= 0 {
  if(x >= 0) {
    return x;
  }
  else {
    return -x;
  }   
} 
```

```
function lteq(x:rational, y:rational) returns (b:bool) {
  return x.numerator * y.denominator<=y.numerator * x.denominator;
}
```

## Theorems

Theorems can be defined on the functions.

```
theorem lteq_reflexive
  forall (x:rational) lteq(x,x)
```

```
theorem lteq_antisymmetric
  forall (x:rational,y:rational) lteq(x,y) && lteq(y,x) ==> x == y
```

```
theorem lteq_transitive
  forall (x:rational,y:rational,z:rational) lteq(x,y) && lteq(y,z) ==> lteq(x,z)
```

## Specifications

```
specification sort_spec
  (function sort (input:seq<rational>)
            returns (out:seq<rational>)) {
  issorted(out) && permutation(out,input)
}
```

where permutation is another function

```
function permutation (x:seq<rational>, y:seq<rational>)
         returns (b:boolean) {
  //...
}
```
