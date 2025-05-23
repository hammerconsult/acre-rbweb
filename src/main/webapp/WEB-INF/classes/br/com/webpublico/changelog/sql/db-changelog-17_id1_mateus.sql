CREATE or replace FUNCTION FORMATACPFCNPJ(valor IN VARCHAR2) 
RETURN VARCHAR2
IS retorno VARCHAR2(20);
cpfcnpj VARCHAR2(20) := replace(replace(replace(valor, '.', ''), '-', ''), '/', '');
BEGIN 
  SELECT case length(cpfcnpj)
            when 14 then substr(cpfcnpj, 0, 2) || '.' || substr(cpfcnpj, 3, 3) || '.' || substr(cpfcnpj, 6, 3) || '/' ||substr(cpfcnpj, 9, 4) || '-' || substr(cpfcnpj, 13, 2)
            when 11 then substr(cpfcnpj, 0, 3) || '.' || substr(cpfcnpj, 4, 3) || '.' || substr(cpfcnpj, 7, 3) || '-' ||substr(cpfcnpj, 10, 2)
            else valor
         end as ret
  into retorno
  FROM dual ; 
  RETURN(retorno); 
END;    