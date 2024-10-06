import java.io.*;
import java.util.*;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;

class ReversePolishCalculator {
    Queue<Character> q;
    Queue<String> q1;

    ReversePolishCalculator() {
        this.q = new LinkedList<Character>(); //queue as a inteface is availabale in java, hence we are extending linked list class to use queue functions
    }

    ReversePolishCalculator(String s) {
        this.q1 = new LinkedList<String>();
    }
    
    void push(char x) {
        q.add(x);
        
        int currSize = q.size();
        while((currSize--) > 1) {
            q.add(q.poll());  //poll is for front element
        } 
    }

    void pushStr(String x) {
        q1.add(x);
        
        int currSize = q1.size();
        while((currSize--) > 1) {
            q1.add(q1.poll());
        }
    }
    
    void pop() {
        q.poll();
    }

    void popStr() {
        q1.poll();
    }
    
    char top() {
        return q.peek();
    }

    String topStr() {
        return q1.peek();
    }
    
    boolean empty() {
        return q.isEmpty();
    }

    boolean emptyStr() {
        return q1.isEmpty();
    }

    boolean isalpha(char c) {
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
            return true;

        return false;
    }

    boolean isdigit(char c) {
        return (c >= '0' && c <= '9');
    }

    boolean isOperator(char c) {
        return (!isalpha(c) && !isdigit(c));
    }

    int getPriority(char ch) {
        if(ch == '-' || ch == '+')
            return 1;
        else if(ch == '*' || ch == '/')
            return 2;
        else if(ch == '^')
            return 3;
    
        return 0;
    }

    String reverse(char str[], int start, int end) {
        char temp;
        while (start < end) {
            temp = str[start];
            str[start] = str[end];
            str[end] = temp;

            ++start;
            --end;
        }

        return String.valueOf(str);
    }

    String checkTypeOfNotation(String notation) {
        /**if we have first non-bracket character as operator, 
         * then it is a prefix notation
         * if we have first non-bracket character as operand and second as operator,
         * then it is an infix notation
         * if we have last non-bracket character as operator,
         *  then it is a postfix notation **/

        int i = 0, len = notation.length();

        while(i < len) {
            if((notation.charAt(i) != '(') || (notation.charAt(i) == ')')) {
                //prefix check
                if(isOperator(notation.charAt(i))) {
                    return "PREFIX";
                } 

                else {
                    /**it's neither bracket nor operator, 
                     * so first charcter is an operand, so prefix check**/
                    int j = i + 1;
                    while(j < len) {
                        if((notation.charAt(j) != '(') || (notation.charAt(j) == ')')) {
                            if(isOperator(notation.charAt(j))) {
                                return "INFIX";
                            }
                            break;
                        }
                        ++j;
                    }
                    break;
                }
            }
            ++i;
        }

        i = len - 1;
        while(i >= 0) {
            if((notation.charAt(i) != '(') || (notation.charAt(i) == ')')) {
                if(isOperator(notation.charAt(i))) {
                    return "POSTFIX";
                }
                break;
            }
            --i;
        }

        return "INVALID";
    }

    String infixToPostfix(char[] infix1) {
        String infix = '(' + String.valueOf(infix1) + ')';
    
        int l = infix.length();
        ReversePolishCalculator st = new ReversePolishCalculator(); 
        String output="";
    
        for (int i = 0; i < l; ++i) {
            if (isalpha(infix.charAt(i)) || isdigit(infix.charAt(i)))
                output += infix.charAt(i);
            else if (infix.charAt(i) == '(')
                st.push('(');
            else if (infix.charAt(i) == ')') {
                while (st.top() != '(') {
                    output += st.top();
                    st.pop();
                }

                // Remove '(' from the stack
                st.pop();
            }
            // Operator found
            else {
                if (isOperator(st.top())) {
                    while ((getPriority(infix.charAt(i)) < getPriority(st.top())) || 
                        (getPriority(infix.charAt(i)) <= getPriority(st.top()) && 
                            infix.charAt(i) == '^')) {
                        output += st.top();
                        st.pop();
                    }

                    st.push(infix.charAt(i));
                }
            }
        }

        while(!st.empty()) {
            output += st.top();
            st.pop();
        }

        return output;
    }

    String infixToPrefix(char[] infix) {
        int l = infix.length;

        // Reverse infix
        String infix1 = reverse(infix, 0, l - 1);
        infix = infix1.toCharArray();

        // Replace ( with ) and vice versa
        for (int i = 0; i < l; ++i) {
            if(infix[i] == '(') {
                infix[i] = ')';
                ++i;
            } else if(infix[i] == ')') {
                infix[i] = '(';
                ++i;
            }
        }

        String prefix = infixToPostfix(infix);
        // Reverse postfix
        prefix = reverse(prefix.toCharArray(), 0, prefix.length() - 1);

        return prefix;
    }

    String prefixToInfix(char[] prefix) {
        int l = prefix.length;
        ReversePolishCalculator st = new ReversePolishCalculator("str");

        for(int i = l - 1; i >= 0; i--) {
            if(isOperator(prefix[i])) {
                String op1 = st.topStr();
                st.popStr();
                String op2 = st.topStr();
                st.popStr();
                
                String temp = "(" + op1 + prefix[i] + op2 + ")";
                st.pushStr(temp);
            } else {
                // To make character to string
                st.pushStr(prefix[i] + "");
            }
        }

        return st.topStr();
    }

