# Groovy Path Walker  
Groovy Path Walker, given a structure and groovy navigation path, provides a more traditional navigation and less memory impacting
- **The given structure can be a map or a list**  
- **The navigation path support the following features**  
  - direct accessor; a.b.c.d
  - accessor beetween square brackets; a[b][c][d]
  - list indexes; list[12]
  - resolves scope variable; variable's value will be used to navigate the structure
  - resolves object attributes and methods trought reflection
  - supports fortress built in functions; size, pick, values, keySet
  - resolves simple string or simple digit 
- **Methods**  
  - walk(path,scope,item): starts the structure navigation; it takes the path to navigate, the scope and the initial item
  - walk(path,scope): starts the structure navigation; it takes the path to navigate. In this case item will be the scope
  - isSupported(path): checks if a path is supported.  
    Braces parenthesys, star, ->, question mark, exclamation mark, assignement operatore are not supported({} * -> ? ! =). Only size, pick, values, keySet are supported functions
- **Usage examples**  
  def map = ['a': ['b': ['c': ['d': 'bar1']]]]  
  def scope = ['var':'d']  
  def path = 'a.b.c[var]'
  GroovyPathWalker.walk(path,scope,map) 
  return value will be bar1 
       