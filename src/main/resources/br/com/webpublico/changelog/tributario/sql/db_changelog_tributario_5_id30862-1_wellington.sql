create or replace function funcMesToNumero(mes varchar) return integer as
begin
    return case mes
               when 'JANEIRO' then 1
               when 'FEVEREIRO' then 2
               when 'MARCO' then 3
               when 'ABRIL' then 4
               when 'MAIO' then 5
               when 'JUNHO' then 6
               when 'JULHO' then 7
               when 'AGOSTO' then 8
               when 'SETEMBRO' then 9
               when 'OUTUBRO' then 10
               when 'NOVEMBRO' then 11
               when 'DEZEMBRO' then 12
           end;
end;
