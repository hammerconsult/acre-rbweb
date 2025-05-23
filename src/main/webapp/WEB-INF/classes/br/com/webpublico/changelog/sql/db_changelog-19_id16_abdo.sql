create or replace view vwenderecopessoa as (
select pessoa.id pessoa_id,
       trim(enderecocorreio.bairro) bairro,
       trim(enderecocorreio.logradouro) logradouro,
       trim(enderecocorreio.cep) cep,
       trim(enderecocorreio.complemento) complemento,
       trim(enderecocorreio.localidade) localidade,
       trim(enderecocorreio.numero) numero,
       trim(enderecocorreio.tipoendereco) tipoendereco,
       trim(enderecocorreio.uf) uf
   from pessoa
  inner join enderecocorreio on enderecocorreio.id = (
select   endereco_id from (
select  s_pessoa.id pessoa_id, enderecocorreio.id endereco_id
   from pessoa s_pessoa
  inner join pessoa_enderecocorreio on pessoa_enderecocorreio.enderecoscorreio_id = s_pessoa.enderecoprincipal_id
  inner join enderecocorreio on enderecocorreio.id = pessoa_enderecocorreio.enderecoscorreio_id
union
select s_pessoa.id pessoa_id, enderecocorreio.id endereco_id
   from pessoa s_pessoa
  inner join pessoa_enderecocorreio on pessoa_enderecocorreio.pessoa_id = s_pessoa.id
  inner join enderecocorreio on enderecocorreio.id = pessoa_enderecocorreio.enderecoscorreio_id
where enderecocorreio.tipoendereco = 'DOMICILIO_FISCAL'
union
select  s_pessoa.id pessoa_id, enderecocorreio.id endereco_id
   from pessoa s_pessoa
  inner join pessoa_enderecocorreio on pessoa_enderecocorreio.pessoa_id = s_pessoa.id
  inner join enderecocorreio on enderecocorreio.id = pessoa_enderecocorreio.enderecoscorreio_id)
where pessoa_id = pessoa.id and rownum = 1)
)

