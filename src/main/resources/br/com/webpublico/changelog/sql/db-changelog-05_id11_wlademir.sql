create or replace function NIVELESTRUTURA(TEXT IN VARCHAR2,DELIMITADOR IN VARCHAR2)
RETURN NUMBER IS
qtd number;
recorte number;
temp number;
retorno number;
texto VARCHAR2(50);
BEGIN
texto:=text;
qtd:=length(REGEXP_REPLACE(texto,'[0-9]'));
temp :=0;
for temp in 1..qtd loop
  recorte:=instr(texto,'.',1);
  if length(replace(substr(texto,0, recorte - 1),'0')) > 0 then
      retorno:=temp;
  end if;
  texto:=substr(texto,recorte+1,length(texto)) ;
end loop;
if length(replace(texto,'0')) > 0 then
  retorno:=(qtd+1);
end if;
RETURN retorno;
END