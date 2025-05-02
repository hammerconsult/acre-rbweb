update EVENTOFPEMPREGADOR
set INCIDENCIATRIBUTARIAIRRF_ID = null
where INCIDENCIATRIBUTARIAIRRF_ID in (select id
                                      from IncidenciaTributariaIRRF
                                      where codigo in ('00', '01', '15', '35', '44', '55', '78', '81', '82', '83', '91', '92', '93', '94', '95'))
