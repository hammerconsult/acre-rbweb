alter table cadastroeconomico add areautizacaobkp numeric(19,2);

update cadastroeconomico set areautizacaobkp = (select areautilizacao
                                                  from enderecocadastroeconomico
                                               where cadastroeconomico_id = cadastroeconomico.id
                                                 and tipoendereco = 'COMERCIAL'
                                               order by id
                                               fetch first 1 row only);

delete from enderecocadastroeconomico where tipoendereco = 'COMERCIAL' or TIPOENDERECO is null;

insert into enderecocadastroeconomico (ID, CADASTROECONOMICO_ID, NUMERO, COMPLEMENTO,
                                       TIPOENDERECO, AREAUTILIZACAO, SEQUENCIAL, CEP,
                                       LOGRADOURO, BAIRRO, LOCALIDADE, UF)
select HIBERNATE_SEQUENCE.nextval,
       ce.id, vwep.NUMERO, vwep.COMPLEMENTO, 'COMERCIAL',
       ce.areautizacaobkp, 0, vwep.cep, vwep.LOGRADOURO, vwep.bairro, vwep.LOCALIDADE, vwep.uf
   from cadastroeconomico ce
  left join vwenderecopessoa vwep on vwep.pessoa_id = ce.pessoa_id;

alter table cadastroeconomico drop column areautizacaobkp;
