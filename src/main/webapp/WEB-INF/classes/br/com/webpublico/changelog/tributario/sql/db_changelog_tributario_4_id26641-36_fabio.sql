insert into sociedadepessoajuridica (id, socio_id, proporcao, dataregistro, datainicio, datafim, pessoajuridica_id)
select hibernate_sequence.nextval, soc.socio_id, soc.proporcao, soc.dataregistro, soc.datainicio, soc.datafim, ce.pessoa_id
from sociedadecadastroeconomico soc
inner join CE_SOCIEDADE cesoc on cesoc.sociedadecadastroeconomico_id = soc.id
inner join cadastroeconomico ce on ce.id = cesoc.cadastroeconomico_id
inner join pessoajuridica pj on pj.id = ce.pessoa_id
