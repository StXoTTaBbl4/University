@echo off
"C:\Program Files\Java\jdk-21\bin\java.exe" -Xmx500M -cp "D:\ITMO\3-re\University\Spring\Compilers\hw4\code\antlr-4.13.2-complete.jar;%CLASSPATH%" org.antlr.v4.Tool %* -Dlanguage=Python3 SimpleLang.g4 -visitor -no-listener
PAUSE