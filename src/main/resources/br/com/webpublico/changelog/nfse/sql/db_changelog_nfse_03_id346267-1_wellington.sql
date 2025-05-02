declare
texto clob;
begin
for registro in (select nf.id, nf.INFORMACOESADICIONAIS
                     from notafiscal nf
                              inner join declaracaoprestacaoservico dec on dec.id = nf.DECLARACAOPRESTACAOSERVICO_ID
                              inner join itemdeclaracaoservico ids on ids.DECLARACAOPRESTACAOSERVICO_ID = dec.ID
                              inner join servico s on s.id = ids.SERVICO_ID
                     where dec.NATUREZAOPERACAO != 'TRIBUTACAO_FORA_MUNICIPIO'
                       and nf.INFORMACOESADICIONAIS like '%Serviço tributado no município%'
                       and nf.informacoesadicionais not like '%Serviço tributado no município: RIO BRANCO - AC.%'
                       and coalesce(s.PERMITERECOLHIMENTOFORA, 0) = 0)
    loop
        texto := substr(registro.informacoesadicionais, instr(registro.informacoesadicionais, 'Serviço tributado no município', 1));
        texto
:= substr(texto, 0, instr(texto, '.', 1));
update notafiscal
set INFORMACOESADICIONAIS = replace(informacoesadicionais, texto, 'Serviço tributado no município: RIO BRANCO - AC.')
where id = registro.id;
end loop;
end;
