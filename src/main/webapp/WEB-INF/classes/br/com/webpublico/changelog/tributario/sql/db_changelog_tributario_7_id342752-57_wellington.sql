insert into TEMPLATEEMAIL (ID, TIPO, ASSUNTO, CONTEUDO)
select HIBERNATE_SEQUENCE.nextval,
       'SOLICITACAO_ALVARA_IMEDIATO',
       'Solicitação de Alvará Imediato',
       '${EXERCICIO}, ${CODIGO}, ${LINK_VISUALIZACAO}'
from dual
where not exists(select 1 from templateemail where TIPO = 'SOLICITACAO_ALVARA_IMEDIATO');

insert into TEMPLATEEMAIL (ID, TIPO, ASSUNTO, CONTEUDO)
select HIBERNATE_SEQUENCE.nextval,
       'REGISTRO_EXIGENCIA_ALVARA_IMEDIATO',
       'Registro de Exigência de Alvará Imediato',
       '${LINK}'
from dual
where not exists(select 1 from templateemail where TIPO = 'REGISTRO_EXIGENCIA_ALVARA_IMEDIATO');
