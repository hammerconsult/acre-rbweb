create or replace view vwenderecopessoa as (
select pes.id as pessoa_id,
       ec.id as endereco_id,
       trim(ec.bairro) as bairro,
       trim(ec.logradouro) as logradouro,
       trim(ec.cep) as cep,
       trim(ec.complemento) as complemento,
       trim(ec.localidade) as localidade,
       trim(ec.numero) as numero,
       trim(ec.tipoendereco) as tipoendereco,
       trim(ec.tipologradouro) as tipologradouro,
       trim(ec.uf) as uf
from pessoa pes
         inner join enderecocorreio ec on coalesce(pes.enderecoprincipal_id,
                                                   (select pec1.enderecoscorreio_id from pessoa_enderecocorreio pec1
                                                    inner join enderecocorreio ec2 on pec1.enderecoscorreio_id = ec2.id and pec1.pessoa_id = pes.id
                                                    order by case when ec2.tipoendereco = 'DOMICILIO_FISCAL' then 1 end fetch first 1 rows only)) = ec.id
)