    String prefixToPostfix(char[] prefix) {
        int l = prefix.length;
        ReversePolishCalculator st = new ReversePolishCalculator("str");

        for(int i = l - 1; i >= 0; i--) {
            if (isOperator(prefix[i])) {
                String op1 = st.topStr();
                st.popStr();
                String op2 = st.topStr();
                st.popStr();

                String temp = op1 + op2 + prefix[i];
                st.pushStr(temp);
            } else {
                // push the operand to the stack
                st.pushStr(prefix[i] + "");
            }
        }
 
        return st.topStr();
    }

    String postfixToInfix(char[] postfix) {
        int l = postfix.length;
        ReversePolishCalculator st = new ReversePolishCalculator("str");
    
        for (int i = 0; i < l; ++i) {
            if(isalpha(postfix[i]))
                st.pushStr(postfix[i] + "");
    
            //Assuming that input is a valid postfix and expect an operator.
            else {
                String op1 = st.topStr();
                st.popStr();
                String op2 = st.topStr();
                st.popStr();

                st.pushStr("(" + op2 + postfix[i] + op1 + ")");
            }
        }

        return st.topStr();
    }

    String postfixToPrefix(char[] postfix) {
        int l = postfix.length;
        ReversePolishCalculator st = new ReversePolishCalculator("str");
 
        for(int i = 0; i < l; ++i) {
            if(isOperator(postfix[i])) {
                String op1 = st.topStr();
                st.popStr();
                String op2 = st.topStr();
                st.popStr();
 
                String temp = postfix[i] + op2 + op1;
                st.pushStr(temp);
            } else {
                // push the operand to the stack
                st.pushStr(postfix[i] + "");
            }
        }
        
        String ans = "";
        for (String i : st.q1) {
            ans += i;
        }

        return ans;
    }

    public static void main(String[] args) {   
        try {
            Scanner sc = new Scanner(System.in);    

            ReversePolishCalculator o = new ReversePolishCalculator();  //object to call other methods
            
            Map<String, Integer> mapping = new HashMap<>();
            mapping.put("INFIX", 1);
            mapping.put("PREFIX", 2);
            mapping.put("POSTFIX", 3);
            int ans;

        do {
            File yourFile = new File("output.txt");
            yourFile.createNewFile(); // if file already exists will do nothing 
            FileOutputStream oFile = new FileOutputStream(yourFile, true);  //FALSE MEANS NOT CREATING NEW FILE OBJECT

            System.out.println("WELCOME TO REVERSE POLISH CALCULATOR");
            System.out.println("Enter valid expression:");

            //STEP-1: GETTING THE EXPRESSION FROM USER
            String notation = sc.nextLine();
            oFile.write(("Input String: " + notation + "\n").getBytes());

            /**STEP-2: IDENTIFYING THE TYPE OF NOTATION, HERE WE ARE ASSUMING THAT THE
             *  EXPRESSION WOULD BE NORMAL CONSISTING OF NO BRACKETS**/
            String typeOfNotation = o.checkTypeOfNotation(notation);

            //STEP-3: INFORM USER ABOUT THE TYPE OF NOTATION, AND THEN ASK THE USER FOR CONVERSION INTO.
            System.out.println("Input literal is of type = " + typeOfNotation);
            oFile.write(("Input literal is of type = " + typeOfNotation + "\n").getBytes());
            if(typeOfNotation.equals("INVALID")) {
                oFile.close();
                sc.close();        //if we get expression invalid, then we close the file and exit the program.
                System.exit(0);
            }
            
            int derivedInput = mapping.get(typeOfNotation); //
            int input;
            while(true) {
                System.out.println("Please Choose an appropriate number from below for conversion of the notation:");
                System.out.println("1. INFIX\n2. PREFIX\n3. POSTFIX");

                input = sc.nextInt();
                if(input != derivedInput)
                    break;
            }

            if(input == 1) {
                oFile.write(("Choice of conversion: INFIX\n").getBytes());
            } else if(input == 2) {
                oFile.write(("Choice of conversion: PREFIX\n").getBytes());
            } else if(input == 3) {
                oFile.write(("Choice of conversion: POSTFIX\n").getBytes());
            }

            //STEP-4: OUTPUT THE CONVERTED NOTATION/EXPRESSION
            //INFIX TO PREFIX
            String output = "";
            if(derivedInput == 1 && input == 2) {
                output = o.infixToPrefix(notation.toCharArray());
            }
            //INFIX TO POSTFIX
            else if(derivedInput == 1 && input == 3) {
                output = o.infixToPostfix(notation.toCharArray());
            }
            //PREFIX TO INFIX
            else if(derivedInput == 2 && input == 1) {
                output = o.prefixToInfix(notation.toCharArray());
            }
            //PREFIX TO POSTFIX
            else if(derivedInput == 2 && input == 3) {
                output = o.prefixToPostfix(notation.toCharArray());
            }
            //POSTFIX TO INFIX
            else if(derivedInput == 3 && input == 1) {
                output = o.postfixToInfix(notation.toCharArray());
            }
            //POSTFIX TO PREFIX
            else if(derivedInput == 3 && input == 2) {
                output = o.postfixToPrefix(notation.toCharArray());
            }
            
            System.out.println(output);

            oFile.write(("Output String: " + output +"\n"+"\n").getBytes());

            oFile.close();
            System.out.println("Enter 1 if you want to continue, otherwise enter 0: ");
            ans = sc.nextInt();
            sc.nextLine();
        }while(ans==1);
            sc.close();
            System.out.println("Thank you");
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}