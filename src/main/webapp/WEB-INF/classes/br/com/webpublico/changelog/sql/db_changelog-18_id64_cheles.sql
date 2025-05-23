create or replace view vwbem
(bem_id, identificacao_patrimonial, descricao, valororiginal, valoracumuladodaamortizacao, valoracumuladodadepreciacao, valoracumuladodaexaustao, valoracumuladodeajuste, id_ultimo_estado_bem, id_ultimo_evento_bem)
as (
select b.id as bem_id,
       b.identificacao as identificacao_patrimonial,
       b.descricao,
       coalesce(resultante.valororiginal, 0) as valororiginal,
       coalesce(resultante.valoracumuladodaamortizacao, 0) as valoracumuladodaamortizacao,
       coalesce(resultante.valoracumuladodadepreciacao, 0) as valoracumuladodadepreciacao,
       coalesce(resultante.valoracumuladodaexaustao, 0) as valoracumuladodaexaustao,
       coalesce(resultante.valoracumuladodeajuste, 0) as valoracumuladodeajuste,
       resultante.id as id_ultimo_estado_bem,
       eb.id as id_ultimo_evento_bem
from bem b
inner join eventobem eb on eb.bem_id = b.id
inner join estadobem resultante on resultante.id = eb.estadoresultante_id
where eb.id = (select max(eb2.id) from eventobem eb2 where eb2.bem_id = b.id)
)