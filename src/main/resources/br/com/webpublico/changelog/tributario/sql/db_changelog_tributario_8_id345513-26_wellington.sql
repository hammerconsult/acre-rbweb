insert into grupodoctooficial (ID, CODIGO, DESCRICAO, TIPOSEQUENCIA)
select HIBERNATE_SEQUENCE.nextval, 31, 'ALVARÁ', 'SEQUENCIA'
from dual
where not exists (select 1 from GRUPODOCTOOFICIAL where DESCRICAO = 'ALVARÁ');

insert into tipodoctooficial (ID, CODIGO, DESCRICAO, GRUPODOCTOOFICIAL_ID, TIPOCADASTRODOCTOOFICIAL,
                              VALIDADEDOCTO, TIPOVALIDACAODOCTOOFICIAL,
                              CONTROLEENVIORECEBIMENTO, MODULOTIPODOCTOOFICIAL, IMPRIMIRDIRETOPDF,
                              DISPONIVELSOLICITACAOWEB, EXIGIRASSINATURA, PERMITIRIMPRESSAOSEMASSINATURA)
select HIBERNATE_SEQUENCE.nextval, (select coalesce(max(codigo), 0) + 1 from TIPODOCTOOFICIAL),
       'DECLARAÇÃO DE DISPENSA DE LICENCIAMENTO', (select id from GRUPODOCTOOFICIAL where DESCRICAO = 'ALVARÁ'),
       'CADASTROECONOMICO', 365, 'SEMVALIDACAO', 0, 'DECLARACAO_DISPENSA_LICENCIAMENTO', 0, 0, 0, 0
from dual
where not exists(select 1 from TIPODOCTOOFICIAL where DESCRICAO = 'DECLARAÇÃO DE DISPENSA DE LICENCIAMENTO');

update configuracaotributario set tipodoctodeclaracaodispensalicenciamento_id = (select id from tipodoctooficial where descricao = 'DECLARAÇÃO DE DISPENSA DE LICENCIAMENTO')
where tipodoctodeclaracaodispensalicenciamento_id is null;
