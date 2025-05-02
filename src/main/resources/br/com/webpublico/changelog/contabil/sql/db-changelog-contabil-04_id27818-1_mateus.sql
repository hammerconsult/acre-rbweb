update TransporteDeSaldoAbertura transp set transp.data = (
select afe.datageracao from TransporteDeSaldoAbertura tsa
inner join ABERTURAFECHAEXERCICIO afe on tsa.ABERTURAFECHAMENTOEXERCICIO_ID = afe.id
where transp.id = tsa.id
)
