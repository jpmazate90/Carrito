/* codigo de usuario */
package Analizadores.Flex;


import java.util.ArrayList;
import java_cup.runtime.Symbol;
import Analizadores.Cup.sym;
import javax.swing.JTextArea;
import Model.Instruccion;


%% //separador de area



 /* opciones y declaraciones de jflex */

%class Lexer
%public
%cup
%cupdebug
%line
%column


WhiteSpace = [\r|\n|\r\n] | [ \t\f]
SaltoLinea = [\n]




INSTRUCCION = "INSTRUCCION"
PARENTESIS_ABIERTO = "("
PARENTESIS_CERRADO = ")"
COMA = ","

ALTA = "ALTA"
BAJA = "BAJA"
MEDIA = "MEDIA"

ADELANTE = "ADELANTE"
ATRAS = "ATRAS"
IZQUIERDA = "IZQUIERDA"
DERECHA = "DERECHA"
PARAR = "PARAR"
ENCENDER = "ENCENDER"
APAGAR = "APAGAR"

NUMERO = [0-9]
NUMEROS = ({NUMERO})+


%{

	String lexema = "";
	String lexemaError = "";

	String lexemaMandar = "";
	JTextArea area;

	public Lexer(java.io.Reader in,JTextArea area) {
    		this.zzReader = in;
		this.area = area;

  	}
  	
  	
  	

	public void crearLexema(String mandar){
		lexema = lexema+mandar;	
	}
	public void crearLexemaMandar(String mandar){
		lexemaMandar = lexemaMandar+mandar;	
	}
	public void crearLexemaError(String mandar){
		lexemaError = lexemaError+ mandar;
	}
	public void verificarError(){
		if(!lexemaError.equals("")){
			error(lexemaError);
		}
		lexemaError="";
	}
	

  
  private Symbol symbol(int type) {
    return new Symbol(type, yyline+1, yycolumn+1);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline+1, yycolumn+1, value);
  }


  private void error(String message) {
      area.append("\nError Lexico en Fila: " + (yyline + 1) + ", columna " + (yycolumn + 1) + " : " + message + "\n");
        
  }
%}



%% /* separador de areas*/

/* reglas lexicas */
<YYINITIAL> {
{WhiteSpace} {verificarError();}

{INSTRUCCION} {verificarError();return symbol(sym.INSTRUCCION);}
{PARENTESIS_ABIERTO} {verificarError();return symbol(sym.PARENTESIS_ABIERTO);}
{PARENTESIS_CERRADO} {verificarError();return symbol(sym.PARENTESIS_CERRADO);}
{COMA} {verificarError();return symbol(sym.COMA);}
{ALTA} {verificarError();return symbol(sym.ALTA);}
{BAJA} {verificarError();return symbol(sym.BAJA);}
{MEDIA} {verificarError();return symbol(sym.MEDIA);}
{ADELANTE} {verificarError();return symbol(sym.ADELANTE);}
{ATRAS} {verificarError();return symbol(sym.ATRAS);}
{IZQUIERDA} {verificarError();return symbol(sym.IZQUIERDA);}
{DERECHA} {verificarError();return symbol(sym.DERECHA);}
{PARAR} {verificarError();return symbol(sym.PARAR);}
{ENCENDER} {verificarError();return symbol(sym.ENCENDER);}
{APAGAR} {verificarError();return symbol(sym.APAGAR);}
{NUMEROS} {verificarError();return symbol(sym.NUMEROS,yytext());}


}


[^]                     {crearLexemaError(yytext());}
<<EOF>>                 {return symbol(sym.EOF);}


