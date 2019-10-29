# Groovy Path Walker  
## Introduction  
Groovy offers an easy way to navigate nested data structures.

It includes two notations:
- dot notation  
  `a.b.c.d`  
- brackets notation  
  `a['b']['c']`, `a["b"]["c"]`  
- any combination of the notations above  
  `a.b['c'].d`

While using them is fine within the code, it becomes a problem when you need to navigate a data structure given a path expressed as a string.  
The most common way is to compile the path as a groovy script, but this has both security and memory drawbacks.  
This library is meant to mitigate the security and memory drawbacks providing the same features of groovy navigation
  
## Features  
### Input  
Input structure can be:
  - nested structure as a map or a list, any combination of nested structures are allowed. For example map containg a list of maps.
  - simple string or simple digit  
    "foobar", 123
  - scope containing variables and the structure to navigate
  - generic java object
### Path  
Path supports the following notations
  - direct accessor  
    `a.b.c.d`
  - accessor between square brackets and quotes or double quotes  
    `a['b']['c']`, `a["b"]["c"]`  
  - list indexes  
    `list[12]`
  - scope variables between square brackets  
    `a[scopeVariable]`
  - any combination of previous accessors  
    `a.b[c][12].d.e[0].e[scopeVariable]`
  
### Navigation Features  
Navigation supports the following features  
  - resolves direct accessors
  - resolves bracketed accessors
  - resolves list indexes
  - resolves scope variable. variable's value will be used to navigate the structure
  - resolves generic object attributes and methods through reflection  
    a.b.string.length
  - resolves built in functions.  
    Supported functions are size, pick, values, keySet

## Public Methods  
  - walk(path,scope,item): starts the structure navigation; it takes the path to navigate, the scope and the initial item (if null the scope will be the initial item)  
  - isSupported(path): checks if the path provided is supported.  
    The path is not supported if includes unsupported characters or unsupported built in functions  
    Unsupported characters are:
      - braces brackets: `{}`
      - star `*` 
      - `->`
      - question mark `?`
      - exclamation mark `!`
      - assignment operator `=`  
      
    Built in functions are
    - `size`
    - `pick`
    - `values`
    - `keySet`

## Usage examples  
  ```
  def map = ['a': ['b': ['c': ['d': 'bar']]]]  
  def scope = ['var':'d']  
  def path = 'a.b.c[var]'
  assert('bar'==GroovyPathWalker.walk(path,scope,map))
  ``` 
       
## Maven
```
<dependency>
  <groupId>com.apifortress</groupId>
  <artifactId>groovy-path-walker</artifactId>
  <version>1.0.0</version>
</dependency>
``` 
