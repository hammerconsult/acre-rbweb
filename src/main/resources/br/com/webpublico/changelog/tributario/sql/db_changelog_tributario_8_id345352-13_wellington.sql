create
or replace function func_rbt12(p_data in date, p_prestador in numeric) return numeric
is
    v_meses_atividade integer;
    v_total_servicos
numeric;
begin
select round(months_between(to_date(to_char(current_date, 'MM/yyyy'), 'MM/yyyy'),
                            to_date(to_char(ce.abertura, 'MM/yyyy'), 'MM/yyyy')), 0) - 1
into v_meses_atividade
from cadastroeconomico ce
where ce.id = p_prestador;

select coalesce(sum(dec.totalservicos), 0)
into v_total_servicos
from notafiscal nf
         inner join declaracaoprestacaoservico dec
on dec.id = nf.declaracaoprestacaoservico_id
where dec.situacao != 'CANCELADA'
  and nf.prestador_id = p_prestador
  and to_date(to_char(dec.competencia
    , 'MMyyyy')
    , 'MMyyyy')
    between to_date(to_char(add_months(p_data
    , -12)
    , 'MMyyyy')
    , 'MMyyyy')
  and to_date(to_char(add_months(p_data
    , -1)
    , 'MMyyyy')
    , 'MMyyyy');

if
(v_meses_atividade < 1) then
        return 0.0;
    elsif
(v_meses_atividade = 1) then
        return v_total_servicos * 12;
    elsif
(v_meses_atividade < 12) then
        return (v_total_servicos/v_meses_atividade) * 12;
else
        return v_total_servicos;
end if;
end;
