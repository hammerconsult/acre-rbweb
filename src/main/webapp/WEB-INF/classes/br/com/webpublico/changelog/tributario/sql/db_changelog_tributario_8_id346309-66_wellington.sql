insert into tipodoctooficial (id, codigo, descricao, tiposequencia, grupodoctooficial_id,
                              tipocadastrodoctooficial, tributo_id, validadedam, validadedocto,
                              valor, tipovalidacaodoctooficial, controleenviorecebimento,
                              modulotipodoctooficial, imprimirdiretopdf, disponivelsolicitacaoweb,
                              exigirassinatura, permitirimpressaosemassinatura)
select hibernate_sequence.nextval, (select max(codigo) + 1 from tipodoctooficial),
       'F.A.C - SÍMILE REGISTRO DE MARCA A FOGO', 'SEQUENCIA',
       (select id from grupodoctooficial where descricao = 'TERMOS GERAIS'),
       'NENHUM', null, null, 90, null, 'SEMVALIDACAO', 0,
       'CERTIDAO_MARCA_FOGO', 0, 0, 0, null from dual
where not exists (select 1
                  from tipodoctooficial t
                  where t.descricao = 'F.A.C - SÍMILE REGISTRO DE MARCA A FOGO')
