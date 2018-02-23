JAVACC - CALCULATOR PROGRAM
Tanmay Gore ( tgore03@iastate.edu )
15th January 2018


1. Execution Process:
	javacc Calculator.jj
	javac Calculator.java
	java Calculator
	< Enter mathematical equations to calculate>

2. Specifications:
a. Program accepts only integers as input without decimal places.
b. Outputs result in integer.
c. Operations accepted: Addition, Subtraction, Multiplication, Division, Parenthesis, Negation.

3. Output:

PS C:\Tanmay\Assignments - Github folder\Semester 3\Advanced Database System\Programming Assignment1> javacc Calculator.jj
Java Compiler Compiler Version 5.0 (Parser Generator)
(type "javacc" with no arguments for help)
Reading from file Calculator.jj . . .
File "TokenMgrError.java" is being rebuilt.
File "ParseException.java" is being rebuilt.
File "Token.java" is being rebuilt.
File "SimpleCharStream.java" is being rebuilt.
Parser generated successfully.
PS C:\Tanmay\Assignments - Github folder\Semester 3\Advanced Database System\Programming Assignment1> javac Calculator.java
PS C:\Tanmay\Assignments - Github folder\Semester 3\Advanced Database System\Programming Assignment1> java Calculator
-2 + 2/(3*2) +1
Result = -1

2 + 2/(3*2)
Result = 2


4. References:
a. https://www.engr.mun.ca/~theo/JavaCC-Tutorial/javacc-tutorial.pdf
b. http://digital.cs.usu.edu/~allanv/cs4700/javacc/javacc.html
c. https://javacc.org/




