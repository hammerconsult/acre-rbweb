update alteracaoorc al set al.tipodespesaorc = (
select anu.tipodespesaorc from alteracaoorc alt
inner join anulacaoorc anu on anu.alteracaoorc_id = alt.id
where rownum = 1 and al.id = alt.id)
where al.tipodespesaorc is null