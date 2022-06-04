#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <ctype.h>

char getLine[10000];
int i;
// 토큰 구조체 생성
typedef struct Token
{
    char category[1000];
    char string[10000];
    bool isComment;
    bool isBlank;
} Token;

Token get_Token()
{
    // return 할 토큰 생성
    Token newToken = {'\0', '\0', false, false};
    char temp;
    if (getLine[i] == '\0')
    {
        newToken.isBlank = true;
        return newToken;
    }
    // 주석
    else if (getLine[i] == '/')
    {
        if (getLine[i + 1] == '/')
        {
            newToken.isComment = true;
            while (getLine[i] != '\0')
            {
                i++;
                return newToken;
            }
        }
        else
        {
            strcpy(newToken.string, "/");
            i = i + 1;
            return newToken;
        }
    }
    else if (getLine[i] == '"')
    {
        temp = getLine[i];
        strcpy(newToken.string, &temp);
        i++;
        while (getLine[i] != '"')
        {
            temp = getLine[i];
            strcat(newToken.string, &temp);
            i++;
        }
        temp = getLine[i];
        strcat(newToken.string, &temp);
        i++;
        strcpy(newToken.category, "charLiteral ");
        return newToken;
    }
    
    // 공백 & ,
    if (getLine[i] == ' ' || getLine[i] == ',')
    {
        i++;
        newToken.isBlank = true;
        return newToken;
    }

    // operator
    else if (getLine[i] == '=')
    {
        if (getLine[i + 1] == '=')
        {
            strcpy(newToken.string, "==");
            i = i + 2;
            return newToken;
        }
        else
        {
            strcpy(newToken.string, "=");
            i = i + 1;
            return newToken;
        }
    }
    else if (getLine[i] == '|')
    {
        if (getLine[i + 1] == '|')
        {
            strcpy(newToken.string, "||");
            i = i + 2;
            return newToken;
        }
        else
        {
            i = i + 1;
            return newToken;
        }
    }
    else if (getLine[i] == '&')
    {
        if (getLine[i + 1] == '&')
        {
            strcpy(newToken.string, "&&");
            i = i + 2;
            return newToken;
        }
        else
        {
            i = i + 1;
            return newToken;
        }
    }
    else if (getLine[i] == '!')
    {
        if (getLine[i + 1] == '=')
        {
            strcpy(newToken.string, "!=");
            i = i + 2;
            return newToken;
        }
        else
        {
            strcpy(newToken.string, "!");
            i = i + 1;
            return newToken;
        }
    }
    else if (getLine[i] == '<')
    {
        if (getLine[i + 1] == '=')
        {
            strcpy(newToken.string, "<=");
            i = i + 2;
            return newToken;
        }
        else
        {
            strcpy(newToken.string, "<");
            i = i + 1;
            return newToken;
        }
    }
    else if (getLine[i] == '>')
    {
        if (getLine[i + 1] == '=')
        {
            strcpy(newToken.string, ">=");
            i = i + 2;
            return newToken;
        }
        else
        {
            strcpy(newToken.string, ">");
            i = i + 1;
            return newToken;
        }
    }

    else if (getLine[i] == '+')
    {
        if (getLine[i + 1] == '+')
        {
            strcpy(newToken.string, "++");
            i = i + 2;
            return newToken;
        }
        else
        {
            strcpy(newToken.string, "+");
            i = i + 1;
            return newToken;
        }
    }
    else if (getLine[i] == '-')
    {
        if (getLine[i + 1] == '-')
        {
            strcpy(newToken.string, "--");
            i = i + 2;
            return newToken;
        }
        else
        {
            strcpy(newToken.string, "-");
            i = i + 1;
            return newToken;
        }
    }
    else if (getLine[i] == '*')
    {
        if (getLine[i + 1] == '*')
        {
            strcpy(newToken.string, "**");
            i = i + 2;
            return newToken;
        }
        else
        {
            strcpy(newToken.string, "*");
            i = i + 1;
            return newToken;
        }
    }
    else if (getLine[i] == '[')
    {
        strcpy(newToken.string, "[");
        i = i + 1;
        return newToken;
    }
    else if (getLine[i] == ']')
    {
        strcpy(newToken.string, "]");
        i = i + 1;
        return newToken;
    }
    // punctuation
    else if (getLine[i] == ';')
    {
        strcpy(newToken.string, ";");
        i = i + 1;
        return newToken;
    }
    else if (getLine[i] == '(')
    {
        strcpy(newToken.string, "(");
        i = i + 1;
        return newToken;
    }
    else if (getLine[i] == ')')
    {
        strcpy(newToken.string, ")");
        i = i + 1;
        return newToken;
    }
    else if (getLine[i] == '{')
    {
        strcpy(newToken.string, "{");
        i = i + 1;
        return newToken;
    }
    else if (getLine[i] == '}')
    {
        strcpy(newToken.string, "}");
        i = i + 1;
        return newToken;
    }
    else if (getLine[i] == ',')
    {
        strcpy(newToken.string, ",");
        i = i + 1;
        return newToken;
    }
    else if (getLine[i] == ':')
    {
        strcpy(newToken.string, ":");
        i = i + 1;
        return newToken;
    }
    else if (getLine[i] == '.')
    {
        strcpy(newToken.string, ".");
        i = i + 1;
        return newToken;
    }
    else if (getLine[i] == '$')
    {
        strcpy(newToken.string, "$");
        i = i + 1;
        return newToken;
    }
    //letter [Keyword, Id, char]
    else if (isalpha(getLine[i]))
    {
        if (getLine[i] == 'b' && getLine[i+1] == 'o' && getLine[i+2] == 'o' && getLine[i+3] == 'l')
        {
            strcpy(newToken.category, "bool");
            i = i + 4;
            return newToken;
        }
        else if (getLine[i] == 'c' && getLine[i+1] == 'h' && getLine[i+2] == 'a' && getLine[i+3] == 'r')
        {
            strcpy(newToken.category, "char");
            i = i + 4;
            return newToken;
        }
        else if (getLine[i] == 'i' && getLine[i+1] == 'n' && getLine[i+2] == 't')
        {
            strcpy(newToken.category, "Int");
            i = i + 3;
            return newToken;
        }
        else if (getLine[i] == 'i' && getLine[i+1] == 'f')
        {
            strcpy(newToken.category, "if");
            i = i + 2;
            return newToken;
        }
        else if (getLine[i] == 'e' && getLine[i+1] == 'l' && getLine[i+2] == 's' && getLine[i+3] == 'e')
        {
            strcpy(newToken.category, "else");
            i = i + 4;
            return newToken;
        }
        else if (getLine[i] == 'w' && getLine[i+1] == 'h' && getLine[i+2] == 'i' && getLine[i+3] == 'l' && getLine[i+4] == 'e')
        {
            strcpy(newToken.category, "while");
            i = i + 5;
            return newToken;
        }
        else if (getLine[i] == 'f' && getLine[i+1] == 'a' && getLine[i+2] == 'l' && getLine[i+3] == 's' && getLine[i+4] == 'e')
        {
            strcpy(newToken.category, "false");
            i = i + 5;
            return newToken;
        }
        else if (getLine[i] == 'f' && getLine[i+1] == 'l' && getLine[i+2] == 'o' && getLine[i+3] == 'a' && getLine[i+4] == 't')
        {
            strcpy(newToken.category, "float");
            i = i + 5;
            return newToken;
        }
        else if (getLine[i] == 'm' && getLine[i+1] == 'a' && getLine[i+2] == 'i' && getLine[i+3] == 'n')
        {
            strcpy(newToken.category, "main");
            i = i + 4;
            return newToken;
        }
        else if (getLine[i] == 'f' && getLine[i+1] == 'o' && getLine[i+2] == 'r')
        {
            strcpy(newToken.category, "for");
            i = i + 3;
            return newToken;
        }
        

        temp = getLine[i];
        strcat(newToken.string, &temp);
        i++;
        while (isalpha(getLine[i]) || isdigit(getLine[i]) || getLine[i] == '.')
        {
            temp = getLine[i];
            strcat(newToken.string, &temp);
            i++;
        }
        strcpy(newToken.category, "Id ");
        return newToken;
    }
    else if(isdigit(getLine[i]))
    {
        bool isFloat = false;
        while (isdigit(getLine[i]))
        {
            temp = getLine[i];
            strcat(newToken.string, &temp);
            i++;
            if(getLine[i] == '.')
            {
                isFloat = true;
                temp = getLine[i];
                strcat(newToken.string, &temp);
                i++;
                while (isdigit(getLine[i]))
                {
                    temp = getLine[i];
                    strcat(newToken.string, &temp);
                    i++;
                }
                break;
            }
        }
        if (isFloat){
            strcpy(newToken.category, "FloatLiteral ");
        }
        else{
            strcpy(newToken.category, "IntLiteral ");
        }
        return newToken;
    }
    

    else
    {
        while (getLine[i] != '\0')
        {
            i++;
        }
        return newToken;
    }
    // input값을 하나씩 읽어가며, 정해진 Token 형식이 나오면
    // 정해진 형식에 따라 category 저장 하며, return newToken
}

int main()
{
    // 파일 열기 읽기만 해도 됨
    FILE *file = fopen("input.txt", "r");
    int lineNum;
    lineNum = 1;

    // input.txt를 입력 받고 한줄씩 읽기
    if (file != NULL)
    {
        // 읽은 1라인의 값들을 Line 1 // this is a ... 형식으로 출력
        while (fgets(getLine, 10000, file) != NULL)
        {
            printf("Line %d ", lineNum++);
            fputs(getLine, stdout);
            i = 0;
            // 한줄에 입력된 한 문자 씩 읽기
            while (getLine[i] != '\0')
            {
                // get_Token 함수를 통해 토큰 값 자르기
                Token token = get_Token();
                // // 카테고리와 문자열 출력
                if (token.isComment)
                { // 주석 부분이면 출력 X
                    break;
                }
                else if (token.isBlank) // 출력 할 필요 없을 때 (공백 , \0)
                {
                    continue;
                }
                else
                {
                    printf("%s", token.category);
                    printf("%s\n", token.string);
                }
            }
        }
    }

    // 파일 닫기
    fclose(file);
    return 0;
}