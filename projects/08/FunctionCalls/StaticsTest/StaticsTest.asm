//Init func
@256
D=A
@SP
M=D
//writeCall
@RETURN_LABEL0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@5
D=D-A
@0
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
//writeGoTO
@Sys.init
0;JMP
(RETURN_LABEL0)
//writeFunction
(Class1.set)
//Push ARG 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//pop static 
@SP
AM=M-1
D=M
@Class1.vm0
M=D
//Push ARG 1
@ARG
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//pop static 
@SP
AM=M-1
D=M
@Class1.vm1
M=D
//Push constant 0
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
//writeReturn
@LCL
D=M
@FRAME
M=D
@5
A=D-A
D=M
@RET
M=D
@ARG
D=M
@0
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
@ARG
D=M
@SP
M=D+1
@FRAME
D=M-1
AM=D
D=M
@THAT
M=D
@FRAME
D=M-1
AM=D
D=M
@THIS
M=D
@FRAME
D=M-1
AM=D
D=M
@ARG
M=D
@FRAME
D=M-1
AM=D
D=M
@LCL
M=D
@RET
A=M
0;JMP
//writeFunction
(Class1.get)
//Push Static Class1.vm0
@Class1.vm0
D=M
@SP
A=M
M=D
@SP
M=M+1
//Push Static Class1.vm1
@Class1.vm1
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
M=M-D
//writeReturn
@LCL
D=M
@FRAME
M=D
@5
A=D-A
D=M
@RET
M=D
@ARG
D=M
@0
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
@ARG
D=M
@SP
M=D+1
@FRAME
D=M-1
AM=D
D=M
@THAT
M=D
@FRAME
D=M-1
AM=D
D=M
@THIS
M=D
@FRAME
D=M-1
AM=D
D=M
@ARG
M=D
@FRAME
D=M-1
AM=D
D=M
@LCL
M=D
@RET
A=M
0;JMP
//writeFunction
(Sys.init)
//Push constant 6
@6
D=A
@SP
A=M
M=D
@SP
M=M+1
//Push constant 8
@8
D=A
@SP
A=M
M=D
@SP
M=M+1
//writeCall
@RETURN_LABEL1
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@5
D=D-A
@2
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
//writeGoTO
@Class1.set
0;JMP
(RETURN_LABEL1)
//pop temp 0
@R5
D=M
@5
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
//Push constant 23
@23
D=A
@SP
A=M
M=D
@SP
M=M+1
//Push constant 15
@15
D=A
@SP
A=M
M=D
@SP
M=M+1
//writeCall
@RETURN_LABEL2
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@5
D=D-A
@2
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
//writeGoTO
@Class2.set
0;JMP
(RETURN_LABEL2)
//pop temp 0
@R5
D=M
@5
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
//writeCall
@RETURN_LABEL3
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@5
D=D-A
@0
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
//writeGoTO
@Class1.get
0;JMP
(RETURN_LABEL3)
//writeCall
@RETURN_LABEL4
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@5
D=D-A
@0
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
//writeGoTO
@Class2.get
0;JMP
(RETURN_LABEL4)
//writeLabel
(WHILE)
//writeGoTO
@WHILE
0;JMP
//writeFunction
(Class2.set)
//Push ARG 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//pop static 
@SP
AM=M-1
D=M
@Class2.vm0
M=D
//Push ARG 1
@ARG
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//pop static 
@SP
AM=M-1
D=M
@Class2.vm1
M=D
//Push constant 0
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
//writeReturn
@LCL
D=M
@FRAME
M=D
@5
A=D-A
D=M
@RET
M=D
@ARG
D=M
@0
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
@ARG
D=M
@SP
M=D+1
@FRAME
D=M-1
AM=D
D=M
@THAT
M=D
@FRAME
D=M-1
AM=D
D=M
@THIS
M=D
@FRAME
D=M-1
AM=D
D=M
@ARG
M=D
@FRAME
D=M-1
AM=D
D=M
@LCL
M=D
@RET
A=M
0;JMP
//writeFunction
(Class2.get)
//Push Static Class2.vm0
@Class2.vm0
D=M
@SP
A=M
M=D
@SP
M=M+1
//Push Static Class2.vm1
@Class2.vm1
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
M=M-D
//writeReturn
@LCL
D=M
@FRAME
M=D
@5
A=D-A
D=M
@RET
M=D
@ARG
D=M
@0
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
@ARG
D=M
@SP
M=D+1
@FRAME
D=M-1
AM=D
D=M
@THAT
M=D
@FRAME
D=M-1
AM=D
D=M
@THIS
M=D
@FRAME
D=M-1
AM=D
D=M
@ARG
M=D
@FRAME
D=M-1
AM=D
D=M
@LCL
M=D
@RET
A=M
0;JMP
