create or replace view vwenderecobce as
(
select cmc.id as cadastroeconomico_id,
       trim(vw.tipologradouro) as tipologradouro,
       trim(vw.logradouro) as logradouro,
       trim(vw.numero) as numero,
       trim(vw.complemento) as complemento,
       trim(vw.bairro) as bairro,
       trim(vw.cep) as cep
from vwenderecopessoa vw
         inner join cadastroeconomico cmc on vw.pessoa_id = cmc.pessoa_id)

