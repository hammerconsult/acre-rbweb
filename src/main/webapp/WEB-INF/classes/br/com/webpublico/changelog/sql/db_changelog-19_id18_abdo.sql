create or replace view vwenderecobce as (
select ec.cadastroeconomico_id,
       trim(tl.descricao) tipologradouro,
       trim(l.nome) logradouro,
       trim(ec.numero) numero,
       trim(ec.complemento) complemento,
       trim(b.descricao) bairro,
       trim(lb.cep) cep
   from enderecocadastroeconomico ec
  left join logradourobairro lb on lb.id = ec.logradourobairro_id
  left join logradouro l on l.id = lb.logradouro_id
  left join tipologradouro tl on tl.id = l.tipologradouro_id
  left join bairro b on b.id = lb.bairro_id
where ec.tipoendereco = 'COMERCIAL')