insert into TEMPLATEEMAIL (ID, TIPO, ASSUNTO, CONTEUDO)
select HIBERNATE_SEQUENCE.nextval,
       'REQUERIMENTO_LICENCIAMENTO_ETR',
       'Requerimento de Licenciamento de ETR',
       '${EXERCICIO}, ${CODIGO}, ${RESPONSAVEL}, ${TELEFONE}, ${EMAIL}, ${ENDERECO}, ${LINK_VISUALIZACAO}'
from dual
where not exists(select 1 from templateemail where TIPO = 'REQUERIMENTO_LICENCIAMENTO_ETR');

insert into TEMPLATEEMAIL (ID, TIPO, ASSUNTO, CONTEUDO)
select HIBERNATE_SEQUENCE.nextval,
       'REGISTRO_EXIGENCIA_ETR',
       'Registro de Exigência de ETR',
       '${LINK}'
from dual
where not exists(select 1 from templateemail where TIPO = 'REGISTRO_EXIGENCIA_ETR');
