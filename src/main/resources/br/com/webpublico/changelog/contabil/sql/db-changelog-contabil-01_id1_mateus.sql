update alteracaoorc al set al.tipodespesaorc = (
select sup.tipodespesaorc from alteracaoorc alt
inner join suplementacaoorc sup on sup.alteracaoorc_id = alt.id
where rownum = 1 and al.id = alt.id)
